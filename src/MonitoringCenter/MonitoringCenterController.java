package MonitoringCenter;

import CAQ.*;
import CAQ.MonitoringCenter;
import LocalServer.LocalServer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static MonitoringCenter.MonitoringCenter.getOrb;


public class MonitoringCenterController implements Initializable {

    private static final ObservableList<TableReading> readingsList = FXCollections.observableArrayList ();
    private static final ObservableList<TableLS> lsList = FXCollections.observableArrayList();
    private static final ObservableList<TableAgency> agenciesList = FXCollections.observableArrayList();
    private static final ObservableList<TableMS> msList = FXCollections.observableArrayList();

    @FXML
    private ListView<String> lsListView;
    @FXML
    private Button getReadingBtn, viewStationsBTN;
    @FXML
    private Text readingsTitle;

    @FXML
    private TableView<TableReading> readingsTable;
    @FXML
    private TableColumn<TableReading, String> stationNameCol;
    @FXML
    private TableColumn<TableReading, Integer> readingCol;
    @FXML
    private TableColumn<TableReading, String> dateCol;
    @FXML
    private TableColumn<TableReading, String> timeCol;

    @FXML
    private TableView<TableMS> msTable;
    @FXML
    private TableColumn<TableMS, String> msNameCol;
    @FXML
    private TableColumn<TableMS, String> msLocationCol;
    @FXML
    private TableColumn<TableMS, Boolean> msStatusCol;

    @FXML
    private TableView<TableAgency> agencyTable;
    @FXML
    private TableColumn<TableAgency, String> aNameCol;
    @FXML
    private TableColumn<TableAgency, String> aLocationCol;
    @FXML
    private TableColumn<TableAgency, String> aContactInfoCol;

    @FXML
    private TableView<TableLS> lsTable;
    @FXML
    private TableColumn<TableLS, String> lsNameCol;
    @FXML
    private TableColumn<TableLS, String> lsLocationCol;


    private static ORB orb = getOrb();
    private static NamingContextExt nameService;

    //static private String lsName;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        //Readings Table
        stationNameCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("station_name"));
        readingCol.setCellValueFactory(new PropertyValueFactory<TableReading,Integer>("reading_value"));
        dateCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("time"));
        readingsTable.setItems(readingsList);
        //ls Table
        lsNameCol.setCellValueFactory(new PropertyValueFactory<TableLS,String>("name"));
        lsLocationCol.setCellValueFactory(new PropertyValueFactory<TableLS,String>("location"));
        lsTable.setItems(lsList);
        //MS Table
        msNameCol.setCellValueFactory(new PropertyValueFactory<TableMS,String>("name"));
        msLocationCol.setCellValueFactory(new PropertyValueFactory<TableMS,String>("location"));
        msStatusCol.setCellValueFactory(new PropertyValueFactory<TableMS, Boolean>("status"));
        msTable.setItems(msList);
        //Agency Table
        aNameCol.setCellValueFactory(new PropertyValueFactory<TableAgency,String>("name"));
        aLocationCol.setCellValueFactory(new PropertyValueFactory<TableAgency,String>("locationOfInterest"));
        aContactInfoCol.setCellValueFactory(new PropertyValueFactory<TableAgency, String>("contactInfo"));
        agencyTable.setItems(agenciesList);


    }

    public static void updateLocalServerList()
    {
        Platform.runLater(() -> {
            List<Station> stationList = MonitoringCenterImpl.getLocalServerList();
            lsList.clear();
            for(Station s: stationList) {
                TableLS ls = new TableLS(s.name, s.location);
                lsList.add(ls);
            }
        });
    }

    public static void updateReadings()
    {
        Platform.runLater(() -> {
            List<NoxReading> collectedReadings = MonitoringCenterImpl.getReadingsList();
            List<TableReading> readings = new ArrayList<>();
            for (NoxReading reading : collectedReadings) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat tf= new SimpleDateFormat("hh:mm:ss");
                TableReading readingToAdd = new TableReading(tf.format(reading.time), df.format(reading.date), reading.reading_value, reading.station_name);
                readings.add(readingToAdd);
            }
            readingsList.clear();
            readingsList.addAll(readings);
        });
    }

    public static void updateMonitoringStations()
    {
        Platform.runLater(() -> {
            List<Station> stationList = MonitoringCenterImpl.getMonitoringStationList();
            msList.clear();
            for(Station s: stationList) {
                TableMS item = new TableMS(s.name,s.location,true);
                msList.add(item);
            }
        });
    }

    public static void updateAgencies()
    {
        Platform.runLater(() -> {
            List<Agency> listToAdd = MonitoringCenterImpl.getAgenciesList();
            agenciesList.clear();
            for(Agency a: listToAdd) {
                TableAgency agency = new TableAgency(a.name,a.locationOfInterest,a.contactNumber);
                agenciesList.add(agency);
            }
        });
    }


    @FXML
    private void getReading(ActionEvent event) throws IOException {
        String lsName = lsListView.getSelectionModel().getSelectedItem();
        try {

            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            // resolve the Count object reference in the Naming service
            String name = "MonitoringCenter";
            MonitoringCenter msServant = MonitoringCenterHelper.narrow(nameService.resolve_str(name));
            msServant.takeReadings(lsName);

        } catch(Exception e) {
            System.err.println("Exception");
            System.err.println(e);
        }
        readingsTitle.setText("Readings From " + lsName);
        updateReadings();
    }

    public static void raiseAlarm(NoxReading reading, String lsName){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("High Reading");
            String  toAdd =  reading.station_name + " has received a high reading of: " + reading.reading_value;
            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText(toAdd);
            alert.show();
        });

        /*for (TableAgency agencyToCheck : agenciesList) {
            {
                if (agencyToCheck.locationOfInterest.equals(lsName)) {
                    try {
                        AgencyMonitor agencyServant = AgencyMonitorHelper.narrow(nameService.resolve_str(agencyToCheck.name));
                        agencyServant.raise_alarm(reading);
                    } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                        cannotProceed.printStackTrace();
                    }
                }
            }
        }*/
    }

    //View Stations
    @FXML
    private void ViewStations(ActionEvent event) throws IOException {
        System.out.println(msList);
        for (int i = 0; i < msList.size(); i++) {
            System.out.println(msList.get(i));
        }
    }

    @FXML
    private void enable(ActionEvent event) throws IOException {
        try{
            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            MonitoringStation msServant = MonitoringStationHelper.narrow(nameService.resolve_str(getSelectedName()));
            msServant.activate();
        } catch (CannotProceed | InvalidName | NotFound | org.omg.CORBA.ORBPackage.InvalidName cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    @FXML
    private void disable(ActionEvent event) throws IOException {
        try{
            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            MonitoringStation msServant = MonitoringStationHelper.narrow(nameService.resolve_str(getSelectedName()));
            msServant.deactivate();
        } catch (CannotProceed | InvalidName | NotFound | org.omg.CORBA.ORBPackage.InvalidName cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    private String getSelectedName(){
        TableMS selectedStation = msTable.getSelectionModel().getSelectedItem();
        //Updates GUI
        int itemIndex = msTable.getSelectionModel().getSelectedIndex();
        msList.get(itemIndex).setStatus(!msList.get(itemIndex).status);
        msTable.refresh();
        return selectedStation.Name;
    }
}

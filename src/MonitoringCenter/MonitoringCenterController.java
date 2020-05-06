package MonitoringCenter;
import CAQ.*;
import CAQ.MonitoringCenter;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static MonitoringCenter.MonitoringCenter.getOrb;


public class MonitoringCenterController implements Initializable {

    private static final ObservableList<TableReading> readingsList = FXCollections.observableArrayList ();
    private static final ObservableList<String> lsList = FXCollections.<String>observableArrayList();
    ObservableList<String> msList = FXCollections.observableArrayList();

    @FXML
    private ListView<String> lsListView;
    @FXML
    private ListView<String> msListView;
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


    private ORB orb = getOrb();
    private NamingContextExt nameService;

    static private String lsName;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsListView.setItems(lsList);
        //Readings Table
        stationNameCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("station_name"));
        readingCol.setCellValueFactory(new PropertyValueFactory<TableReading,Integer>("reading_value"));
        dateCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<TableReading,String>("time"));

        readingsTable.setItems(readingsList);
    }

    public static void updateLocalServerList()
    {
        Platform.runLater(() -> {
            List<Station> stationList = MonitoringCenterImpl.getLocalServerList();
            lsList.clear();
            for(Station s: stationList) {
                lsList.add(s.name);
            }
        });
    }
    public static void updateReadings()
    {
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

    public static void raiseAlarm(NoxReading reading){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("High Reading");

            String  toAdd =  reading.station_name + " has received a high reading of: " + reading.reading_value;

            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText(toAdd);

            alert.show();
        });
    }

    //View Stations
    @FXML
    private void ViewStations(ActionEvent event) throws IOException {
        lsName = lsListView.getSelectionModel().getSelectedItem();
        //StationsController.setLSName(lsName);
        try {
            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj = orb.resolve_initial_references("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
        try {
            RegionalCentre lsServant = RegionalCentreHelper.narrow(nameService.resolve_str(lsName));
            lsServant.stations();
            ArrayList<Station> stations = new ArrayList<>(Arrays.asList(lsServant.stations()));
            for (Station station : stations) {
                //TableReading ms = new TableReading(station.name, station.location);
                //msList.add(ms);
            }

            //nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
           // locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));

            //readingsTable.setItems(msList);
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    @FXML
    private void enable(ActionEvent event) throws IOException {
        try{
            CAQ.MonitoringStation msServant = MonitoringStationHelper.narrow(nameService.resolve_str(getSelectedName()));
            msServant.activate();
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    @FXML
    private void disable(ActionEvent event) throws IOException {
        try{
            CAQ.MonitoringStation msServant = MonitoringStationHelper.narrow(nameService.resolve_str(getSelectedName()));
            msServant.deactivate();
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    private String getSelectedName(){
        if (readingsTable.getSelectionModel().getSelectedItem() != null) {
            TableReading selectedStation = readingsTable.getSelectionModel().getSelectedItem();
            //return selectedStation.Name;
        }
        return null;
    }


}

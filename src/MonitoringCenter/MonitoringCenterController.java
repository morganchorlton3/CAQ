package MonitoringCenter;
import CAQ.*;
import CAQ.MonitoringCenter;
import MonitoringStation.MonitoringStation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static MonitoringCenter.MonitoringCenter.getOrb;


public class MonitoringCenterController implements Initializable {

    private static final ObservableList<String> readingsList = FXCollections.observableArrayList ();
    private static final ObservableList<String> lsList = FXCollections.<String>observableArrayList();

    @FXML
    private ListView<String> lsListView;
    @FXML
    private ListView<String> readingsListView;
    @FXML
    private Button getReadingBtn, viewStationsBTN;
    @FXML
    private Text readingsTitle;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsListView.setItems(lsList);
        readingsListView.setItems(readingsList);
//        getReadingBtn.disableProperty().bind(lsListView.getSelectionModel().selectedItemProperty().isNull());
        //getReadingBtn.disableProperty().bind(readingsListView.getSelectionModel().selectedItemProperty().isNull());
    }

    public static void updateLocalServerList()
    {
        List<String> stationList = MonitoringCenterImpl.getLocalServerList();
        lsList.clear();
        lsList.addAll(stationList);
    }
    public static void updateReadings()
    {
        List<NoxReading> collectedReadings = MonitoringCenterImpl.getReadingsList();
        List<String> readings = new ArrayList<>();
        for (NoxReading reading : collectedReadings) {
            String readingToAdd = "Monitoring Station: " + reading.station_name + " Reading: " + reading.reading_value;
            readings.add(readingToAdd);
        }
        readingsList.clear();
        readingsList.addAll(readings);
    }


    @FXML
    private void getReading(ActionEvent event) throws IOException {
        String lsName = lsListView.getSelectionModel().getSelectedItem();
        try {
            //get ORB
            ORB orb = getOrb();

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

            // resolve the Count object reference in the Naming service
            String name = "MonitoringCenter";
            MonitoringCenter counter = MonitoringCenterHelper.narrow(nameService.resolve_str(name));
            counter.takeReadings(lsName);

        } catch(Exception e) {
            System.err.println("Exception");
            System.err.println(e);
        }
        readingsTitle.setText("Readings From " + lsName);
        updateReadings();
    }

    @FXML
    private void ViewStations(ActionEvent event) throws IOException {
        String lsName = lsListView.getSelectionModel().getSelectedItem();
        StationsController.setLSName(lsName);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MonitoringCenter/Stations.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Monitoring Stations");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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


}

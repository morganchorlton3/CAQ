package MonitoringCenter;
import CAQ.NoxReading;
import CAQ.RegionalCentre;
import CAQ.RegionalCentreHelper;
import MonitoringStation.MonitoringStation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class MonitoringCenterController implements Initializable {

    private static final ObservableList<String> readings = FXCollections.observableArrayList ();
    private static final ObservableList<String> lsList = FXCollections.<String>observableArrayList();

    @FXML
    private ListView<String> lsListView;
    @FXML
    private ListView<String> readingList;

    @FXML
    private Button updateBtn, getReadingBtn;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsListView.setItems(lsList);
    }

    public static void updateLocalServerList()
    {
        List<String> stationList = MonitoringCenterImpl.getLocalServerList();
        lsList.clear();
        lsList.addAll(stationList);
    }

    @FXML
    private void updateBtn(ActionEvent event) throws IOException {
        updateLocalServerList();
    }

    @FXML
    private void getReading(ActionEvent event) throws IOException{

        /*List<NoxReading> stationList = MonitoringCenterImpl.getReadings();
        ObservableList<String> SList = FXCollections.<String>observableArrayList();
        for (NoxReading reading : stationList) {
            String readingToAdd = "MS Name : " + reading.station_name + " Reading: " + reading.reading_value;
            SList.addAll(readingToAdd);
        }
        readingList.getItems().clear();
        readingList.getItems().addAll(SList);*/
        List<NoxReading> stationList = MonitoringCenterImpl.getReadings();
        for (NoxReading reading : stationList) {
            String readingToAdd = "MS Name : " + reading.station_name + " Reading: " + reading.reading_value;
            readings.addAll(readingToAdd);
        }
        readingList.setItems(readings);
    }


}

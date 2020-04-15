package MonitoringCenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.jacorb.orb.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;


public class MonitoringCenterController implements Initializable {

    @FXML
    private ListView<String> listView;
    private Set<String> stringSet;
    ObservableList observableList = FXCollections.observableArrayList();

    @FXML
    private Button updateBtn, getReadingBtn;


    @Override
    public void initialize(URL url, ResourceBundle rb){
    }

    public void updateLocalServerList()
    {
        List<String> stationList = MonitoringCenterImpl.getLocalServerList();
        ObservableList<String> seasonList = FXCollections.<String>observableArrayList();
        seasonList.addAll(stationList);
        System.out.println("LIST");
        System.out.println(seasonList.toString());
        listView.getItems().clear();
        listView.getItems().addAll(seasonList);
    }

    @FXML
    private void updateBtn(ActionEvent event) throws IOException {
        updateLocalServerList();
    }

    @FXML
    private void getReading(ActionEvent event) throws IOException {
        String selectedLot = (String) listView.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        //manipulate String
        System.out.println(selectedLot);


    }
}

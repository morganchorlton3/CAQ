package MonitoringCenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


public class MonitoringCenterController implements Initializable {

    @FXML
    private ListView listView;
    private Set<String> stringSet;
    ObservableList observableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle rb){
        loadData();
    }

    private void loadData()
    {

      // listView.getItems().addAll(list);
    }
}

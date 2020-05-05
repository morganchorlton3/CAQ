package MonitoringCenter;

import CAQ.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class StationsController implements Initializable {

    ObservableList<MonitoringStation> msList = FXCollections.observableArrayList();

    @FXML
    private ListView<String> stationsListView;

    @FXML
    private TableView<MonitoringStation> msTable;

    @FXML
    private TableColumn<MonitoringStation, String> nameCol;

    @FXML
    private TableColumn<MonitoringStation, String> locationCol;


    private ORB orb = MonitoringCenter.getOrb();
    private NamingContextExt nameService;
    static private String lsName;

    public static void setLSName(String name){
        lsName = name;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
                MonitoringStation ms = new MonitoringStation(station.name, station.location);
                msList.add(ms);
            }

            nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));

            msTable.setItems(msList);
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
        if (msTable.getSelectionModel().getSelectedItem() != null) {
            MonitoringStation selectedStation = msTable.getSelectionModel().getSelectedItem();
            return selectedStation.Name;
        }
        return null;
    }
}

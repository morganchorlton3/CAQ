package MonitoringCenter;

import CAQ.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class StationsController implements Initializable {

    ObservableList<TableReading> msList = FXCollections.observableArrayList();


    @FXML
    private TableView<TableReading> msTable;

    @FXML
    private TableColumn<TableReading, String> nameCol;

    @FXML
    private TableColumn<TableReading, String> locationCol;


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
               // TableReading ms = new TableReading(station.name, station.location);
                //msList.add(ms);
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
            TableReading selectedStation = msTable.getSelectionModel().getSelectedItem();
           // return selectedStation.Name;
        }
        return null;
    }
}

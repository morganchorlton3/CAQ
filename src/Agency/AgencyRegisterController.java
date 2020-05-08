package Agency;

import CAQ.MonitoringCenter;
import CAQ.MonitoringCenterHelper;
import CAQ.NoxReading;
import CAQ.Station;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class AgencyRegisterController implements Initializable {

    @FXML
    ComboBox<String> lsSelect;

    @FXML
    TextField nameTXT, numberTXT;

    private String serverName = "MonitoringCenter";

    public static void raiseAlarm(NoxReading reading) {
        System.out.println("ALARM!!!");
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

    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsSelect.getItems().clear();
        //Get List of Local servers
        try {
            MonitoringCenter mcServant = MonitoringCenterHelper.narrow(Agency.nameService.resolve_str(serverName));
            List<Station> lsList = Arrays.asList(mcServant.localServers());
            for(Station s: lsList){
                //System.out.println(s.name);
                lsSelect.getItems().add(s.name);
            }
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    @FXML
    private void registerAgency(ActionEvent event) throws IOException {
        CAQ.Agency agency = new CAQ.Agency(nameTXT.getText(), lsSelect.getSelectionModel().getSelectedItem(), nameTXT.getText());
        Agency.setupAgency(agency);
        try {
            MonitoringCenter msServant = MonitoringCenterHelper.narrow(Agency.nameService.resolve_str(serverName));
            msServant.register_agency(agency);
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }

    }


}

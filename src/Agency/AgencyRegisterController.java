package Agency;

import CAQ.MonitoringCenter;
import CAQ.MonitoringCenterHelper;
import CAQ.Station;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsSelect.getItems().clear();
        //Get List of Local servers
        try {
            MonitoringCenter msServant = MonitoringCenterHelper.narrow(Agency.nameService.resolve_str(serverName));
            List<Station> lsList = Arrays.asList(msServant.localServers());
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
        try {
            MonitoringCenter msServant = MonitoringCenterHelper.narrow(Agency.nameService.resolve_str(serverName));
            msServant.register_agency(agency);
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }

    }


}

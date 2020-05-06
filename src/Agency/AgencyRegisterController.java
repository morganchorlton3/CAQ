package Agency;

import CAQ.MonitoringCenter;
import CAQ.MonitoringCenterHelper;
import CAQ.Station;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class AgencyRegisterController implements Initializable {

    @FXML
    MenuButton lsButtonMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        lsButtonMenu.getItems().clear();
        //Get List of Local servers
        try {
            String name = "MonitoringCenter";
            MonitoringCenter counter = MonitoringCenterHelper.narrow(Agency.nameService.resolve_str(name));
            List<Station> lsList = Arrays.asList(counter.localServers());
            for(Station s: lsList){
                //System.out.println(s.name);
                lsButtonMenu.getItems().add(new MenuItem(s.name));
            }
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }


}

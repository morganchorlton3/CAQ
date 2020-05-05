package MonitoringCenter;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {
    public static void Alert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("High Reading");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.show();
    }
}
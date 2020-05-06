package Agency;

import CAQ.MonitoringCenter;
import CAQ.MonitoringCenterHelper;
import CAQ.Station;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Agency extends Application {

    private static ORB orb;
    public static NamingContextExt nameService;

    public static void main(String[] args) {
        try {
            orb = ORB.init(args, null);

            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }


        } catch(Exception e) {
            System.err.println("Exception");
            System.err.println(e);
        }
            launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("AgencyRegister.fxml"));
            primaryStage.setTitle("CAQ Monitoring Station");
            Scene scene = new Scene(root, 400, 500);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(500);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package MonitoringCenter;

import CAQ.NoxReading;
import CAQ.Station;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MonitoringCenter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("CAQ Monitoring LocalServer.Station");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    static class MonitoringCenterImpl extends CAQ.MonitoringCenterPOA {

        List<Station> localStations = new ArrayList<>();

        @Override
        public void raise_alarm(NoxReading alarm_reading) {

        }

        @Override
        public void register_agency(String who, String contact_details, String area_of_interest) {

        }

        @Override
        public void register_local_server(String server_name) {
        }
    }

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            MonitoringCenterImpl monitoringStation = new MonitoringCenterImpl();

            // Get the 'stringified IOR'
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(monitoringStation);
            String stringified_ior = orb.object_to_string(ref);

            // Save IOR to file
            BufferedWriter out = new BufferedWriter(new FileWriter("/home/morgan/src/jacorb-3.9/bin/name.ior"));
            out.write(stringified_ior);
            out.close();
            System.out.println("stringified_ior = " + stringified_ior);

            System.out.println("Monitoring Center ready and waiting ...");

            //activate with local server

            // wait for invocations from clients
            launch(args);
            orb.run();

        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Monitoring Center Exiting ...");
    }

}

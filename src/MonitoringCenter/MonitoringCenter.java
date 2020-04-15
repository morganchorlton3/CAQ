package MonitoringCenter;

import CAQ.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


class MonitoringCenterImpl extends MonitoringCenterPOA {

    static List<String> localServerList = new ArrayList<>();

    @Override
    public void raise_alarm(NoxReading alarm_reading) {

    }

    @Override
    public void register_agency(String who, String contact_details, String area_of_interest) {

    }

    @Override
    public void register_local_server(String server_name) {
        localServerList.add(server_name);
        System.out.println(server_name + " has successfully been registered");
    }


    public static List<String> getLocalServerList(){
        return localServerList;
    }
}

public class MonitoringCenter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("CAQ Monitoring LocalServer.Station");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }



    static public void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the Count servant object
            MonitoringCenterImpl MCServant = new MonitoringCenterImpl();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(MCServant);
            CAQ.MonitoringCenter MSRef = MonitoringCenterHelper.narrow(ref);

            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            NameComponent[] monitoringCenters = nameService.to_name("MonitoringCenter");
            nameService.rebind(monitoringCenters, MSRef);

            //Launch GUI
            launch(args);

            //  wait for invocations from clients
            orb.run();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void getReading(String[] args, String serverName){
        try {

            ORB orb = ORB.init(args, null);
            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            try {
                MonitoringStation monitoringCenter = MonitoringStationHelper.narrow(nameService.resolve_str("MS1"));
                System.out.println(monitoringCenter.get_reading());

            }catch (Exception e){
                System.out.println("Monitoring Center not found");
            }


            System.out.println("Local Server registered with the Monitoring Center");

        } catch(Exception e) {
            System.err.println("Exception");
            System.err.println(e);
        }

    }

}
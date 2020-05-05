package MonitoringCenter;

import CAQ.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class MonitoringCenterImpl extends MonitoringCenterPOA {

    private ORB orb;
    private NamingContextExt nameService;

    static List<String> localServerList = new ArrayList<>();
    static List<NoxReading> lsLog = new ArrayList<>();

    public MonitoringCenterImpl(ORB orb_val) {
        try {
            orb = orb_val;
            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj = orb.resolve_initial_references ("NameService");
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
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void raise_alarm(NoxReading alarm_reading) {

    }

    @Override
    public NoxReading[] readingsLog() {
        return lsLog.toArray(new NoxReading[0]);
    }

    @Override
    public void register_agency(String who, String contact_details, String area_of_interest) {

    }

    @Override
    public void register_local_server(String server_name) {
        localServerList.add(server_name);
        MonitoringCenterController.updateLocalServerList();
        System.out.println(server_name + " has successfully been registered");
    }

    @Override
    public void takeReadings(String name) {
        try {
            System.out.println(name);
            RegionalCentre lsServant = RegionalCentreHelper.narrow(nameService.resolve_str(name));
            ArrayList<NoxReading> collectedReadings = new ArrayList<>(Arrays.asList(lsServant.readingsLog()));
            for (int i = 0; i < collectedReadings.size(); i++) {
               lsLog.add(collectedReadings.get(i));
            }
            ObservableList<String> readings = FXCollections.observableArrayList ();
            for (NoxReading reading : lsLog) {
                String readingToAdd = "MS Name : " + reading.station_name + " Reading: " + reading.reading_value;
                readings.addAll(readingToAdd);
            }
            Alerts.Alert("High Reading");
        } catch (NotFound | CannotProceed | InvalidName notFound) {
            notFound.printStackTrace();
        }
    }


    public static List<String> getLocalServerList(){
        return localServerList;
    }

    public static List<NoxReading> getReadings(){
        return lsLog;
    }
}

public class MonitoringCenter extends Application {

    static public void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the Count servant object
            MonitoringCenterImpl MCServant = new MonitoringCenterImpl(orb);

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

    //GUI
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MonitoringCenter.fxml"));
        primaryStage.setTitle("CAQ Monitoring Station");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();

    }

}
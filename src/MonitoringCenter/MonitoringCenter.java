package MonitoringCenter;

import CAQ.*;
import LocalServer.LocalServer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    static List<Station> localServerList = new ArrayList<>();
    static List<NoxReading> readingsLog = new ArrayList<>();
    static List<Station> monitoringStationList = new ArrayList<>();
    static List<Agency> RegisteredAgenciesList = new ArrayList<>();

    public MonitoringCenterImpl(ORB orb_val) {
        try {
            orb = orb_val;
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
    }

    @Override
    public Station[] localServers() {
        return localServerList.toArray(new Station[0]);
    }

    @Override
    public Station[] monitoringStations() {
        return localServerList.toArray(new Station[0]);
    }

    @Override
    public void raise_alarm(NoxReading alarm_reading, String lsName) {
        System.out.println("Alarm Recived");
        for (Agency agency : RegisteredAgenciesList) {
            if (agency.locationOfInterest.equals(lsName)) {
                System.out.println("Alert Agency");
                try {
                    AgencyMonitor agencyServant = AgencyMonitorHelper.narrow(nameService.resolve_str(agency.name));
                    agencyServant.raise_alarm(alarm_reading);
                } catch (NotFound | CannotProceed | InvalidName notFound) {
                    notFound.printStackTrace();
                }
            }
        }
        MonitoringCenterController.raiseAlarm(alarm_reading, lsName);
    }


    @Override
    public void register_agency(Agency agencyObject) {
        RegisteredAgenciesList.add(agencyObject);
        System.out.println(Arrays.toString(RegisteredAgenciesList.toArray()));
        MonitoringCenterController.updateAgencies();
    }


    @Override
    public void register_local_server(Station station) {
        localServerList.add(station);
        MonitoringCenterController.updateLocalServerList();
        System.out.println(station.name + " has successfully been registered");
    }

    @Override
    public void register_monitoring_station(Station station_name) {
        monitoringStationList.add(station_name);
        MonitoringCenterController.updateMonitoringStations();
    }

    @Override
    public void takeReadings(String name) {
        try {
            System.out.println(name);
            RegionalCentre lsServant = RegionalCentreHelper.narrow(nameService.resolve_str(name));
            ArrayList<NoxReading> collectedReadings = new ArrayList<>(Arrays.asList(lsServant.readingsLog()));
            readingsLog.clear();
            for (int i = 0; i < collectedReadings.size(); i++) {
                readingsLog.add(collectedReadings.get(i));
            }
            MonitoringCenterController.updateReadings();
        } catch (NotFound | CannotProceed | InvalidName notFound) {
            notFound.printStackTrace();
        }
    }

    public static List<NoxReading> getReadingsList() {
        return readingsLog;
    }

    public static List<Station> getLocalServerList() {
        return localServerList;
    }

    public static List<Agency> getAgenciesList() {
        return RegisteredAgenciesList;
    }

    public static List<Station> getMonitoringStationList() {
        return monitoringStationList;
    }

}


public class MonitoringCenter extends Application {

    public static ORB orb;
    public static NamingContextExt nameService;

    static public void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

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
            nameService = NamingContextExtHelper.narrow(nameServiceObj);
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

    public static ORB getOrb(){
        return orb;
    }

    //GUI
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MonitoringCenter.fxml"));
        primaryStage.setTitle("CAQ Monitoring Station");
        Scene scene = new Scene(root, 1300, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1300);
        primaryStage.setMinHeight(700);
        primaryStage.setMaxWidth(1300);
        primaryStage.setMaxHeight(700);
        primaryStage.show();
    }

}
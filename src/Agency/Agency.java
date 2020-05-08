package Agency;

import CAQ.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

class AgencyServant extends AgencyMonitorPOA {

    ORB orb;
    NamingContextExt nameService;
    String name = "";
    String locationOfInterest  = "";
    String contactInfo  = "";

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String arg) {
        name = arg;
    }

    @Override
    public String locationOfInterest() {
        return locationOfInterest;
    }

    @Override
    public void locationOfInterest(String arg) {
        locationOfInterest = arg;
    }

    @Override
    public String contactInfo() {
        return contactInfo;
    }

    @Override
    public void contactInfo(String arg) {
        contactInfo = arg;
    }

    @Override
    public void raise_alarm(NoxReading reading) {
        AgencyRegisterController.raiseAlarm(reading);
    }

    public AgencyServant(ORB orb_val) {
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
}

public class Agency extends Application {



    private static ORB orb;
    public static NamingContextExt nameService;


    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            nameService = NamingContextExtHelper.narrow(nameServiceObj);

            launch(args);
            orb.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    public static void setupAgency(CAQ.Agency agency){

        AgencyServant agencyServant = new AgencyServant(orb);
        System.out.println("Setting up Monitoring Station");

        agencyServant.name(agency.name);
        agencyServant.contactInfo(agency.contactNumber);
        agencyServant.locationOfInterest(agency.locationOfInterest);
        try {
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(agencyServant);
            AgencyMonitor agencyRef = AgencyMonitorHelper.narrow(ref);

            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // bind the Count object in the Naming service
            NameComponent[] countName = nameService.to_name(agency.name);
            nameService.rebind(countName, agencyRef);

        } catch (WrongPolicy | AdapterInactive | ServantNotActive | InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound wrongPolicy) {
            wrongPolicy.printStackTrace();
        }
    }
}

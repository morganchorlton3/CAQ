package LocalServer;

import CAQ.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Scanner;

class RegionalCenterImpl extends RegionalCentrePOA {

    public String stationName = "";

    public String stationLocation = "";

    @Override
    public String name() {
        return stationName;
    }

    public void name(String name){
        stationName = name;
    }

    @Override
    public String location() {
        return stationLocation;
    }

    public void location(String location){
        stationLocation = location;
    }

    @Override
    public NoxReading[] log() {
        return new NoxReading[0];
    }

    @Override
    public void add_monitoring_station(String station_name, String station_location, String station_ior) {

    }
}

public class LocalServer {

    public static void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            //Regional Center Setup
            RegionalCenterImpl regionalCenter = registerRegionalCenter();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(regionalCenter);
            RegionalCentre cref = RegionalCentreHelper.narrow(ref);

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

            // bind the Count object in the Naming service


            NameComponent[] countName = nameService.to_name(regionalCenter.name());
            nameService.rebind(countName, cref);

            System.out.println("Local Server Ready...");

            System.out.println(
                    "Name: " + regionalCenter.name() + '\n' +
                            "Location: " + regionalCenter.location() + '\n'
            );

            //  wait for invocations from clients
            orb.run();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Setup Regional Center
    private static RegionalCenterImpl registerRegionalCenter(){
        // Create the Count servant object
        RegionalCenterImpl regionalCenter = new RegionalCenterImpl();
        System.out.println("Setting up Local Server");

        Scanner in = new Scanner(System.in);

        System.out.println("Local Server Name:");

        String stationName = in.nextLine();

        regionalCenter.name(stationName);

        System.out.println("Local Server Location:");

        String stationLocation = in.nextLine();

        regionalCenter.location(stationLocation);

        return regionalCenter;
    }
}
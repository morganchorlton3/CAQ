package MonitoringStation;

import CAQ.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import LocalServer.LocalServer;
import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;


class MonitoringStationImpl extends MonitoringStationPOA {

    String name = "";
    String location  = "";

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String stationName) {
        name = stationName;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public void location(String stationLocation) {
        location = stationLocation;
    }

    @Override
    public boolean status() {
        return false;
    }

    @Override
    public void status(boolean arg) {
        return;
    }

    @Override
    public NoxReading get_reading() {
        //New date
        Date date = new Date();
        Random r = new Random();

        //Random Number
        int co2 = r.nextInt(100-10) + 10;
        return new NoxReading(date.getTime(), date.getTime(), name(),co2);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {
        this.status(false);
    }

    @Override
    public void reset() {

    }

}


public class MonitoringStation {

    public static void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            //Regional Center Setup
            MonitoringStationImpl monitoringStation = setUpMonitoringStation();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(monitoringStation);
            CAQ.MonitoringStation MSRef = MonitoringStationHelper.narrow(ref);

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


            NameComponent[] countName = nameService.to_name(monitoringStation.name());
            nameService.rebind(countName, MSRef);

            System.out.println("Monitoring Station Ready...");

            System.out.println(
                    "Name: " + monitoringStation.name() + '\n' +
                            "Location: " + monitoringStation.location() + '\n'
            );

            //  wait for invocations from clients
            orb.run();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Setup Regional Center
    private static MonitoringStationImpl setUpMonitoringStation(){
        // Create the Count servant object
        MonitoringStationImpl regionalCenter = new MonitoringStationImpl();
        System.out.println("Setting up Local Server");

        Scanner in = new Scanner(System.in);

        System.out.println("Station Name:");

        String stationName = in.nextLine();

        regionalCenter.name(stationName);

        System.out.println("Station Location:");

        String stationLocation = in.nextLine();

        regionalCenter.location(stationLocation);

        return regionalCenter;
    }
}

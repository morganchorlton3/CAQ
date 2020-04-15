package MonitoringStation;

import CAQ.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import LocalServer.LocalServer;
import org.omg.CORBA.*;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;


class MonitoringStationImpl extends MonitoringStationPOA {

    String station_name = "";

    @Override
    public String station_name() {
        return station_name;
    }

    @Override
    public void station_name(String name) {
        station_name = name;
    }

    @Override
    public String location() {
        return "Home";
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
        return new NoxReading(date.getTime(), date.getTime(),station_name(),co2);
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
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            MonitoringStationImpl monitoringStation = new MonitoringStationImpl();

            // Get the 'stringified IOR'
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(monitoringStation);
            String stringified_ior = orb.object_to_string(ref);

            // Save IOR to file
            BufferedWriter out = new BufferedWriter(new FileWriter("name.ior"));
            out.write(stringified_ior);
            out.close();
            System.out.println("stringified_ior = " + stringified_ior);

            System.out.println(stringified_ior);

            //registerWithLocalServer(orb, monitoringStation, stringified_ior);

            System.out.println("Monitoring Station ready and waiting ...");

            //activate with local server
            try {
                // read in the 'stringified IOR'
                BufferedReader in = new BufferedReader(new FileReader("name.ior"));
                String LocalServer_stringified_ior = in.readLine();
                System.out.println("stringified_ior = " + stringified_ior);

                // get object reference from stringified IOR
                org.omg.CORBA.Object server_ref =
                        orb.string_to_object(LocalServer_stringified_ior);
                CAQ.RegionalCentre server =
                        CAQ.RegionalCentreHelper.narrow(server_ref);

                monitoringStation.station_name("Hello World");

                System.out.println(monitoringStation.station_name);

                server.add_monitoring_station(monitoringStation.station_name(), monitoringStation.location(), stringified_ior);

                System.out.println("Registered with Regional Center");

            } catch (Exception e) {
                System.out.println("ERROR : " + e) ;
                e.printStackTrace(System.out);
            }


            // wait for invocations from clients
            orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");

    }
}

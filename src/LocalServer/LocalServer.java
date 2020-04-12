package LocalServer;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import CAQ.RegionalCentrePOA;
import CAQ.Station;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

class MonitoringStationServant extends RegionalCentrePOA {

    private ORB orb;
    private CAQ.MonitoringStation server;

    public Station[] station_list;

    @Override
    public String name() {
        return null;
    }

    @Override
    public String location_name() {
        return null;
    }

    @Override
    public Station[] getStationList() {
        return station_list;
    }

    @Override
    public void addStation(Station station_object) {
        station_list.
    }

    MonitoringStationServant(ORB orb_val) {
        // store reference to ORB
        orb = orb_val;

        // look up the server
        try {
            // read in the 'stringified IOR'
            BufferedReader in = new BufferedReader(new FileReader("server.ref"));
            String stringified_ior = in.readLine();

            // get object reference from stringified IOR
            org.omg.CORBA.Object server_ref =
                    orb.string_to_object(stringified_ior);
            server = CAQ.MonitoringStationHelper.narrow(server_ref);
        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void add_monitoring_station(String station_name, String station_location, String station_ior) {
        CAQ.Station station = new CAQ.Station(station_name, station_location,station_ior);
        try{
            //add(station);
            //Testing
            //System.out.println(stationsList.get(0).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


public class LocalServer {

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            MonitoringStationServant relayRef = new MonitoringStationServant(orb);

            // Get the 'stringified IOR'
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(relayRef);
            String stringified_ior = orb.object_to_string(ref);

            // Save IOR to file
            BufferedWriter out = new BufferedWriter(new FileWriter("LocalServer.ref"));
            out.write(stringified_ior);
            out.close();

            // wait for invocations from clients
            System.out.println("Local Server started.  Waiting for clients...");



            orb.run();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Local Server Exiting ...");

    }
}

package LocalServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import CAQ.RegionalCentrePOA;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

class RegionalCenterImpl extends RegionalCentrePOA {

    List<Station> stationsList= new ArrayList<Station>();

    @Override
    public String name() {
        return null;
    }

    @Override
    public String location_name() {
        return null;
    }

    @Override
    public void add_monitoring_station(String station_name, String station_location, String station_ior) {
        Station station = new Station(station_name, station_location,station_ior);
        try{
            stationsList.add(station);
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
            RegionalCenterImpl RegionalCenterImpl = new RegionalCenterImpl();

            // Get the 'stringified IOR'
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(RegionalCenterImpl);
            String stringified_ior = orb.object_to_string(ref);

            // Save IOR to file
            BufferedWriter out = new BufferedWriter(new FileWriter("server.ref"));
            out.write(stringified_ior);
            out.close();
            System.out.println("stringified_ior = " + stringified_ior);

            System.out.println("Monitoring LocalServer.Station ready and waiting ...");

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

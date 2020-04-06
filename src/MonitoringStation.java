import CAQ.MonitoringStationPOA;
import CAQ.NoxReading;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.Random;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;


class MonitoringStationImpl extends MonitoringStationPOA {

    @Override
    public String station_name() {
        return "Station 1";
    }

    @Override
    public String location() {
        return "Home";
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
            MonitoringStationImpl sensorImpl = new MonitoringStationImpl();

            // Get the 'stringified IOR'
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(sensorImpl);
            String stringified_ior = orb.object_to_string(ref);

            // Save IOR to file
            BufferedWriter out = new BufferedWriter(new FileWriter("server.ref"));
            out.write(stringified_ior);
            out.close();
            System.out.println("stringified_ior = " + stringified_ior);

            System.out.println("HelloServer ready and waiting ...");

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

import org.jacorb.orb.ORB;

import java.io.*;

public class testClient {
    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = (ORB) ORB.init(args, null);

            // read in the 'stringified IOR'
            BufferedReader in = new BufferedReader(new FileReader("server.ref"));
            String stringified_ior = in.readLine();
            System.out.println("stringified_ior = " + stringified_ior);

            // get object reference from stringified IOR
            org.omg.CORBA.Object server_ref =
                    orb.string_to_object(stringified_ior);
            CAQ.RegionalCentre server =
                    CAQ.RegionalCentreHelper.narrow(server_ref);

            // call the Hello server object and print results

            server.add_monitoring_station("Server 1", "Manchester", server_ref.toString());


        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }
}

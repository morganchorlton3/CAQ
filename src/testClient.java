import CAQ.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class testClient {
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            System.out.println("Initializing the ORB");
            ORB orb = ORB.init(args, null);

            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            // resolve the Count object reference in the Naming service
            String name = "MonitoringCenter";
            MonitoringCenter counter = MonitoringCenterHelper.narrow(nameService.resolve_str(name));
            counter.takeReadings("LS1");
            ArrayList<NoxReading> collectedReadings = new ArrayList<>(Arrays.asList(counter.readingsLog()));
            for (int i = 0; i < collectedReadings.size(); i++) {
                System.out.println(collectedReadings.get(i).reading_value);
            }

        } catch(Exception e) {
            System.err.println("Exception");
            System.err.println(e);
        }
    }
}

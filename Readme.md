<h1 align="center">University Project</h1>

 <h3 align="center">Corba Air Quality Monitoring System</h3>


Setup And Running:

Add Jacorb 

    Jacorb Download:

    https://drive.google.com/file/d/1fjcKhrBw5iqWjZDuAKC25L7qjHUIZxrc/view?usp=sharing

    Also download the library jboss-rmi-api_1.0_spec-1.0.6.Final.jar from the Maven Repository at:
    
    https://mvnrepository.com/artifact/org.jboss.spec.javax.rmi/jboss-rmi-api_1.0_spec/1.0.6.Final.
 

How to add libraries:

    File > Project Structure > libraries

Add Jacorb 3.9 lib folder

    /Jacorb-3.9/lib/

When Running add VM Arguments:

    -Djacorb.log.default.verbosity=2
    
Generate IDL code

    cd src
    ~/src/jacorb-3.9/bin/idl CAQ.idl
    
To run the Monitoring station (GUI) you may need to add this VM argument

    --module-path Path to openFX/ JFX --add-modules=javafx.controls,javafx.fxml

Add to program arguments:

    -ORBInitRef.NameService=file: Path to ior file

    -ORBInitRef.NameService=file:/home/morgan/Work/University/Distributed/name.ior

Start Naming Service:

    (you may need to add jacrob to path)

    ns -Djacorb.naming.ior_filename= Path to ior file
    
    ns -Djacorb.naming.ior_filename=/home/morgan/Work/University/Distributed/name.ior
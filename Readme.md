<h1 align="center">University Project</h1>

 <h3 align="center">Corba Air Quality Monitoring System</h3>


Setup And Running:

For Step 1. and 2.

How to add libraries:

    File > Project Structure > libraries

Add Jacorb 3.9 lib folder

    /Jacorb-3.9/lib/

When Running add VM Arguments:

    -Djacorb.log.default.verbosity=2
    
Generate IDL code

    cd src
    ~/src/jacorb-3.9/bin/idl CAQ.idl

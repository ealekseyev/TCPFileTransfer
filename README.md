# TCPFileTransfer
A basic, unencrypted TCP File Transfer program - custom made to be fast and versatile
## How to run
1) Navigate to the directory */FileTransfer/out/artifacts/FileTransfer_jar*
2) Execute the jar file in that directory named *FileTransfer.jar*
Terminal Command for all operating systems:

``` java -jar FileTransfer.jar```

*Note Java must be installed on your machine for this to work.*

#### Optional:

You can add an arg for the mode the program automatically goes into - either `server` or `client`. 

#### To auto-execute in server mode: 

```java -jar FileTransfer.jar server```
  
This command tells the program to automatically listen for incoming connections from the client.

#### To auto-execute in client mode: 

```java -jar FileTransfer.jar client```

This will tell the program to go into client mode, where you input the full path of the file to send and the IP Address of the server.
  
  ## Reference
  * The recieved file is stored in the Downloads folder in your home directory.
  * If a file to be recieved has the same name as an existing file in `Downloads`, the sent file name will automatically be changed.
  * This program uses **port 30000** for *file transmission* and **port 30001** for *file info transmission* - these constants are located in `Constants.java`.
  * This program currently sends completely unencrypted TCP packets.
  * **DO NOT USE THIS ON UNSECURED NETWORKS OR ACROSS THE GLOBAL WAN FOR YOUR OWN SECURITY.**

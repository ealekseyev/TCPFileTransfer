package com.ealekseyev.TCPFileTransfer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private String filePath = "";
    private String fileName = "";
    private long fileSize = -1;

    public TCPServer() {

    }
    public void listen() throws Exception {
        try {
            //Initialize Sockets
            ServerSocket ssock = new ServerSocket(Constants.port);
            Socket socket = ssock.accept();
            System.out.println(Constants.YELLOW + "Incoming connection from " + socket.getRemoteSocketAddress().toString() + Constants.RESET);

            // input stream from socket
            DataInputStream sockStream = new DataInputStream(socket.getInputStream());

            // get info
            fileName = sockStream.readUTF();
            fileSize = sockStream.readLong();
            

            // check if file exists, change name if necessary
            filePath = System.getenv("HOME") + "/Downloads/";
            verifyName(filePath + OtherFunctions.getFirstEntry(fileName));

            //Initialize the FileOutputStream to the output file's full path.
            BufferedOutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            System.out.printf(Constants.CYAN + "File Size: %.1f kilobytes\n" + Constants.RESET, fileSize/1000.0);
            System.out.println("Saving file to " + filePath);

            // TODO: for sending directories, simply send the file and its path.
            // TODO: If the path does not exist, create the required folders automatically.

            /*
            -----------START RETRIEVAL----------
             */

            // buffer for incoming data
            byte[] contents = new byte[Constants.bufLen];
            long dataRecv = 0;
            long prevDataRecv = 0;
            long prevCheckTime = System.currentTimeMillis();
            // This will become the byte count from the read() call
            int byteCount = 0;

            // write data to file as it comes in
            long start = System.currentTimeMillis();
            System.out.println(Constants.YELLOW + "Receiving file - 0% complete..." + Constants.RESET);
            while ((byteCount = sockStream.read(contents)) != -1) {
                outputStream.write(contents, 0, byteCount);
                dataRecv += byteCount;
                if(((dataRecv * 100) / fileSize) - ((prevDataRecv * 100) / fileSize) == 5 && dataRecv != fileSize) {
                    System.out.print(Constants.YELLOW + "Receiving file - " + (dataRecv * 100) / fileSize + "% complete..." + Constants.RESET);
                    System.out.printf(Constants.CYAN + " Speed: %.1f mbps\n" + Constants.RESET, ((dataRecv - prevDataRecv) / 1000000.0) / ((System.currentTimeMillis() - prevCheckTime) / 1000.0));
                    prevDataRecv = dataRecv;
                    prevCheckTime = System.currentTimeMillis();
                }
            }
            if(dataRecv != fileSize) {
                System.out.println(Constants.RED + "An error occurred during retreival of " + fileName + "." + Constants.RESET);
                System.exit(0);
            }
            System.out.println(Constants.YELLOW + "Recieving file - 100% complete!" + Constants.RESET);
            System.out.printf(Constants.CYAN + "Time elapsed: %.1f minutes\n" + Constants.RESET, (System.currentTimeMillis() - start) / 60000.0);
            System.out.printf(Constants.CYAN + "Average speed: %.1f megabytes per second.\n" + Constants.RESET, (fileSize/1000000.0) / ((System.currentTimeMillis() - start) / 1000.0));


            /*
            -----------END RETRIEVAL----------
             */

            // close data streams
            outputStream.flush();
            outputStream.close();
            socket.close();
            ssock.close();

            System.out.println(Constants.GREEN + "Success!" + Constants.RESET);
        } catch (Exception e) {
            System.out.println(Constants.RED + e + Constants.RESET);
        }
    }
    private String verifyName(String pathToFile) {
        String tempPath = OtherFunctions.stripLastEntry(pathToFile);
        String tempFileName = OtherFunctions.getLastEntry(pathToFile);
        while(true) {
            if (new File(pathToFile).exists()) {
                tempFileName = OtherFunctions.newFileName(tempFileName);
            } else {
                return tempPath + "/" + tempFileName;
            }
        }
    }
}
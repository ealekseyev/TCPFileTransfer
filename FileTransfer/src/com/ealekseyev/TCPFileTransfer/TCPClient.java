package com.ealekseyev.TCPFileTransfer;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    public void send(String address, String path) throws Exception {
        // TODO: have the machines send a status back at the end of transfer
        /*Thread getNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket nameSock = new Socket(address, Constants.namePort);
                    DataOutputStream nameOutput = new DataOutputStream(nameSock.getOutputStream());

                    String name = path.split("/")[path.split("/").length - 1];
                    nameOutput.writeUTF(name);
                    // its length
                    nameOutput.writeUTF(Long.toString(new File(path).length()));

                    nameOutput.flush();
                    nameOutput.close();
                    nameSock.close();
                } catch (Exception e) {
                    // print the stackTrace in ANSI_RED
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });*/

        /*
        ################################
        END INFO TRANSMISSION
        START FILE TRANSMISSION
        ################################
         */

        Thread sendFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize socket
                    Socket socket = new Socket(address, Constants.port);

                    String name = path.split("/")[path.split("/").length - 1];

                    //Specify the file
                    File file = new File(path);
                    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                    //Get socket's output stream
                    OutputStream outputStream = socket.getOutputStream();

                    /*
                    -----------START TRANSMISSION----------
                     */

                    // name
                    outputStream.write(name.getBytes("UTF-8"));
                    // length
                    //outputStream.write(Long.toString(new File(path).length()).getBytes());


                    // Read file contents into fileData array, then send it
                    byte[] fileData;
                    // file size (bytes)
                    long fileSize = file.length();
                    // amount of data read and sent (bytes)
                    long dataSent = 0;
                    // used for sending progress messages - amount of bytes sent
                    long prevDataSent = 0;
                    // length of packets to be sent (bytes)
                    int packSize = Constants.bufLen;

                    // Send file
                    long start = System.currentTimeMillis();
                    long prevCheckTime = System.currentTimeMillis(); // to calculate speed at percentage checkpoint
                    System.out.println(Constants.YELLOW + "Sending file - 0% complete..." + Constants.RESET);
                    while (dataSent != fileSize) {
                        if (fileSize - dataSent >= packSize)
                            dataSent += packSize;
                        else {
                            packSize = (int) (fileSize - dataSent);
                            dataSent = fileSize;
                        }
                        fileData = new byte[packSize];
                        inputStream.read(fileData, 0, packSize);
                        outputStream.write(fileData);
                        if (((dataSent * 100) / fileSize) - ((prevDataSent * 100) / fileSize) == 5 && dataSent != fileSize) {
                            System.out.print(Constants.YELLOW + "Sending file - " + (dataSent * 100) / fileSize + "% complete..." + Constants.RESET);
                            System.out.printf(Constants.CYAN + " Speed: %.1f mbps\n" + Constants.RESET, ((dataSent-prevDataSent)/1000000.0) / ((System.currentTimeMillis() - prevCheckTime) / 1000.0));
                            prevDataSent = dataSent;
                            prevCheckTime = System.currentTimeMillis();
                        }
                    }
                    System.out.println(Constants.YELLOW + "Sending file - 100% complete!" + Constants.RESET);

                    /*
                    -----------END TRANSMISSION----------
                     */

                    // close data streams
                    outputStream.flush();
                    socket.close();

                    // feedback - speed and time
                    System.out.printf(Constants.CYAN + "Time elapsed: %.1f minutes\n" + Constants.RESET, (System.currentTimeMillis() - start) / 60000.0);
                    System.out.printf(Constants.CYAN + "Average speed: %.1f megabytes per second.\n" + Constants.RESET, (fileSize/1000000.0) / ((System.currentTimeMillis() - start) / 1000.0));
                    System.out.println(Constants.GREEN + "Success!" + Constants.RESET);
                } catch (Exception e) {
                    // print the stackTrace in ANSI_RED
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });

        //getNameThread.start();
        sendFileThread.start();
    }
}

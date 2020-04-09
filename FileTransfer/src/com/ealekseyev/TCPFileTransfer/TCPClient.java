package com.ealekseyev.TCPFileTransfer;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    public void send(String address, String path) throws Exception {
        // TODO: have the machines send a status back at the end of transfer
        try {
            //Initialize socket
            Socket socket = new Socket(address, Constants.port);

            String fileName = path.split("/")[path.split("/").length - 1];

            //Specify the file
            File file = new File(path);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            //Get socket's output stream
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            /*
            -----------START TRANSMISSION----------
             */

            // name
            outputStream.writeUTF(fileName);
            // length
            outputStream.writeLong(file.length());
            long fileSize = file.length();

            if(fileSize < 1000) {
                System.out.println("File size: " + Constants.CYAN + Long.toString(fileSize) + " bytes" + Constants.RESET);
            } else if(fileSize < 500000 && fileSize >= 1000) {
                System.out.printf("File size: " + Constants.CYAN + "%0.1f kilobytes" + Constants.RESET + "\n", fileSize/1000.0);
            } else if(fileSize < 500000000 && fileSize >= 500000) {
                System.out.printf("File size: " + Constants.CYAN + "%0.1f megabytes" + Constants.RESET + "\n", fileSize/1000000.0);
            } else if(fileSize >= 500000000) {
                System.out.printf("File size: " + Constants.CYAN + "%0.1f gigabytes" + Constants.RESET + "\n", fileSize/1000000000.0);
            }


            // Read file contents into fileData array, then send it
            byte[] fileData;
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
}

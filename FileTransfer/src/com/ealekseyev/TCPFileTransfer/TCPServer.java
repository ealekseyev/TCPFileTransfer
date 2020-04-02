package com.ealekseyev.TCPFileTransfer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private String filePath = "";
    private volatile String fileName = "~~NAN~~";
    private volatile byte isDir = -2;
    private volatile long fileSize = -2;
    public void listen() throws Exception {
        Thread getNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket nameSock = new ServerSocket(Constants.namePort);
                    Socket nameSockIn = nameSock.accept();
                    DataInputStream nameIn = new DataInputStream(
                            new BufferedInputStream(nameSockIn.getInputStream()));

                    // get filename
                    fileName = nameIn.readUTF();
                    isDir = Byte.parseByte(nameIn.readUTF());
                    fileSize = Long.parseLong(nameIn.readUTF());

                    nameIn.close();
                    nameSock.close();
                    nameSockIn.close();
                } catch (Exception e) {
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });

        /*
        ################################
        END INFO RETRIEVAL
        ################################
         */

        Thread getFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize Sockets
                    ServerSocket ssock = new ServerSocket(Constants.port);
                    Socket socket = ssock.accept();
                    System.out.println(Constants.YELLOW + "Incoming connection from " + socket.getRemoteSocketAddress().toString() + Constants.RESET);

                    // input stream from socket
                    InputStream sockStream = socket.getInputStream();

                    // wait for the getNameThread thread to finish and spit out file info
                    while(fileName.equals("~~NAN~~")){}
                    System.out.println(Constants.CYAN + "File Name: " + fileName + Constants.RESET);

                    // check if file exists, change name if necessary
                    filePath = System.getenv("HOME") + "/Downloads/";
                    while(true) {
                        if (new File(filePath + fileName).exists()) {
                            fileName = OtherFunctions.newFileName(fileName);
                        } else {
                            filePath += fileName;
                            break;
                        }
                    }

                    //Initialize the FileOutputStream to the output file's full path.
                    BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(filePath));
                    System.out.printf(Constants.CYAN + "File Type: %s\n" + Constants.RESET, (isDir == 0) ? "file":"folder");
                    System.out.printf(Constants.CYAN + "File Size: %.1f kilobytes\n" + Constants.RESET, fileSize/1000.0);
                    System.out.println("Saving file to " + filePath);

                    /*
                    SAVE THE ACTUAL FILE
                    TODO: for sending directories, simply send the file and its path.
                     If the path does not exist, create the required folders automatically.
                     */

                    // buffer for incoming data
                    byte[] contents = new byte[Constants.bufLen];
                    // This will become the byte count in one read() call -
                    int byteCount = 0;

                    /*
                    -----------START RETRIEVAL----------
                     */

                    // write data to file as it comes in
                    while ((byteCount = sockStream.read(contents)) != -1)
                        outputStream.write(contents, 0, byteCount);

                    /*
                    -----------END RETRIEVAL----------
                     */

                    // close data streams
                    outputStream.flush();
                    socket.close();
                    ssock.close();

                    System.out.println(Constants.GREEN + "Success!" + Constants.RESET);
                } catch (Exception e) {
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });

        getNameThread.start();
        getFileThread.start();
    }
}
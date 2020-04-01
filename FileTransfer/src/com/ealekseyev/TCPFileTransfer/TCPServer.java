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
        END INFO RETRIEVAL
         */

        Thread getFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize Sockets
                    ServerSocket ssock = new ServerSocket(Constants.port);
                    Socket socket = ssock.accept();
                    System.out.println(Constants.CYAN + "Incoming connection from " + socket.getRemoteSocketAddress().toString() + Constants.RESET);

                    // input stream from socket
                    InputStream sockStream = socket.getInputStream();

                    // wait for the getNameThread thread to finish and spit out file info
                    while(fileName.equals("~~NAN~~")){}
                    System.out.println("File name: " + fileName);

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
                    System.out.printf(Constants.CYAN + "File type: %s\n" + Constants.RESET, (isDir == 0) ? "file":"folder");
                    System.out.printf(Constants.CYAN + "File Size: %.1f kilobytes\n" + Constants.RESET, fileSize/1000.0);
                    System.out.println(Constants.CYAN + "Saving file to " + filePath + Constants.RESET);

                    // buffer for incoming data
                    byte[] contents = new byte[30000];
                    // This will become the byte count in one read() call -
                    int byteCount = 0;

                    // write data to file as it comes in
                    while ((byteCount = sockStream.read(contents)) != -1)
                        outputStream.write(contents, 0, byteCount);

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
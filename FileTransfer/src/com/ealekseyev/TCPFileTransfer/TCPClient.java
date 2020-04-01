package com.ealekseyev.TCPFileTransfer;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    public void send(String address, String path) throws Exception {
        Thread getNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket nameSock = new Socket(address, Constants.namePort);
                    DataOutputStream nameOutput = new DataOutputStream(nameSock.getOutputStream());

                    String name = path.split("/")[path.split("/").length - 1];
                    nameOutput.writeUTF(name);

                    // if it is directory, also send length
                    byte isDir = 0;
                    if (new File(path).isDirectory()) {
                        isDir = 1;
                        nameOutput.writeUTF(Byte.toString(isDir));
                        nameOutput.writeUTF("-1");
                    } else {
                        nameOutput.writeUTF(Byte.toString(isDir));
                        // its length
                        nameOutput.writeUTF(Long.toString(new File(path).length()));
                    }
                    nameOutput.close();
                    nameSock.close();
                } catch (Exception e) {
                    // print the stackTrace in ANSI_RED
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });

        /*
        END INFO TRANSMISSION
         */

        Thread sendFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize socket
                    Socket socket = new Socket(address, Constants.port);

                    //Specify the file
                    File file = new File(path);
                    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                    //Get socket's output stream
                    OutputStream outputStream = socket.getOutputStream();

                    // Read File Contents into contents array
                    byte[] contents;
                    long fileLength = file.length();
                    long current = 0;

                    // Send file
                    long start = System.currentTimeMillis();
                    long prevCurrent = 0;
                    while (current != fileLength) {
                        int size = 30000;
                        if (fileLength - current >= size)
                            current += size;
                        else {
                            size = (int) (fileLength - current);
                            current = fileLength;
                        }
                        contents = new byte[size];
                        inputStream.read(contents, 0, size);
                        outputStream.write(contents);
                        if (((current * 100) / fileLength) - prevCurrent == 5) {
                            System.out.println(Constants.YELLOW + "Sending file ... " + (current * 100) / fileLength + "% complete!" + Constants.RESET);
                            prevCurrent = (current * 100) / fileLength;
                        }
                    }

                    outputStream.flush();
                    socket.close();

                    // feedback - speed and time
                    System.out.println(Constants.CYAN + "Done!" + Constants.RESET);
                    System.out.printf(Constants.CYAN + "Total time elapsed: %d seconds\n" + Constants.RESET, (System.currentTimeMillis() - start) / 1000);
                    System.out.printf(Constants.CYAN + "Approximate speed: %.1f kilobytes per second.\n" + Constants.RESET, (fileLength/1000.0) / ((System.currentTimeMillis() - start) / 1000.0));
                    System.out.println(Constants.GREEN + "Success!" + Constants.RESET);
                } catch (Exception e) {
                    // print the stackTrace in ANSI_RED
                    System.out.println(Constants.RED + e + Constants.RESET);
                }
            }
        });

        getNameThread.start();
        sendFileThread.start();
    }
}

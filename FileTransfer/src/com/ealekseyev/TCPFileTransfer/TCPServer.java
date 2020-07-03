package com.ealekseyev.TCPFileTransfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private ServerSocket ssock;
    public void listenAndAccept(int port) {
        try {
            ssock = new ServerSocket(port);
        } catch (IOException e) {
            Errors.throwError(e);
        }
        while(true) {
            try {
                Socket client = ssock.accept();
                new Thread(new UserThreadLauncher(client)).start();
            } catch(Exception e) {
                Errors.throwError(e);
            }
        }
    }
}
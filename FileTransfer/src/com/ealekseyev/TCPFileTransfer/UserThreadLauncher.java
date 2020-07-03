package com.ealekseyev.TCPFileTransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class UserThreadLauncher implements Runnable {
    private Socket sock;
    private DataInputStream sockIn;
    private DataOutputStream sockOut;
    private DataOutputStream fileReader;
    public UserThreadLauncher(Socket sock) throws Exception{
        this.sock = sock;
        sockIn = new DataInputStream(sock.getInputStream());
        sockOut = new DataOutputStream(sock.getOutputStream());

    }
    public void run() {
        String filename = "";
        long fileSize = -1;
        try {
            filename = sockIn.readUTF();
            fileSize = sockIn.readLong();
        } catch (Exception e) {
            Errors.throwError(e);
            return;
        }
    }
}

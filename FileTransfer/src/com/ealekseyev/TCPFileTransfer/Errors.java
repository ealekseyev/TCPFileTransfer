package com.ealekseyev.TCPFileTransfer;

public class Errors {
    public static void throwError(Exception e) {
        System.out.println(e.getStackTrace().toString());
    }
    public static void throwFatalError(Exception e) {
        System.out.println(e.getStackTrace().toString());
        // stop the program with failed status code (unix)
        // System.exit(1);
    }
}

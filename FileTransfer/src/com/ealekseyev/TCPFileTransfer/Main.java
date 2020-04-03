package com.ealekseyev.TCPFileTransfer;

import java.io.File;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        System.out.println("System: " + Constants.CYAN + System.getProperty("os.name") + Constants.RESET);
        Scanner scanner = new Scanner(System.in);
        if(args.length != 0) {
            if (args[0].strip().toLowerCase().equals("server")) {
                selectServer();
            } else if (args[0].strip().toLowerCase().equals("client")) {
                selectClient(scanner);
            } else {
                selectMode(scanner);
            }
        } else {
            selectMode(scanner);
        }
    }

    private static void selectMode(Scanner scanner) {
        System.out.print("Mode - 0 for server, 1 for client > ");
        String mode = scanner.nextLine().strip();
        if(mode.equals("0")) {
            selectServer();
        } else if(mode.equals("1")) {
            selectClient(scanner);
        }
        else {
            System.out.println("Invalid value entered.");
            selectMode(scanner);
        }
    }

    private static void selectClient(Scanner scanner) {
        // get and format path
        System.out.print("Full path of file to be sent > ");
        String path = scanner.nextLine().strip();
        if(!(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)) {
            path = path.replaceAll("\\\\", "");
        }
        // check if it exists, break if not
        if(!new File(path).exists()) {
            System.out.println("E: no such file or directory");
            System.exit(0);
        }
        // separate into directory and file
        if(new File(path).isDirectory()) {
            System.out.println("File type: directory");
            try {
                System.out.println("Children:");
                ArrayList<String> children = OtherFunctions.folderScanner(path);
                for (String child : children) {
                    System.out.println("\t" + child);
                }
            } catch (Exception e) {
                System.out.println(Constants.RED + "E: could not fetch directory listing" + Constants.RESET);
                System.out.println(Constants.RED + e + Constants.RESET);
            }
        } else {
            System.out.println("File type: file");
        }
        // get IP address of server/listener
        System.out.print("IP Address of server > ");
        String address = scanner.nextLine().strip();
        try {
            new TCPClient().send(address, path);
        } catch (Exception e) {
            System.out.println(Constants.RED + e + Constants.RESET);
        }
    }

    private static void selectServer() {
        System.out.println("Listening...");
        try {
            new TCPServer().listen();
        } catch (Exception e) {
            System.out.println(Constants.RED + e + Constants.RESET);
        }
    }
}


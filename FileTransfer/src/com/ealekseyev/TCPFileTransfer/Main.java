package com.ealekseyev.TCPFileTransfer;

import java.io.File;
import java.util.Scanner;

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
        System.out.print("Full path of file to be sent > ");
        String path = scanner.nextLine().strip();
        if(!(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)) {
            path = path.replaceAll("\\\\", "");
        }
        System.out.print("IP Address of server > ");
        String address = scanner.nextLine().strip();
        try {
            new TCPClient().send(address, path);
        } catch (Exception e) {
            System.out.println(Constants.RED + e + Constants.RESET);
        }
        /*try {
            if(new File(path).isDirectory() && !path.contains(".zip")) {
                FileZipper.zipDirectory(new File(path), path+".zip");
                new TCPClient().send(address, path+".zip");
            } else {
                new TCPClient().send(address, path);
            }
        } catch (Exception e) {
            System.out.println(Constants.RED + e + Constants.RESET);
        }*/
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


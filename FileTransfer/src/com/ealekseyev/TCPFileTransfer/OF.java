package com.ealekseyev.TCPFileTransfer;

import java.util.*;
import java.io.*;
import java.io.InvalidObjectException;

public class OF {
    // not an entire path
    public static String newFileName(String fileName) {
        fileName = fileName.strip();
        // test if theres a "(#)" at the end
        int leftPInd = fileName.lastIndexOf('(');
        int rightPInd = fileName.lastIndexOf(')');
        String extension = "";
        // store extension
        if (fileName.length() - fileName.indexOf('.') < 6) {
            //System.out.println("found extension");
            extension = fileName.substring(fileName.indexOf('.'), fileName.length());
            fileName = fileName.substring(0, fileName.indexOf('.')).strip();
            //System.out.println("filename is now " + fileName);
        }

        if(leftPInd < rightPInd) {
            //System.out.println("parenthesis in right place");
            // get the file number
            if (leftPInd != -1 && rightPInd != -1) {
                //System.out.println("both parenthesis exist");
                try {
                    //System.out.println(fileName.substring(leftPInd+1, rightPInd));
                    int copyNum = Integer.parseInt(fileName.substring(leftPInd+1, rightPInd));
                    copyNum++;
                    fileName = fileName.substring(0, leftPInd) + "(" + Integer.toString(copyNum) + ")";
                    fileName += extension;

                } catch (NumberFormatException n) {
                    //System.out.println("num exception");
                    fileName += " (0)" + extension;
                }
            } else {
                //System.out.println("1st else");
                fileName += " (0)" + extension;
            }
        } else {
            //System.out.println("second else");
            fileName += " (0)" + extension;
        }
        //System.out.println(fileName);
        return fileName;
    }

    // add functionality for empty folders
    public static ArrayList<String> folderScanner(String path) throws Exception {
        ArrayList<String> filePaths = new ArrayList<>();
        String name = path.split("/")[path.split("/").length - 1];

        File folder = new File(path);
        File[] children = folder.listFiles();

        for(File child: children) {
            // if it is a directory
            if(child.isDirectory()) {
                // if it is an empty directory
                if(child.list().length == 0) {
                    // indicate it is an empty directory with .EMDIR for server
                    filePaths.add(child.getPath() + "/EMDIR");
                    new File(child.getPath() + "/EMDIR").createNewFile();
                } else {
                    // recursively find all nested files
                    filePaths.addAll(folderScanner(child.getPath()));
                }
            } else {
                // if not a directory, append the file
                filePaths.add(child.getPath());
            }
        }
        return filePaths;
    }
    public static String getFirstEntry(String path) {
        if(path.charAt(0) == '/') {
            return path.split("/")[1];
        }
        return path.split("/")[0];
    }
    public static String getLastEntry(String path) {
        return path.split("/")[path.split("/").length-1];
    }
    public static String stripFirstEntry(String path) {
        // ensure it doesn't get stuck at a slash
        path = path.substring(1);
        return path.substring(path.indexOf("/")+1);
    }
    public static String stripLastEntry(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static void printSize(long fileSize) {
        if(fileSize < 1000) {
            System.out.println("File size: " + Constants.CYAN + Long.toString(fileSize) + " bytes" + Constants.RESET);
        } else if(fileSize < 500000 && fileSize >= 1000) {
            System.out.printf("File size: " + Constants.CYAN + "%.1f kilobytes" + Constants.RESET + "\n", fileSize/1000.0);
        } else if(fileSize < 500000000 && fileSize >= 500000) {
            System.out.printf("File size: " + Constants.CYAN + "%.1f megabytes" + Constants.RESET + "\n", fileSize/1000000.0);
        } else if(fileSize >= 500000000) {
            System.out.printf("File size: " + Constants.CYAN + "%.1f gigabytes" + Constants.RESET + "\n", fileSize/1000000000.0);
        }
    }

    public static ArrayList<File> getFileTree(File dir) throws InvalidObjectException {
        ArrayList<File> files = new ArrayList<File>();
        if(!dir.isDirectory()) {
            throw new InvalidObjectException("File object passed was not a directory");
        } else {
            for(File f: dir.listFiles())
                files.add(f);
            while(true) {

            }
        }
    }
}


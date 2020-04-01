package com.ealekseyev.TCPFileTransfer;

public class OtherFunctions {
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
}


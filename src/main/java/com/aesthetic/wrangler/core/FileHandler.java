package com.aesthetic.wrangler.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static ArrayList<File> selectedFiles;

    static {
        selectedFiles = new ArrayList<>();
    }

    public static void addFiles(List<File> files) {
        if (files != null) {
            clearFiles();
            selectedFiles.addAll(files);
        }
    }
    public static void clearFiles() {
        selectedFiles.clear();
    }
    public static String trimFilename(String filename) {
        int pos = filename.lastIndexOf(".");
        String trimmedFilename = filename.substring(0, pos);
        return trimmedFilename;
    }
    public static String getExtension(String filename) {
        int pos = filename.lastIndexOf(".");
        String extension = filename.substring(pos+1, filename.length());
        return extension;
    }
    public static ArrayList<File> getSelectedFiles() {
        return selectedFiles;
    }
}

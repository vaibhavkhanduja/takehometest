package com.druid.interview.takehometest;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
  File Manager Class
 */
public class FileManager {

    /**
     * Temp folder to layout storage is it exceed threshold
     */
    private static final String TEMP_FOLDER = "/tmp/druid/";

    /**
     * Utility function to create temprory file
     */
    protected static void createTempFolder() {
        File directory = new File(TEMP_FOLDER);
        if (! directory.exists()){
                directory.mkdir();
        }
    }


    /**
     * Write value to the given file
     * @param filename
     * @param value
     */
    protected static void write(String filename, HashSet<String> value)  {
        createTempFolder();
        //check if file exists
        File tmpfile = new File(TEMP_FOLDER +
                                 String.valueOf(Math.abs(filename.hashCode())));
        try {
            boolean newfile = tmpfile.createNewFile() ? true : false;
            FileWriter  writer = new FileWriter(tmpfile);
            if (newfile)
                writer.write(filename + System.lineSeparator());
            writer.append(value.toString() + System.lineSeparator());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns List of TEMP folder ordered with size
     * @return
     * @throws IOException
     */
    protected static List<Path> getOrderedFileList() throws IOException {
        List<Path> paths = Files.walk(Paths.get(TEMP_FOLDER))
                .filter(Files::isRegularFile)
                .sorted((Path a, Path b) -> a.toFile().length() > b.toFile().length() ? 1 : -1)
                .collect(Collectors.toList());
        return paths;
    }

    /**
     * Get the contents of path into file
     * @param path
     * @param writer
     */
    protected static void printFileContents(Path path,
                                            FileWriter writer,
                                            int n) {
        try {
            HashSet<String> values = new HashSet<String>();
            // This should be small file and we should be able to read all.
            if (Files.lines(path).count() > n) {
                Stream<String> lines = Files.lines(path);
                lines.forEach(line -> {
                    try {
                        writer.append(lines + ",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.append(System.lineSeparator());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean up Temp folder
     */
    public static void cleanUP() {
        File index = new File(TEMP_FOLDER);
        if (index.exists()) {
            for (File path: index.listFiles())
                path.delete();
            index.delete();
        }
    }

    /**
     * Write contents of storage to a file named with hash of the key
     * @param storage
     */
    public static void writeToFile(HashMap<String,HashSet<String>> storage) {
        for (Entry<String,HashSet<String>> entry : storage.entrySet()) {
            write(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Print contents of top howMany files
     * @param n
     * @param outfile
     */
    public static void printExternalContents(int n, String outfile) {
        File file = new File(outfile);
        try {
            List<Path> dir = getOrderedFileList();
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < dir.size(); i++)
                printFileContents(dir.get(i), writer, n);
            writer.append(System.lineSeparator());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write storage to the file
     * @param minSize
     * @param storage
     * @param outfile
     */
    public static void printInternalStorage(int minSize,
                                            HashMap<String, HashSet<String>> storage,
                                            String outfile) {
        File file = new File(outfile);
        try {
            FileWriter writer = new FileWriter(file);
            for (Entry<String, HashSet<String>> entry: storage.entrySet()) {
                if (entry.getValue().size() >= minSize)
                    writer.append("UserID: " + entry.getKey()
                         + " File Paths: " + entry.getValue().toString() + System.lineSeparator());
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

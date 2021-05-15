package com.druid.interview.takehometest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.HashSet;

public class FileEngine {

    /**
     * Default values some command line parameters.
     */
    private final String infile = "infile";
    private final String defaultInFile = "access.log";
    private final String defaultOutFile = "output.log";
    private final String defaultOutDirectory = "/tmp/";
    private final String outDir = "outdir";
    private final String noOfRows = "no_of_rows";
    private final String defaultNoOfRows = "10";

    // Internal Storage Used for finding records. The key is userid
    // The value is set of unique paths
    private HashMap<String, HashSet<String>> storage = new HashMap<>();
    // Command Line Arguments sent it
    private HashMap<String, String> arguments = new HashMap<>();
    // Default value of max storage size
    private long maxStorageSize = 0;
    // boolean to understand we exceeded memory and dumped records.
    private boolean wasStorageDumped = false;

    /**
     * Print the storage in the a file
     * @param n
     * @param output
     */
    private void print(int n, String output)  {
        if (wasStorageDumped) {
            clearStorage();
            FileManager.printExternalContents(n, output);
            FileManager.cleanUP();
        } else {
            FileManager.printInternalStorage(n, storage, output);
        }
    }

    /**
     * Clear in memory storage
     */
    private void clearStorage() {
        FileManager.writeToFile(storage);
        storage.clear();
        storage = null;
        System.gc();
        storage = new HashMap<>();
    }

    /**
     * Check and clear storage
     */
    private void checkAndClearMem() {
        if (storage.size() >= maxStorageSize) {
            clearStorage();
            wasStorageDumped = true;
        }
    }

    /**
     * Utility function to get chuck size
     * @param lineSize
     * @return
     */
    private long getChunkSize(int lineSize) {
        return (long)estimateAvailableMemory()/lineSize;
    }

    /**
     * Utility function to estimate available
     * memory
     * @return available memory
     */
    private long estimateAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        long presFreeMemory = r.maxMemory() - allocatedMemory;
        return presFreeMemory;
    }

    /**
     * Process input string to be stored in local
     * Storage
     * @param line
     */
    private void processLine(String line) {
        checkAndClearMem();
        Log log = new Log(line);
        HashSet<String> logPaths =
                storage.containsKey(log.userID) ?
                        storage.get(log.userID) : new HashSet<>();
        logPaths.add(log.path);
        storage.put(log.userID, logPaths);
    }

    /**
     * Default Constructor
     */
    public FileEngine() { }

    /**
     * Print usage of the Utility
     */
    public static void printUsage() {
        System.out.println("--------------------------------------------------------------");
        System.out.println(" Program reads values from a large file and find userid of the " +
                           " N distinct paths.");
        System.out.println(" FileEngine -f <filePath> -n <minimum no of rows> -o <out directory>");
        System.out.println("--------------------------------------------------------------");
    }


    /**
     * Process Command Line Arguments
     * @param args
     * @return
     */
    public HashMap<String,String> processCommandLineArgs(String[] args) {
       HashMap<String, String> arguments = new HashMap<>();
       for (int i = 0; i < args.length; i++) {
           switch (args[i]) {
               case "-f":
                   i +=1;
                   arguments.put(infile, args[i]);
                   break;
               case "-n":
                   i +=1;
                   arguments.put(noOfRows, args[i]);
                   break;
               case "-o":
                   i += 1;
                   arguments.put(outDir, args[i]);
                   break;
           }
       }
       return arguments;
    }


    /**
     * Process incoming arguments and file
     * @param args
     */
    public void process(String[] args) {
        arguments = processCommandLineArgs(args);
        Path input = Paths.get(arguments.getOrDefault(infile, defaultInFile));
        String output = arguments.getOrDefault(outDir,
                                     defaultOutDirectory) + defaultOutFile;
        int size = Integer.valueOf(arguments.getOrDefault(noOfRows, defaultNoOfRows));
        try {
            Stream<String> lines = Files.lines(input);
            maxStorageSize = getChunkSize(size);
            lines.forEach(line -> processLine(line));
            print(size, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String...args) {

        if (args.length < 4) {
            FileEngine.printUsage();
            System.exit(-1);
        }

        FileEngine fe = new FileEngine();
        fe.process(args);
    }
}
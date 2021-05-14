package com.druid.interview.takehometest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FileEngine {

    private HashMap<String, String> storage = new HashMap<>();
    private long maxStorageSize = 0;

    public static final String infile = "infile";
    public static final String defaultInFile = "access.log";
    public static final String defaultOutFile = "/tmp/outfile.txt";
    public static final String outfile = "outfile";
    public static final String noOfRows = "no_of_rows";
    public static final String defaultNoOfRows = "10";
    public static final Integer defaultLineSize = 100;


    private void printTopN(int n, Path output)  {
        try {
            Files.createFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Entry<String, String> entry : storage.entrySet()) {
            System.out.println("Path : " + entry.getKey() + " " + "User Id " + entry.getValue());
            try {
                Files.writeString(output, "Path : " + entry.getKey()
                                           + " " + "User Id " + entry.getValue() + System.lineSeparator(),
                                 StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAndClearMem() {
         // TBD Get right memory size
        if (storage.size() >= maxStorageSize) {
            FileManager.writeToFile(storage);
            storage.clear();
            storage = null;
            System.gc();
            storage = new HashMap<>();
        }
    }

    private void process(String line) {
        //Check for storage if it has reached limit
        // We dump the records in their individual files
        // and clean up memory.
        checkAndClearMem();
        Log log = new Log(line);
        if (storage.containsKey(log.path)) {
            String userId = storage.get(log.path);
            userId += ",";
            userId += log.userID;
            storage.put(log.path, userId);
        } else {
            storage.put(log.path, log.userID);
        }
    }
    public static void printUsage() {
        System.out.println("--------------------------------------------------------------");
        System.out.println(" Program reads values from a large file and find userid of the " +
                           " N distinct paths.");
        System.out.println(" FileEngine -f <filePath> -n <size>");
        System.out.println("--------------------------------------------------------------");
    }

    private long estimateAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        long presFreeMemory = r.maxMemory() - allocatedMemory;
        return presFreeMemory;
    }

    public long getChunkSize(int lineSize) {
        return (long)estimateAvailableMemory()/lineSize;
    }

    public Map<String, String> processCommandLineArgs(String[] args) {
       Map<String, String> arguments = new HashMap<>();
       for (int i = 0; i < args.length; i++) {
           switch (args[i]) {
               case "-i":
                   i +=1;
                   arguments.put(infile, args[i]);
                   break;
               case "-n":
                   i +=1;
                   arguments.put(noOfRows, args[i]);
                   break;
               case "-o":
                   i += 1;
                   arguments.put(outfile, args[i]);
                   break;
           }
       }
       return arguments;
    }

    public FileEngine() { }

    public void processFile(Path input, Path output, int size) {
        try {
            Stream<String> lines = Files.lines(input).parallel();
            maxStorageSize = getChunkSize(size);
            lines.forEach(line -> process(line));
            System.out.println("Storage size " + storage.size());
            printTopN(Math.min(storage.size(), size), output);
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
        Map<String, String> arguments = fe.processCommandLineArgs(args);
        Path input = Paths.get(arguments.getOrDefault(FileEngine.infile, defaultInFile));
        Path output = Paths.get(arguments.getOrDefault(FileEngine.outfile, FileEngine.defaultOutFile));
        fe.processFile(input, output,
                Integer.valueOf(arguments.getOrDefault(FileEngine.noOfRows, FileEngine.defaultNoOfRows)));
    }
}
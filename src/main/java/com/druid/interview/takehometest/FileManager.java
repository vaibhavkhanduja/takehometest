package com.druid.interview.takehometest;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class FileManager {


    private static void write(String filename, String value)  {
        //check if file exists
        File output = new File(String.valueOf(Math.abs(filename.hashCode())));
        try {
            FileWriter  writer = new FileWriter(output);
            writer.append(value);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(Map<String,String> map) {
        for (Entry<String,String> entry : map.entrySet()) {
            write(entry.getKey(), entry.getValue());
        }
    }
}

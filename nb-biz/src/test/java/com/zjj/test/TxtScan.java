package com.zjj.test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by admin on 2017/3/22.
 */
public class TxtScan {

    public static void main(String[] args) {
        io();
    }

    public static void io() {
        Map<String, Set<String>> map = new HashMap<>();
        String fileName = "/Users/admin/Desktop/error.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                line = line.substring(line.indexOf("{"));
                if (line.contains("1000000")) {
                    if (map.containsKey("1000000")) {
                        map.get("1000000").add(line);
                    } else {
                        Set<String> set = new HashSet<>();
                        set.add(line);
                        map.put("1000000", set);
                    }
                } else if (line.contains("1000002")) {
                    if (map.containsKey("1000002")) {
                        map.get("1000002").add(line);
                    } else {
                        Set<String> set = new HashSet<>();
                        set.add(line);
                        map.put("1000002", set);
                    }
                }
                line = reader.readLine();
            }
            for (String key : map.keySet()) {
                for (String str : map.get(key)) {
                    System.out.println(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void nio() {
        String fileName = "/Users/admin/Desktop/error.txt";
        int bufferSize = 100;
        String enter = "\n";
        try {
            FileChannel channel = new RandomAccessFile(fileName, "r").getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            byte[] bytes = new byte[bufferSize];
            while (channel.read(buffer) != -1) {
                int rSize = buffer.position();
                buffer.rewind();
                buffer.get(bytes);
                buffer.clear();
                String tempStr = new String(bytes, 0, rSize);
                int fromIndex = 0;
                int endIndex = 0;
                while ((endIndex = tempStr.indexOf(enter, fromIndex)) != -1) {
                    String line = tempStr.substring(fromIndex, endIndex);
                    System.out.println(line);
                    fromIndex = endIndex + 1;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.zjj.nb.biz.nio;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

/**
 * Created by jinju.zeng on 2017/4/6.
 */
public class ChannelToChannel {

    public static void main(String args[]){
        try {
            Path path= Paths.get("/Users/admin/test/AccountAmountService.java");
            if(Files.notExists(path)){
                Files.createFile(path);
            }
            FileInputStream in=new FileInputStream("/Users/admin/test/service/AccountAmountService.java");
            FileOutputStream out=new FileOutputStream("/Users/admin/test/AccountAmountService.java");

            FileChannel readChannel=in.getChannel();
            readChannel.transferTo(0,readChannel.size(), out.getChannel());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

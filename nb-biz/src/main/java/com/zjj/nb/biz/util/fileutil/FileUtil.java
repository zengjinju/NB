package com.zjj.nb.biz.util.fileutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jinju.zeng on 2017/4/6.
 */
public class FileUtil {

    /**
     * 该方法使用nio中的channel-to-channel技术实现文件的快速拷贝，
     * 具有传输效率高的特点
     *
     * @param source
     * @param target
     * @return
     */
    public static void fileCopy(Path source, Path target) {
        try {
            if (Files.notExists(source)) {
                throw new RuntimeException("资源文件不存在" + source.getFileName());
            }
            if (Files.notExists(target)) {
                //当目标文件不存在时，默认创建目标文件
                Files.createFile(target);
            }
            FileChannel readChannel = new FileInputStream(source.toFile()).getChannel();
            FileChannel writeChannel = new FileOutputStream(target.toFile()).getChannel();
            readChannel.transferTo(0, readChannel.size(), writeChannel);
            readChannel.close();
            writeChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Path source = Paths.get("/Users/admin/test/service/AccountAmountService.java");
        Path target = Paths.get("/Users/admin/test/service/abc.txt");
        fileCopy(source, target);
    }
}

package com.zjj.nb.biz.util.fileutil;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * 这个工具的作用是扫描某个路径下面的文件，统计文件的个数
 * 优势是该工具使用了fork/join框架，使用多线程的方式来统计
 * 提高了效率
 * Created by jinju.zeng on 2017/3/16.
 */
@Slf4j
public class FileScanUtil {

    private static class FileTask extends RecursiveAction {
        //指定的文件夹路径
        @Setter
        private Path path;
        //文件的扩展名(后缀)
        @Setter
        private String extension;

        public FileTask(Path path, String extension) {
            this.path = path;
            this.extension = extension;
        }

        public FileTask() {
        }

        @Override
        protected void compute() {
            List<FileTask> list = new ArrayList<>();
            try {
                DirectoryStream<Path> ds = Files.newDirectoryStream(path);
                for (Path subPath : ds) {
                    String fileName = subPath.getFileName().toString();
                    //判断当前的path是否是目录
                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                        //对每个子目录新建一个fork子任务
                        list.add(new FileTask(subPath, extension));
                    } else if (fileName.endsWith(extension)) {
                        fileName = fileName.substring(0, fileName.indexOf("."));
                        /**
                         * 这里为什么要对fileName.intern()加锁而不对fileName加锁(个人见解)
                         *  1. 由于fileName是一个局部变量在多线程环境下对fileName加synchronize锁，等同于加在不同的对象上
                         *  2. 为什么要使用intern()方法，通过对intern()方法的工作机制的了解(先从缓冲池中查找是否有相同字符串的对象，如果
                         *     没有则将该字符串放到缓冲池里面，并返回这个对象在缓冲池中的地址；如果有则直接返回这个对象的地址)，所以通过这个
                         *     方法在多线程环境下对相同字符串加的是同一个对象的锁。
                         * (这个synchronize锁相当于锁分段技术的实现，对有相同字符串的对象加锁，不同的字符串之间不存在竞争锁的情况)
                         */
                        synchronized (fileName.intern()) {
                            if (!map.containsKey(fileName)) {
                                map.put(fileName, 1);
                            } else {
                                int count = map.get(fileName);
                                count++;
                                map.put(fileName, count);
                            }
                        }
                    }
                }

                if (!list.isEmpty()) {
                    // 在当前的 ForkJoinPool 上调度所有的子任务
                    for (FileTask task : invokeAll(list)) {
                        task.join();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
    private static final ForkJoinPool pool = new ForkJoinPool();
    private static final FileTask task = new FileTask();
    private Path path;
    private String extension;

    public FileScanUtil(Path path, String extension) {
        this.path = path;
        this.extension = extension;
        try {
            task.setExtension(extension);
            task.setPath(path);
            //使用forkJoinPool执行给定的任务
            Future result = pool.submit(task);
            //等待任务的执行完成
            result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public Integer count() {
        return map.size();
    }

    public String getFileCount(String fileName) {
        if (map.containsKey(fileName)) {
            return String.valueOf(map.get(fileName));
        }
        return null;
    }

    public static void main(String[] args) {
        FileScanUtil scan = new FileScanUtil(Paths.get("/Users/admin/test"), ".java");
        System.out.println(scan.count());
    }
}

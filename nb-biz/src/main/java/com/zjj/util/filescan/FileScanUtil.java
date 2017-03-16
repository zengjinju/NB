package com.zjj.util.filescan;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
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
                        if (!map.containsKey(fileName)) {
                            map.put(fileName, 1);
                        } else {
                            int count = map.get(fileName);
                            count++;
                            map.put(fileName, count);
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
        FileScanUtil scan = new FileScanUtil(Paths.get("/Users/admin/project/qccr/maintaincore"), ".java");
        System.out.println(scan.count());
    }
}

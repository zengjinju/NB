package com.zjj.nb.biz;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jinju.zeng on 2017/3/16.
 * <p>
 * 使用fork/join框架用多线程的方式实现统计某个目录下面的.java文件的个数，相同文件名的个数
 */
public class ClassFileScanUtil {

    private static final ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String FILE_PATH = "/Users/admin/project/qccr/merchantcore";
        ForkJoinPool pool = new ForkJoinPool();
        CountService tast = new CountService(Paths.get(FILE_PATH));
        Future result = pool.submit(tast);
        result.get();
        System.out.println(countMap.size());
        for (Map.Entry<String, Integer> item : countMap.entrySet()) {
            //System.out.println(item.getKey() + "--" + item.getValue());
            if (item.getValue() > 1) {
                System.out.println(item.getKey() + "--" + item.getValue());
            }
        }
    }

    static class CountService extends RecursiveAction {
        private Path path = null;

        public CountService(Path path) {
            this.path = path;
        }

        @Override
        protected void compute() {
            List<CountService> list = new ArrayList<>();
            try {
                DirectoryStream<Path> ds = Files.newDirectoryStream(path);
                for (Path subPath : ds) {
                    String fileName = subPath.getFileName().toString();
                    //判断当前的path是否是目录
                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                        //对每个子目录新建一个fork子任务
                        list.add(new CountService(subPath));
                    } else if (fileName.endsWith("java")) {
                        fileName = fileName.substring(0, fileName.indexOf("."));
                        synchronized (fileName.intern()) {
                            if (!countMap.containsKey(fileName)) {
                                countMap.put(fileName, 1);
                            } else {
                                int count = countMap.get(fileName);
                                count++;
                                countMap.put(fileName, count);
                            }
                        }
                    }
                }

                if (!list.isEmpty()) {
                    // 在当前的 ForkJoinPool 上调度所有的子任务
                    for (CountService service : invokeAll(list)) {
                        service.join();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}

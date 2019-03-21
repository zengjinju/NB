package com.zjj.nb.biz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * 查找指定package下的类
 * @author zengjinju
 * @date 2019/3/21 下午2:22
 */
public class Scanner {
	private static final Logger logger = LoggerFactory.getLogger(Scanner.class);
	/**
	 * 从指定package中获取所有的class
	 * @param packageName
	 * @param <T>
	 * @return
	 */
	public Set<Class<?>> getClasses(String packageName){
		if (packageName == null || "".equals(packageName)){
			return null;
		}
		Set<Class<?>> classSet = new HashSet<>(256);
		//判断是否要迭代
		Boolean recursive = Boolean.TRUE;
		//获取包所在的路径
		String packageDirName = packageName.replace(".","/");
		Enumeration<URL> dirs;
		try{
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()){
				//获取下一个元素
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)){
					//获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
					//扫描包下的所有.class文件
					findAndAddClassesPackageByFile(packageName,filePath,recursive,classSet);
				}
			}
		}catch (Exception e){
			logger.error("load {} class error",packageName);
		}
		return classSet;
	}

	/**
	 *
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 * @param <T>
	 */
	public void findAndAddClassesPackageByFile(String packageName, String packagePath, Boolean recursive,Set<Class<?>> classes){
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()){
			logger.warn("用户定义包名{}下没有任何文件",packageName);
			return;
		}
		//获取目录下的子目录或者.class文件
		File[] dirFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (recursive && pathname.isDirectory()) || pathname.getName().endsWith(".class");
			}
		});
		for (File file : dirFiles){
			if (file.isDirectory()){
				findAndAddClassesPackageByFile(packageName+"."+file.getName(),file.getAbsolutePath(),recursive,classes);
			} else {
				String className = file.getName().substring(0,file.getName().lastIndexOf("."));
				try{
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className));
				}catch (Exception e){
					logger.error("load class error");
				}
			}
		}
	}

	public Map<String,Class<?>> getClassByAnnotation(){
		Map<String,Class<?>> map = new HashMap<>(16);
		Set<Class<?>> classes = getClasses("com.zjj.nb.biz");
		if (classes == null || classes.size() == 0){
			return map;
		}
		for (Class<?> clazz : classes){
			Service annotation = clazz.getAnnotation(Service.class);
			if (annotation != null){
				char[] chars = clazz.getSimpleName().toCharArray();
				chars[0] = Character.toLowerCase(chars[0]);
				String className = String.valueOf(chars);
				String key = !"".equals(annotation.value()) ? annotation.value() : className;
				map.put(key,clazz);
			}
		}
		return map;
	}

	public static void main(String[] args){
		Scanner scanner = new Scanner();
		Map<String,Class<?>> classSet = scanner.getClassByAnnotation();
		System.out.println(classSet);
	}
}

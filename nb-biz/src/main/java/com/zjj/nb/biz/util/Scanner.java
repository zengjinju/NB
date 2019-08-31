package com.zjj.nb.biz.util;

import com.zjj.nb.biz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
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
	 * @param
	 * @return
	 */
	public Set<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotationClass){
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
					findClass(packageName,filePath,classSet,(Class<?> clazz)->{
						return clazz.isAnnotationPresent(annotationClass);
					});
				}
			}
		}catch (Exception e){
			logger.error("load {} class error",packageName);
		}
		return classSet;
	}

	/**
	 * 在指定包下按照指定规则扫码class
	 * @param packageName
	 * @param packagePath
	 * @param classSet
	 * @param filter
	 */
	private void findClass(String packageName,String packagePath,Set<Class<?>> classSet,Filter<Class<?>> filter){
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()){
			logger.warn("用户定义包名{}下没有任何文件",packageName);
			return;
		}
		//获取目录下的子目录或者.class文件
		File[] dirFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (pathname.isDirectory()) || pathname.getName().endsWith(".class");
			}
		});
		for (File file : dirFiles){
			if (file.isDirectory()){
				findClass(packageName+"."+file.getName(),file.getAbsolutePath(),classSet,filter);
			} else {
				String className = file.getName().substring(0,file.getName().lastIndexOf("."));
				try {
					Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className);
					if (filter != null && filter.accept(clazz)){
						classSet.add(clazz);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @param packageName
	 * @param superClass
	 * @return
	 */
	public Set<Class<?>> getClassBySuper(String packageName,Class<?> superClass){
		if (packageName == null || "".equals(packageName)){
			return null;
		}
		Set<Class<?>> classSet = new HashSet<>(256);
		//获取包所在的路径
		String packageDirName = packageName.replace(".","/");
		Enumeration<URL> dirs;
		try{
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()){
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)){
					//获取包所在的物理路径
					String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
					findClass(packageName,filePath,classSet,(Class<?> clazz)->{
						return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
					});
				}
			}
		}catch (Exception e){

		}
		return classSet;
	}


	public static interface Filter<T>{
		Boolean accept(T var);
	}

	public static void main(String[] args){
		Scanner scanner = new Scanner();
		Set<Class<?>> classSet = scanner.getClassBySuper("com.zjj.nb.biz",UserService.class);
		System.out.println(classSet);
	}
}

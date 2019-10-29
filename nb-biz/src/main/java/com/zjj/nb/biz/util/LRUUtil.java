package com.zjj.nb.biz.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于LinkedHashMap实现的LRU算法
 * @author zengjinju
 * @date 2019/10/29 上午10:58
 */
public class LRUUtil<K,V> extends LinkedHashMap<K,V> {
	private Integer maxSize;

	/**
	 * 每次插入数据默认插入到tail链表尾部，accessOrder=true表示被访问的元素也会放到链表tail
	 * @param maxSize
	 */
	public LRUUtil(int maxSize){
		super(maxSize,0.75f,true);
		this.maxSize = maxSize;
	}

	/**
	 *  每次put和putAll新增元素的时候都会触发判断;当下面函数=true时，就删除链表head元素
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() >= maxSize;
	}
}

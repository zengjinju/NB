package com.zjj.nb.biz.bitmap;

/**
 * @author zengjinju
 * @date 2019/11/15 上午11:27
 */
public interface BitMap<T> {

	/**
	 * 添加元素
	 * @param t
	 * @return
	 */
	boolean set (T t);

	/**
	 * 判断是否包含元素
	 * @param t
	 * @return
	 */
	boolean contains(T t);

	/**
	 * 删除元素
	 * @param t
	 * @return
	 */
	boolean remove(T t);

	void reset();
}

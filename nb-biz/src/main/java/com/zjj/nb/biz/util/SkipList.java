package com.zjj.nb.biz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  https://blog.csdn.net/bohu83/article/details/83654524
 * @author zengjinju
 * @date 2019/12/10 上午11:41
 */
public class SkipList<T> {

	/**
	 * 元素数量
	 */
	private int num;

	private int height;
	/**
	 * 表头
	 */
	private SkipListEntry head;

	/**
	 * 表尾
	 */
	private SkipListEntry tail;

	/**
	 * 生成randomLevel用到的概率值
	 */
	private Random r;

	public SkipList(){
		head = new SkipListEntry(Integer.MIN_VALUE,Integer.MIN_VALUE);
		tail = new SkipListEntry(Integer.MAX_VALUE,Integer.MAX_VALUE);
		head.right = tail;
		tail.left = head;
		num = 0;
		height = 0;
		r = new Random();
	}

	/**
	 * 查找元素，直到最底层的原始链表
	 * @param key
	 * @return
	 */
	private SkipListEntry findEntry(Integer key){
		SkipListEntry p = head;
		while (true){
			while (p.right.key != Integer.MAX_VALUE && p.right.key < key){
				p = p.right;
			}
			if (p.down != null){
				p = p.down;
			} else {
				break;
			}
		}
		return p;
	}

	public Integer get(int key){
		SkipListEntry p = findEntry(key);
		return p.key == key ? p.value : null;
	}

	/**
	 * 插入实现
	 * @param key
	 * @param value
	 * @return
	 */
	public Integer insert(int key,int value){
		int i = 0;
		//找到适合插入的位置
		SkipListEntry p = findEntry(key);
		//如果跳表中包括key节点，则进行更新
		if (p.key == key){
			int oldValue = p.value;
			p.value = value;
			return oldValue;
		}
		//如果不存在key节点，则插入
		SkipListEntry newNode = new SkipListEntry(key,value);
		newNode.left = p;
		newNode.right = p.right;
		p.right.left = newNode;
		p.right = newNode;
		//本层操作完毕，看是否需要插入高层
		//抛硬币决定是否上层插入
		while(r.nextDouble() < 0.5){
			//判断是否创建一个新的level
			if (i >= height){
				addEmptyLevel();
			}
			//找到上层最近的一个元素
			while (p.up == null){
				p = p.left;
			}
			p = p.up;
			//添加上层元素节点，除底层元素包含Value之外，其他的节点不包含Value
			SkipListEntry e = new SkipListEntry(key,null);
			e.left = p;
			e.right = p.right;
			e.down = newNode;
			newNode.up = e;
			p.right.left = e;
			p.right = e;
			newNode = e;
			i++;
		}
		num++;
		return null;
	}

	private void addEmptyLevel(){
		SkipListEntry p1 = new SkipListEntry(Integer.MIN_VALUE,null);
		SkipListEntry p2 = new SkipListEntry(Integer.MAX_VALUE,null);
		p1.right = p2;
		p1.down = head;

		p2.left = p1;
		p2.down = tail;

		head.up = p1;
		tail.up = p2;

		head = p1;
		tail = p2;
		height = height + 1;
	}

	/**
	 * 删除元素,如果有更高层的节点，则重复操作也删除掉
	 * @param key
	 * @return
	 */
	public Integer remove(int key){
		SkipListEntry p = findEntry(key);
		if (!p.key.equals(key)){
			return null;
		}
		SkipListEntry up;
		Integer oldValue = p.value;
		while (p!=null){
			up = p.up;
			p.left.right = p.right;
			p.right.left = p.left;
			p = up;
		}
		return oldValue;
	}

	/**
	 * 找出前N位元素
	 * @param n
	 * @return
	 */
	public List<Integer> topN(int n){
		SkipListEntry p = tail;
		while (p.down != null){
			p = p.down;
		}
		int i=0;
		List<Integer> list = new ArrayList<>(n);
		while (p.left != null && i<n){
			p = p.left;
			list.add(p.value);
			i++;
		}
		return list;
	}

	/**
	 * 格式化打印skipList
	 */
	public void printHorizontal() {
		String s = "";
		int i;

		SkipListEntry p;

	     /* ----------------------------------
		Record the position of each entry
		---------------------------------- */
		p = head;

		while ( p.down != null )
		{
			p = p.down;
		}

		i = 0;
		while ( p != null )
		{
			p.pos = i++;
			p = p.right;
		}

	     /* -------------------
		Print...
		------------------- */
		p = head;

		while ( p != null )
		{
			s = getOneRow( p );
			System.out.println(s);

			p = p.down;
		}
	}

	public String getOneRow( SkipListEntry p ) {
		String s;
		int a, b, i;

		a = 0;

		s = "" + p.key;
		p = p.right;


		while ( p != null )
		{
			SkipListEntry q;

			q = p;
			while (q.down != null)
				q = q.down;
			b = q.pos;

			s = s + " <-";


			for (i = a+1; i < b; i++)
				s = s + "--------";

			s = s + "> " + p.key;

			a = b;

			p = p.right;
		}

		return s;
	}


	private static class SkipListEntry{
		public Integer key;

		public Integer value;

		public SkipListEntry right;

		public SkipListEntry left;

		public SkipListEntry down;

		public SkipListEntry up;

		public SkipListEntry(Integer key, Integer value) {
			this.key = key;
			this.value = value;
		}

		public int pos;
	}


	public static void main(String[] args){
		SkipList skipList = new SkipList();
		Random r = new Random();
		for (int i=0;i<20;i++){
			int t = r.nextInt(100);
			skipList.insert(t,t);
		}
		skipList.printHorizontal();
		List<Integer> top3 = skipList.topN(3);
		System.out.println(top3);

	}
}

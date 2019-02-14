package com.zjj.nb.biz.treeutil.trietree;

/**
 * 字典树
 * @author zengjinju
 * @date 2019/2/14 上午10:22
 */
public class TrieTree {
    private static final int SIZE = 26;
    private TrieTreeNode root;

    public TrieTree(){
    	root = new TrieTreeNode();
	}


    private class TrieTreeNode{
		/**
		 * 数量
		 */
		private int num;
		/**
		 * 子节点
		 */
		private TrieTreeNode[] children;
		/**
		 * 是否是最后一个节点
		 */
		private Boolean isEnd;
		/**
		 * 节点元素
		 */
		private char value;

		public TrieTreeNode(){
			num = 1;
			children = new TrieTreeNode[SIZE];
			isEnd = false;
		}
	}

	public void createTrieTree(String str){
    	if (str == null || "".equals(str)){
    		return;
		}
		//临时节点
		TrieTreeNode node = root;
    	char[] letters = str.toCharArray();
    	for (int i=0;i<letters.length;i++){
    		//计算子节点下标
    		int index = letters[i] - 'a';
    		//子节点不存在新建
    		if (node.children[index] == null){
    			node.children[index] = new TrieTreeNode();
    			node.children[index].value = letters[i];
			} else {
    			node.children[index].num++;
			}
			node = node.children[index];
		}
		node.isEnd = Boolean.TRUE;
	}

	public Boolean contain(String str){
    	if (str == null || "".equals(str)){
    		return Boolean.FALSE;
		}
		TrieTreeNode node = root;
    	char[] letters = str.toCharArray();
    	for (int i=0;i<letters.length;i++){
    		int index = letters[i] - 'a';
    		if (node.children[index] != null){
    			node = node.children[index];
			} else {
    			return Boolean.FALSE;
			}
		}
		return node.isEnd;
	}

	public static void main(String[] args){
    	TrieTree trieTree = new TrieTree();
    	String[] values = {"abc","zjj","bba","jdkjfkdjfdkfjdk","iekjdnc"};
    	for (int i=0;i<values.length;i++){
    		trieTree.createTrieTree(values[i]);
		}
		System.out.println(trieTree.contain("jjz"));
	}
}

package com.zjj.nb.biz.treeutil.trietree;

/**
 * 字典树(前缀重复度较高的字符串集合效率高一些)
 * @author zengjinju
 * @date 2019/2/14 上午10:22
 */
public class TrieTree {
    private static final int SIZE = 127;
    private TrieTreeNode root;

    public TrieTree(){
    	root = new TrieTreeNode();
	}


    private class TrieTreeNode{
		/**
		 * 有多少单词通过这个节点,即由根至该节点组成的字符串模式出现的次数
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

		private String content;

		public TrieTreeNode(){
			num = 1;
			children = new TrieTreeNode[SIZE];
			isEnd = false;
		}
	}

	public void createTrieTree(String str,String value){
    	if (str == null || "".equals(str)){
    		return;
		}
		//临时节点
		TrieTreeNode node = root;
    	char[] letters = str.toCharArray();
    	for (int i=0;i<letters.length;i++){
    		//计算子节点下标
    		int index = letters[i] - 0;
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
    	node.content = value;
	}

	public Boolean contain(String str){
    	if (str == null || "".equals(str)){
    		return Boolean.FALSE;
		}
		TrieTreeNode node = root;
    	char[] letters = str.toCharArray();
    	for (int i=0;i<letters.length;i++){
    		int index = letters[i] - 0;
    		if (node.children[index] != null){
    			node = node.children[index];
			} else {
    			return Boolean.FALSE;
			}
		}
		return node.isEnd;
	}

	public String get(String key){
    	if (key == null || "".equals(key)){
    		return null;
		}
		TrieTreeNode node = root;
    	char[] letters = key.toCharArray();
    	for (int i=0;i<letters.length;i++){
    		int index = letters[i] - 0;
    		if (node.children[index] != null){
    			node = node.children[index];
			} else {
    			return null;
			}
		}
		return node.content;
	}

	public static void main(String[] args){
    	TrieTree trieTree = new TrieTree();
    	String[] values = {"ABC:曾金举","AbC_d:java","1234:c++","bdkjfkdjfdkfjdk:php","iekjdnc:GO"};
    	for (int i=0;i<values.length;i++){
    		String[] s = values[i].split(":");
    		trieTree.createTrieTree(s[0],s[1]);
		}
		System.out.println(trieTree.get("AbC_d"));
	}
}

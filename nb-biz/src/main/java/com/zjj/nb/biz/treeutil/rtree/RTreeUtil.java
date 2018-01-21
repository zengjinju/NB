package com.zjj.nb.biz.treeutil.rtree;


import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.fbs.FactoryFlatBuffers;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.davidmoten.rtree.Entries.entry;

/**
 * Created by admin on 2018/1/19.
 * @author zjj
 */
public class RTreeUtil {

	public static void main(String[] args){
		List<Entry<Object, Point>> entries = createRandomEntries(100000);
		int maxChildren = 8;
		long start=System.nanoTime();
		RTree<Object, Point> tree = RTree.maxChildren(maxChildren).<Object,Point>create().add(entries);
		System.out.println("create Rtree time:"+(System.nanoTime()-start));
//		tree.visualize(2000, 2000).save("greek.png");
		start=System.nanoTime();
		List<Entry<Object,Point>> list= tree.search(Geometries.circle(400,200,100)).toList().toBlocking()
				.single();
		System.out.println("search cost time :"+(System.nanoTime()-start));
		for(Entry<Object,Point> entry : list){
			System.out.println(entry.geometry().x()+","+entry.geometry().y());
		}
	}


	private static List<Entry<Object, Point>> createRandomEntries(long n) {
		List<Entry<Object, Point>> list = new ArrayList<Entry<Object, Point>>();
		for (long i = 0; i < n; i++) {
			list.add(randomEntry());
		}
		return list;
	}


	private static Entry<Object, Point> randomEntry() {
		return entry(new Object(),random());
	}

	private static Point random() {
		return Geometries.point((float) Math.random() * 1000, (float) Math.random() * 1000);
	}
}

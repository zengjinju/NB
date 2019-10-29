package com.zjj.nb.biz.treeutil.rtree;


import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.fbs.FactoryFlatBuffers;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import rx.Observable;
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
		int maxChildren = 8;
		RTree<Object, Point> tree = RTree.maxChildren(maxChildren).<Object,Point>create();
		tree.add("test1",Geometries.pointGeographic(120.008384,30.279711));
		tree.add("test2",Geometries.pointGeographic(120.003852,30.277400));
		tree.add("test3",Geometries.pointGeographic(120.003979,30.293121));
		tree.add("test4",Geometries.pointGeographic(120.046643,30.289109));
		tree.add("test5",Geometries.pointGeographic(120.021893,30.243245));

		Long start=System.currentTimeMillis();
		List<Entry<Object,Point>> list= search(tree, Geometries.point(120.002283,30.285632), 2)
				// get the result
				.toList().toBlocking().single();
		System.out.println("search cost time :"+(System.currentTimeMillis()-start));
		for(Entry<Object,Point> entry : list){
			System.out.println(entry.geometry().x()+","+entry.geometry().y());
		}
	}


	public static <T> Observable<Entry<T, Point>> search(RTree<T, Point> tree, Point lonLat,
														 final double distanceKm) {
		// First we need to calculate an enclosing lat long rectangle for this
		// distance then we refine on the exact distance
		final Position from = Position.create(lonLat.y(), lonLat.x());
		Rectangle bounds = createBounds(from, distanceKm);

		return tree
				// do the first search using the bounds
				.search(bounds)
				// refine using the exact distance
				.filter(new Func1<Entry<T, Point>, Boolean>() {
					@Override
					public Boolean call(Entry<T, Point> entry) {
						Point p = entry.geometry();
						Position position = Position.create(p.y(), p.x());
						return from.getDistanceToKm(position) < distanceKm;
					}
				});
	}

	private static Rectangle createBounds(final Position from, final double distanceKm) {
		// this calculates a pretty accurate bounding box. Depending on the
		// performance you require you wouldn't have to be this accurate because
		// accuracy is enforced later
		Position north = from.predict(distanceKm, 0);
		Position south = from.predict(distanceKm, 180);
		Position east = from.predict(distanceKm, 90);
		Position west = from.predict(distanceKm, 270);

		return Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(), north.getLat());
	}
}

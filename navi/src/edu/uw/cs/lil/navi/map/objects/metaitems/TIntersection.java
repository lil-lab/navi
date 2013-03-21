package edu.uw.cs.lil.navi.map.objects.metaitems;

import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.objects.NaviObj;

/**
 * T-intersection structure filter
 * 
 * @author Yoav Artzi
 */
public class TIntersection extends NaviMetaItem {
	
	public static final TIntersection	INSTANCE	= new TIntersection();
	
	private TIntersection() {
		super("T_INTERSECTION");
	}
	
	@Override
	public boolean isValid(Position e) {
		if (e.getHorizon0().getAt() == NaviObj.UNKNOWN_O) {
			return false;
		} else {
			int directionsCounter = e.getForward() == null ? 0 : 1;
			directionsCounter += e.getLeft().getForward() == null ? 0 : 1;
			directionsCounter += e.getRight().getForward() == null ? 0 : 1;
			directionsCounter += e.getBack().getForward() == null ? 0 : 1;
			return directionsCounter == 3;
		}
	}
}

package edu.uw.cs.lil.navi.map.objects.metaitems;

import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.objects.NaviObj;

/**
 * Deadend structure filter
 * 
 * @author Yoav Artzi
 */
public class Deadend extends NaviMetaItem {
	
	public static final Deadend	INSTANCE	= new Deadend();
	
	private Deadend() {
		super("DEADEND");
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
			return directionsCounter == 1;
		}
	}
}

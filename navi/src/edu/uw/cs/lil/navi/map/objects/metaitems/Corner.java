package edu.uw.cs.lil.navi.map.objects.metaitems;

import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.objects.NaviObj;

/**
 * Corner structure filter
 * 
 * @author Yoav Artzi
 */
public class Corner extends NaviMetaItem {
	
	public static final Corner	INSTANCE	= new Corner();
	
	private Corner() {
		super("CORNER");
	}
	
	@Override
	public boolean isValid(Position e) {
		if (e.getHorizon0().getAt() == NaviObj.UNKNOWN_O) {
			return false;
		} else {
			if (e.getForward() != null) {
				return e.getBack().getForward() == null
						&& (e.getLeft().getForward() == null ^ e.getRight()
								.getForward() == null);
			} else if (e.getBack() != null) {
				return e.getForward() == null
						&& (e.getLeft().getForward() == null ^ e.getRight()
								.getForward() == null);
			} else {
				return false;
			}
		}
	}
}

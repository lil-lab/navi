/*******************************************************************************
 * Navi. Copyright (C) 2013 Yoav Artzi
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 ******************************************************************************/
package edu.uw.cs.lil.navi.map.objects.metaitems;

import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.objects.NaviObj;

/**
 * Intersection structure filter
 * 
 * @author Yoav Artzi
 */
public class Intersection extends NaviMetaItem {
	
	public static final Intersection	INSTANCE	= new Intersection();
	
	private Intersection() {
		super("INTERSECTION");
	}
	
	@Override
	public boolean isValid(Position e) {
		if (e.getHorizon0().getAt() == NaviObj.UNKNOWN_O) {
			return false;
		} else if (Corner.INSTANCE.isValid(e)) {
			return true;
		} else {
			int directionsCounter = e.getForward() == null ? 0 : 1;
			directionsCounter += e.getLeft().getForward() == null ? 0 : 1;
			directionsCounter += e.getRight().getForward() == null ? 0 : 1;
			directionsCounter += e.getBack().getForward() == null ? 0 : 1;
			return directionsCounter > 2;
		}
	}
}

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

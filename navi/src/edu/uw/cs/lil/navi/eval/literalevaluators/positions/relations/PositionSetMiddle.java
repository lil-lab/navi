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
package edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.utils.collections.ListUtils;

public class PositionSetMiddle extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof PositionSet) {
			// Collect the coordinates present in each set
			final Set<Coordinates> ps1Coordinates = new HashSet<Coordinates>(
					ListUtils.map((PositionSet) args[0],
							new ListUtils.Mapper<Position, Coordinates>() {
								
								@Override
								public Coordinates process(Position obj) {
									return obj.getPose().getCoordinates();
								}
							}));
			final Set<Coordinates> ps2Coordinates = new HashSet<Coordinates>(
					ListUtils.map((PositionSet) args[1],
							new ListUtils.Mapper<Position, Coordinates>() {
								
								@Override
								public Coordinates process(Position obj) {
									return obj.getPose().getCoordinates();
								}
							}));
			
			// For each coordinate in args[0], iterate over coodinates from
			// args[1] to verify it's in the middle of coordinates from args[1]
			for (final Coordinates c1 : ps1Coordinates) {
				boolean onYInc = false;
				boolean onYDec = false;
				boolean onXInc = false;
				boolean onXDec = false;
				final Iterator<Coordinates> iterator = ps2Coordinates
						.iterator();
				while (iterator.hasNext()
						&& !((onYInc && onYDec) || (onXInc && onXDec))) {
					final Coordinates c2 = iterator.next();
					if (c2.getX() == c1.getX()) {
						if (c2.getY() > c1.getY()) {
							onYInc = true;
						} else if (c2.getY() < c1.getY()) {
							onYDec = true;
						}
					} else if (c2.getY() == c1.getY()) {
						if (c2.getX() > c1.getX()) {
							onXInc = true;
						} else if (c2.getX() < c1.getX()) {
							onXDec = true;
						}
					}
				}
				// Case coordinate is not in the middle of coordinates from Y
				if (!((onYInc && onYDec) || (onXInc && onXDec))) {
					return false;
				}
			}
			return true;
			
		} else {
			return null;
		}
	}
	
}

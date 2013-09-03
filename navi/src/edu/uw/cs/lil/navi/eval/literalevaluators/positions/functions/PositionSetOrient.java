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
package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;

public class PositionSetOrient extends NaviInvariantLiteralEvaluator {
	
	private static final long	serialVersionUID	= -4369362278797772704L;
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof Direction) {
			final Direction direction = (Direction) args[1];
			final PositionSet ps = (PositionSet) args[0];
			final Set<Position> orientedPositions = new HashSet<Position>();
			for (final Position position : ps) {
				orientedPositions.add(direction.orientPosition(position));
			}
			return new PositionSet(orientedPositions, ps.hasOrientation());
		} else {
			return null;
		}
	}
	
}

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
package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;

public class ActionTo extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof PositionSet) {
			// The destination should be contained within the argument position
			// set, while non of the middle positions should be contained within
			// it
			final PositionSet ps1 = (PositionSet) args[1];
			final Action action = (Action) args[0];
			if (ps1.isIntersective(PositionSetSingleton.of(action.getEnd()))
					&& !ps1.isIntersective(PositionSetSingleton.of(action
							.getStart()))) {
				for (final Position intermediatePosition : action
						.getIntermediatePositions()) {
					if (ps1.isIntersective(PositionSetSingleton
							.of(intermediatePosition))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
}

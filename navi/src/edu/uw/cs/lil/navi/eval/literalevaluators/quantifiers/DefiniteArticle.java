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
package edu.uw.cs.lil.navi.eval.literalevaluators.quantifiers;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Tuple;
import edu.uw.cs.utils.collections.SetUtils;

public class DefiniteArticle implements INaviLiteralEvaluator {
	
	private static double distanceToAgent(PositionSet ps,
			PositionSetSingleton agentPosition) {
		return PositionSet.minDistance(agentPosition, ps);
	}
	
	@Override
	public boolean agentDependent() {
		return false;
	}
	
	@Override
	public Object evaluate(NaviEvaluationServices services, Object[] args) {
		if (args.length == 1 && args[0] instanceof ILambdaResult) {
			return getDefiniteObject((ILambdaResult) args[0], services);
		} else {
			return null;
		}
	}
	
	private Object getDefiniteObject(ILambdaResult set,
			NaviEvaluationServices services) {
		// If the set contains only one object, just return it
		if (set.size() == 1) {
			final Tuple first = set.iterator().next();
			if (first.numKeys() == 1 && first.get(0) instanceof PositionSet) {
				return first.get(0);
			}
		}
		
		// Get all PositionSets from the set
		final Set<PositionSet> positionSets = new HashSet<PositionSet>();
		for (final Tuple tuple : set) {
			if (tuple.numKeys() == 1 && tuple.get(0) instanceof PositionSet) {
				positionSets.add((PositionSet) tuple.get(0));
			} else {
				return null;
			}
		}
		
		final Set<Position> agentForwardColInclusive = Agent.getForwardColumn(
				services.getCurrentAgentPosition(), true);
		final Set<Position> agentForwardColExclusive = Agent.getForwardColumn(
				services.getCurrentAgentPosition(), false);
		
		boolean frontal = false;
		PositionSet definite = null;
		double definiteDistance = Double.MAX_VALUE;
		for (final PositionSet ps : positionSets) {
			final double distance = distanceToAgent(ps,
					services.getCurrentAgentPositionSet());
			final boolean psIsFrontal;
			if (services.isAgentInMovement()
					|| ps.getAllCoordinates().size() != 1) {
				// Case the agent is moving, don't take into account the agent
				// position for computing this flag. Or the object takes more
				// than one coordinate.
				psIsFrontal = SetUtils.isIntersecting(ps,
						agentForwardColExclusive);
			} else {
				psIsFrontal = SetUtils.isIntersecting(ps,
						agentForwardColInclusive);
			}
			
			// Prefer the current if it's frontal and the previously selected
			// one is not frontal. If both are equal as far as being frontal,
			// pick the one that is closer, or if both are the same distance,
			// the one which is smaller (i.e. spread over less coordinates).
			if (definite == null
					|| ((frontal == psIsFrontal) && (distance < definiteDistance || (distance == definiteDistance && ps
							.size() < definite.size())))
					|| (!frontal && psIsFrontal)) {
				definite = ps;
				definiteDistance = distance;
				frontal = psIsFrontal;
			}
		}
		return definite;
	}
}

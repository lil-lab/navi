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

import java.util.Set;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.utils.collections.SetUtils;

public class PositionSetFrontDistance implements INaviLiteralEvaluator {
	
	private static final long	serialVersionUID	= 3683871310029179559L;
	
	@Override
	public boolean agentDependent() {
		return true;
	}
	
	@Override
	public Object evaluate(NaviEvaluationServices services, Object[] args) {
		if (args.length == 1 && args[0] instanceof PositionSet) {
			final Set<Position> forwardPositions = Agent
					.getForwardColumn(services.getCurrentAgentPosition());
			if (SetUtils.retainAll(forwardPositions, (PositionSet) args[0])
					.isEmpty()) {
				return Double.NaN;
			} else {
				return PositionSet.minDistance((PositionSet) args[0],
						services.getCurrentAgentPositionSet());
			}
		} else {
			return null;
		}
	}
}

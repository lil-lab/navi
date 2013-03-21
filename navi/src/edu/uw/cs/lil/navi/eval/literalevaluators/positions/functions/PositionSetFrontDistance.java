package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import java.util.Set;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.utils.collections.SetUtils;

public class PositionSetFrontDistance implements INaviLiteralEvaluator {
	
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

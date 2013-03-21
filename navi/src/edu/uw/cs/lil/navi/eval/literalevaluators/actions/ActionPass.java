package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import java.util.HashSet;
import java.util.List;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;

public class ActionPass extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof PositionSet) {
			final List<Position> passed = ((Action) args[0])
					.getIntermediatePositions();
			if (((Action) args[0]).getAgentAction().equals(AgentAction.FORWARD)
					&& !passed.isEmpty()) {
				return ((PositionSet) args[1]).isIntersective(new PositionSet(
						new HashSet<Position>(passed), true));
			}
			return false;
		} else {
			return null;
		}
	}
}

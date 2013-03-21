package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;

public class ActionPrePosition extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof PositionSet) {
			return ((PositionSet) args[1]).isIntersective(PositionSetSingleton
					.of(((Action) args[0]).getStart()));
		} else {
			return null;
		}
	}
	
}

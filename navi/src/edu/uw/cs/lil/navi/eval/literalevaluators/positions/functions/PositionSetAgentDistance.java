package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.PositionSet;

public class PositionSetAgentDistance implements INaviLiteralEvaluator {
	
	@Override
	public boolean agentDependent() {
		return true;
	}
	
	@Override
	public Object evaluate(NaviEvaluationServices services, Object[] args) {
		if (args.length == 1 && args[0] instanceof PositionSet) {
			return PositionSet.minDistance((PositionSet) args[0],
					services.getCurrentAgentPositionSet());
		} else {
			return null;
		}
	}
}

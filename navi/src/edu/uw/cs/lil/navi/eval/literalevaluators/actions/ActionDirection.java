package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;

public class ActionDirection extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof Direction) {
			return ((Direction) args[1]).validaAgentAction(((Action) args[0])
					.getAgentAction());
		} else {
			return null;
		}
	}
	
}

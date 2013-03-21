package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;

public class ActionLength extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof Double) {
			return ((Action) args[0]).length() == ((Double) args[1]).intValue();
		} else {
			return null;
		}
	}
	
}

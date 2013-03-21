package edu.uw.cs.lil.navi.eval.literalevaluators;

import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;

public abstract class NaviInvariantLiteralEvaluator implements
		INaviLiteralEvaluator {
	
	@Override
	public final boolean agentDependent() {
		return false;
	}
	
	@Override
	public final Object evaluate(NaviEvaluationServices services, Object[] args) {
		return evaluate(args);
	}
	
	protected abstract Object evaluate(Object[] args);
	
}

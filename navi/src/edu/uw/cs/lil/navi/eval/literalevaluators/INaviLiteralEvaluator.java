package edu.uw.cs.lil.navi.eval.literalevaluators;

import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;

public interface INaviLiteralEvaluator {
	
	boolean agentDependent();
	
	Object evaluate(NaviEvaluationServices services, Object[] args);
}

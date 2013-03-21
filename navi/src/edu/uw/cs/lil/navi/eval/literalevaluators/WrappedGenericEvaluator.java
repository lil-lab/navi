package edu.uw.cs.lil.navi.eval.literalevaluators;

import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILiteralEvaluator;

public class WrappedGenericEvaluator extends NaviInvariantLiteralEvaluator {
	private final ILiteralEvaluator	evaluator;
	
	public WrappedGenericEvaluator(ILiteralEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	@Override
	public Object evaluate(Object[] args) {
		return evaluator.evaluate(args);
	}
}

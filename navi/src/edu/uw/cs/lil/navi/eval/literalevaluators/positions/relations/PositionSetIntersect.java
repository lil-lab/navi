package edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.PositionSet;

public class PositionSetIntersect extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof PositionSet) {
			final PositionSet ps0 = (PositionSet) args[0];
			final PositionSet ps1 = (PositionSet) args[1];
			return ps0.isIntersective(ps1);
		} else {
			return null;
		}
	}
	
}

package edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.PositionSet;

public class PositionSetDistance extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 3 && args[0] instanceof PositionSet
				& args[1] instanceof PositionSet && args[2] instanceof Double) {
			return PositionSet.minDistance((PositionSet) args[0],
					(PositionSet) args[1]) == (Double) args[2];
		} else {
			return null;
		}
	}
	
}

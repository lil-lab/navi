package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.utils.filter.IFilter;

public class PositionSetType extends NaviInvariantLiteralEvaluator {
	
	private final IFilter<Position>	positionFilter;
	
	public PositionSetType(IFilter<Position> positionFilter) {
		this.positionFilter = positionFilter;
	}
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 1 && args[0] instanceof PositionSet) {
			return ((PositionSet) args[0]).accept(positionFilter);
		} else {
			return null;
		}
	}
}

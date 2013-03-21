package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;

public class PositionSetOrient extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof Direction) {
			final Direction direction = (Direction) args[1];
			final PositionSet ps = (PositionSet) args[0];
			final Set<Position> orientedPositions = new HashSet<Position>();
			for (final Position position : ps) {
				orientedPositions.add(direction.orientPosition(position));
			}
			return new PositionSet(orientedPositions, ps.hasOrientation());
		} else {
			return null;
		}
	}
	
}

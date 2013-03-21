package edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;

/**
 * <p>
 * end:<ps,<ps,t>>
 * </p>
 * Returns 'true' iff arg0 intersects with the ends of arg1. The ends of arg1
 * are defined by these that moving forward form them will force the agent out
 * of the area captured by arg1, while the position behind is in arg1.
 * 
 * @author Yoav Artzi
 */
public class PositionSetEnd extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof PositionSet) {
			final PositionSet ps1 = (PositionSet) args[1];
			final PositionSet ps0 = (PositionSet) args[0];
			
			for (final Position p0 : ps0) {
				if (ps1.contains(p0) || !ps1.contains(p0.getBack())) {
					return false;
				}
			}
			return true;
		} else {
			return null;
		}
	}
	
}

package edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Tuple;

public class PositionSetOrder extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 3 && args[0] instanceof ILambdaResult
				&& args[1] instanceof ILambdaResult
				&& args[2] instanceof Double && ((Double) args[2]) >= 1.0) {
			// Case set contains only one entry, return null, since no order is
			// available for a single item
			if (((ILambdaResult) args[0]).size() == 1) {
				return null;
			}
			
			// Map all objects to their distance
			final Map<PositionSet, Double> distances = new HashMap<PositionSet, Double>();
			for (final Tuple tuple : ((ILambdaResult) args[1])) {
				if (tuple.numKeys() == 1 && tuple.get(0) instanceof PositionSet
						&& tuple.getValue() instanceof Double) {
					final Double distance = (Double) tuple.getValue();
					if (distance != 0.0 && !distance.isNaN()) {
						distances.put((PositionSet) tuple.get(0),
								(Double) tuple.getValue() == 0.0 ? Double.NaN
										: (Double) tuple.getValue());
					}
				} else {
					return null;
				}
			}
			
			// Create a list of all objects, sort them and return the n-th one
			// (according to arg2)
			final List<PositionSet> objects = new ArrayList<PositionSet>(
					((ILambdaResult) args[0]).size());
			for (final Tuple tuple : (ILambdaResult) args[0]) {
				if (tuple.numKeys() == 1 && tuple.get(0) instanceof PositionSet) {
					if (distances.containsKey(tuple.get(0))) {
						objects.add((PositionSet) tuple.get(0));
					}
				} else {
					return null;
				}
			}
			Collections.sort(objects, new Comparator<PositionSet>() {
				
				@Override
				public int compare(PositionSet o1, PositionSet o2) {
					return distances.get(o1).compareTo(distances.get(o2));
				}
			});
			
			final int order = ((Double) args[2]).intValue();
			if (order <= objects.size()) {
				return objects.get(order - 1);
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}
}

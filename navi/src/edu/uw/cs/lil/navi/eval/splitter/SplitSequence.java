package edu.uw.cs.lil.navi.eval.splitter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

public class SplitSequence {
	
	public static List<LogicalExpression> of(LogicalExpression exp,
			NaviEvaluationConstants consts) {
		final Pair<Variable, List<Literal>> seqInfo = DetectActionSeq.of(exp,
				consts);
		
		if (seqInfo == null) {
			// Case no sequence of actions
			return ListUtils.createSingletonList(exp);
		} else {
			// Case sequence detected, split it
			final List<LogicalExpression> seq = new ArrayList<LogicalExpression>(
					seqInfo.second().size());
			for (final Literal indexLiteral : seqInfo.second()) {
				final HashSet<Literal> rest = new HashSet<Literal>(
						seqInfo.second());
				rest.remove(indexLiteral);
				seq.add(ExtractSingleAction.of(exp, seqInfo.first(),
						indexLiteral, rest));
			}
			return seq;
		}
		
	}
	
}

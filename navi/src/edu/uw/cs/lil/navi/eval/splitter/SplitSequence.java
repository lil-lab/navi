/*******************************************************************************
 * Navi. Copyright (C) 2013 Yoav Artzi
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 ******************************************************************************/
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

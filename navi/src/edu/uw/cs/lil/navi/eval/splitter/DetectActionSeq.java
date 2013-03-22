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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.tiny.mr.language.type.ArrayType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.utils.composites.Pair;

/**
 * Detects the outer-most array variable (if such exists) and collects index
 * literals that refer to it. If none exists returns null.
 * 
 * @author Yoav Artzi
 */
public class DetectActionSeq implements ILogicalExpressionVisitor {
	
	private final NaviEvaluationConstants	consts;
	private final Set<Literal>				indexLiterals	= new HashSet<Literal>();
	private Variable						splitVariable	= null;
	
	private DetectActionSeq(NaviEvaluationConstants consts) {
		this.consts = consts;
	}
	
	public static Pair<Variable, List<Literal>> of(LogicalExpression exp,
			NaviEvaluationConstants consts) {
		final DetectActionSeq visitor = new DetectActionSeq(consts);
		visitor.visit(exp);
		if (visitor.splitVariable == null) {
			return null;
		} else {
			final List<Literal> indexLiteralsList = new ArrayList<Literal>(
					visitor.indexLiterals);
			Collections.sort(indexLiteralsList, new Comparator<Literal>() {
				
				@Override
				public int compare(Literal o1, Literal o2) {
					return new Integer(LogicLanguageServices
							.indexConstantToInt((LogicalConstant) o1
									.getArguments().get(1)))
							.compareTo(LogicLanguageServices
									.indexConstantToInt((LogicalConstant) o2
											.getArguments().get(1)));
				}
			});
			return Pair.of(visitor.splitVariable, indexLiteralsList);
		}
	}
	
	@Override
	public void visit(Lambda lambda) {
		final Type type = lambda.getArgument().getType();
		if (splitVariable == null
				&& type.isArray()
				&& consts.getActionSeqType().equals(
						((ArrayType) type).getBaseType())) {
			splitVariable = lambda.getArgument();
		}
		
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		// If this is an index literal of the splitting variable, add to set of
		// index literals
		if (LogicLanguageServices.isArrayIndexPredicate(literal.getPredicate())
				&& literal.getArguments().size() == 2
				&& literal.getArguments().get(0).equals(splitVariable)) {
			indexLiterals.add(literal);
		}
		// Visit arguments
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		// Nothing to do
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
	}
	
}

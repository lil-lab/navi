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
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.Simplify;
import edu.uw.cs.lil.tiny.mr.language.type.ArrayType;

/**
 * Visitor to extract a single action from a LF that includes a sequence of
 * actions. This visitor makes strong assumptions about how the array variable
 * is used within the LF. Use with caution.
 * 
 * @author Yoav Artzi
 */
public class ExtractSingleAction implements ILogicalExpressionVisitor {
	
	private final Variable		arrayVariable;
	private final Literal		indexToExtract;
	private final Set<Literal>	indicesToIgnore;
	private final Variable		newVariable;
	private LogicalExpression	result	= null;
	
	private ExtractSingleAction(Variable arrayVariable, Literal indexToExtract,
			Set<Literal> indicesToIgnore) {
		this.arrayVariable = arrayVariable;
		this.indexToExtract = indexToExtract;
		this.indicesToIgnore = indicesToIgnore;
		this.newVariable = new Variable(
				((ArrayType) arrayVariable.getType()).getBaseType());
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			Variable arrayVariable, Literal indexToExtract,
			Set<Literal> indicesToIgnore) {
		final ExtractSingleAction visitor = new ExtractSingleAction(
				arrayVariable, indexToExtract, indicesToIgnore);
		visitor.visit(exp);
		return Simplify.of(visitor.result);
	}
	
	@Override
	public void visit(Lambda lambda) {
		final Variable updatedVariable = lambda.getArgument().equals(
				arrayVariable) ? newVariable : lambda.getArgument();
		lambda.getBody().accept(this);
		final LogicalExpression newBody = result;
		if (updatedVariable != lambda.getArgument()
				|| newBody != lambda.getBody()) {
			result = new Lambda(updatedVariable, newBody,
					LogicLanguageServices.getTypeRepository());
		} else {
			result = lambda;
		}
	}
	
	@Override
	public void visit(Literal literal) {
		if (literal.equals(indexToExtract)) {
			result = newVariable;
		} else {
			literal.getPredicate().accept(this);
			final LogicalExpression newPred = result;
			boolean argsChanged = false;
			final List<LogicalExpression> newArgs = new ArrayList<LogicalExpression>(
					literal.getArguments().size());
			for (final LogicalExpression arg : literal.getArguments()) {
				if (indicesToIgnore.contains(arg)) {
					if (literal.getType().equals(
							LogicLanguageServices.getTypeRepository()
									.getTruthValueType())) {
						result = LogicLanguageServices.getTrue();
						return;
					} else {
						throw new IllegalStateException(
								"Unable to handle non-truth-typed literal here: "
										+ literal);
					}
				} else {
					arg.accept(this);
					newArgs.add(result);
					if (result != arg) {
						argsChanged = true;
					}
				}
			}
			if (argsChanged || newPred != literal.getPredicate()) {
				result = new Literal(newPred, argsChanged ? newArgs
						: literal.getArguments(),
						LogicLanguageServices.getTypeComparator(),
						LogicLanguageServices.getTypeRepository());
			} else {
				result = literal;
			}
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		// Nothing to do
		result = logicalConstant;
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
		result = variable;
	}
}

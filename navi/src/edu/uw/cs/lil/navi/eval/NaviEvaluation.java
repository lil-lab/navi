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
package edu.uw.cs.lil.navi.eval;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants.StateFlag;
import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Evaluation;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.AToExists;
import edu.uw.cs.lil.tiny.mr.language.type.ComplexType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;

/**
 * The main evaluation visitor.
 * 
 * @author Yoav Artzi
 */
public class NaviEvaluation extends Evaluation {
	private final LogicalExpression			completeExp;
	private final NaviEvaluationServices	services;
	
	protected NaviEvaluation(LogicalExpression completeExp,
			NaviEvaluationServices services) {
		super(services);
		this.completeExp = completeExp;
		this.services = services;
	}
	
	public static Object of(LogicalExpression exp,
			NaviEvaluationServices services) {
		final LogicalExpression existsExp = AToExists.of(
				StatefulWrapping.of(exp, services),
				services.getExistsQuantifier(), services.getAQuantifier(),
				services.getEqualsPredicates());
		final NaviEvaluation visitor = new NaviEvaluation(existsExp, services);
		visitor.visit(existsExp);
		return visitor.result;
	}
	
	private static boolean isFunctionToT(Type type) {
		return type.isComplex()
				&& ((ComplexType) type).getRange().equals(
						LogicLanguageServices.getTypeRepository()
								.getTruthValueType());
	}
	
	@Override
	public void visit(Lambda lambda) {
		if (lambda == completeExp
				&& services.isActionSeq(lambda.getArgument().getType())
				&& !(lambda.getBody() instanceof Lambda)) {
			// Case the complete logical expression and the argument
			// represents an action, short-circuit the evaluation of the body,
			// since the denotation is sorted by action priority anyway. This
			// execution is not cached for two reasons: (a) it's not a complete
			// execution and (b) it's the outer-most expression, which is
			// useless to cache anyway.
			super.visit(lambda, true);
		} else {
			super.visit(lambda);
		}
	}
	
	@Override
	public void visit(Literal literal) {
		// Case a partial literal, let the general executor handle it
		if (isPartialLiteral(literal)) {
			super.visit(literal);
			return;
		}
		
		if (services.isExistsQuantifier(literal)) {
			if (services.isCached(literal)) {
				result = services.getFromCache(literal);
			} else {
				// Short-circuit exists quantifiers. This is more efficient.
				// This code assumes exists quantifiers are not stateful.
				if (literal.getArguments().size() == 1
						&& isFunctionToT(literal.getArguments().get(0)
								.getType())) {
					final Lambda innerLambda;
					final LogicalExpression arg0 = literal.getArguments()
							.get(0);
					if (arg0 instanceof Lambda) {
						innerLambda = (Lambda) literal.getArguments().get(0);
					} else {
						innerLambda = null;
					}
					
					if (innerLambda != null) {
						
						final Object[] evalArgs = new Object[1];
						// The output of the inner lambda is not cached, since
						// it's not a complete execution. Caching is done on the
						// exists level.
						visit(innerLambda, true);
						// If failed to evaluate, propagate failure to
						// literal
						if (result != null) {
							evalArgs[0] = result;
							// Execute using the normal executor
							result = services.evaluateLiteral(
									literal.getPredicate(), evalArgs);
						}
						services.cacheResult(literal, result);
						return;
					}
				}
				// Weird structure, try to handle it, but probably it will
				// simply explode later
				super.visit(literal);
			}
		} else if (services.isDefiniteQuantifier(literal)) {
			// The content of definite determiners is resolved according to the
			// position of the agent during speaking-time
			services.pushAgentPosition(services.getInitAgentPosition());
			
			// Process the literal as usual
			super.visit(literal);
			
			// Pop the agent initial position, to return to previous state
			services.popAgentState();
			
			// If the returned set is empty, try to re-execute without modifying
			// the state
			if (result == null) {
				super.visit(literal);
			}
			
		} else if (services.isStatefulWrapper(literal)) {
			// Case stateful wrapper -- it requires a
			// potential modification to the agent state. Such evaluation
			// doesn't use the original visit(literal), so has to handle
			// caching.
			
			// This literal has only 2 arguments, one that sets the state and
			// another that actually has the content
			if (literal.getArguments().size() != 2) {
				throw new IllegalStateException(
						"State wrapper with invalid number of arguments: "
								+ literal);
			}
			
			// Get the relevant action from the first argument, to set the
			// state
			literal.getArguments().get(0).accept(this);
			final Object arg0Eval = result;
			
			// Case evaluation of arg0 failed, propagate it
			if (result == null) {
				services.cacheResult(literal, result);
				return;
			}
			
			// Set the state
			if (arg0Eval instanceof Action) {
				final StateFlag stateFlag = services.getStateFlag(literal);
				if (stateFlag.equals(StateFlag.POST)) {
					services.pushAgentPosition(((Action) arg0Eval).getEnd());
				} else if (stateFlag.equals(StateFlag.PRE)) {
					services.pushAgentPosition(((Action) arg0Eval).getStart());
				} else {
					throw new IllegalStateException("unhandled state flag");
				}
			} else {
				throw new IllegalStateException("first arg must be an action");
			}
			
			if (services.isCached(literal)) {
				result = services.getFromCache(literal);
			} else {
				// Evaluate the second argument
				literal.getArguments().get(1).accept(this);
				
				// Basically pass by the evaluation of the second argument
				services.cacheResult(literal, result);
			}
			
			// Reset the state
			services.popAgentState();
		} else {
			super.visit(literal);
		}
	}
}

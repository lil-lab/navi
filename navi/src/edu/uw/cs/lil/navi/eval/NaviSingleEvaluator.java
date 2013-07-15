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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.splitter.SplitSequence;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.IsEvaluable;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.LambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Tuple;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviSingleEvaluator {
	private static final ILogger				LOG	= LoggerFactory
															.create(NaviSingleEvaluator.class);
	private final NaviEvaluationServicesFactory	servicesFactory;
	
	public NaviSingleEvaluator(NaviEvaluationServicesFactory servicesFactory) {
		this.servicesFactory = servicesFactory;
	}
	
	public NaviEvaluationServicesFactory getServicesFactory() {
		return servicesFactory;
	}
	
	public boolean isEvaluable(LogicalExpression exp, Task task) {
		// The verification code can't handle arrays, so we have redundant split
		// here. This can be easily fixed, but not high on the priority list.
		for (final LogicalExpression part : SplitSequence.of(exp,
				servicesFactory.getNaviEvaluationConsts())) {
			if (!IsEvaluable.of(part, servicesFactory.create(task))) {
				return false;
			}
		}
		return true;
	}
	
	public Object of(LogicalExpression exp, Task task) {
		return of(exp, task, false);
	}
	
	public Object of(LogicalExpression exp, Task task, boolean noImplicit) {
		if (!isEvaluable(exp, task)) {
			return null;
		}
		
		// Split the sequence, if it's a single action, this procedure will
		// return a list containing a single object, which will be the
		// original expression
		final List<LogicalExpression> split = SplitSequence.of(exp,
				servicesFactory.getNaviEvaluationConsts());
		
		if (split.size() == 1) {
			return executeSingle(exp, task, noImplicit);
		} else {
			// Iterate over split expressions, execute each one in turn
			Agent currentAgent = task.getAgent();
			final List<Step> steps = new LinkedList<Step>();
			for (final LogicalExpression singleActionExp : split) {
				
				// Execute the single action expression
				final Object singleResult = executeSingle(singleActionExp,
						task.updateAgent(currentAgent), noImplicit);
				
				if (singleResult instanceof Trace) {
					currentAgent = new Agent(
							((Trace) singleResult).getEndPosition());
					steps.addAll(((Trace) singleResult).getSteps());
				} else if (singleResult == null
						|| ((singleResult instanceof LambdaResult) && ((LambdaResult) singleResult)
								.isEmpty())) {
					return null;
				} else {
					// Results is not a trace, not sure what to do here, log it
					// and return null
					LOG.warn(
							"Expected a trace or null (for: %s), but got something else (%s), returning null: %s",
							singleActionExp, singleResult.getClass()
									.getSimpleName(), singleResult);
					return null;
				}
			}
			return new Trace(steps, currentAgent.getPosition());
		}
	}
	
	private Object doExecuteSingleStatement(LogicalExpression exp,
			NaviEvaluationServices evalServices) {
		final Object evalResult = NaviEvaluation.of(exp, evalServices);
		
		// If the result is a set including a single action, create a trace
		// out of it
		if (evalResult instanceof ILambdaResult
				&& ((ILambdaResult) evalResult).size() == 1) {
			final Tuple first = ((ILambdaResult) evalResult).iterator().next();
			if (first.numKeys() == 1 && (first.get(0) instanceof Action)) {
				final Action action = (Action) first.get(0);
				return new Trace(action.getSteps(), action.getEnd());
			}
		}
		
		return evalResult;
	}
	
	private Object executeSingle(LogicalExpression exp, Task task,
			boolean noImplicit) {
		if (LogicLanguageServices.getTypeRepository().getTruthValueType()
				.equals(exp.getType())) {
			return executeSingleStatement(exp, task, noImplicit);
		} else {
			return executeSingleAction(exp, task, noImplicit);
		}
	}
	
	private Object executeSingleAction(LogicalExpression exp, Task task,
			boolean noImplicit) {
		final Object evalResult = NaviEvaluation.of(exp,
				servicesFactory.create(task, noImplicit));
		
		// If the result is a set including a single action, create a trace
		// out of it
		if (evalResult instanceof ILambdaResult
				&& ((ILambdaResult) evalResult).size() == 1) {
			final Tuple first = ((ILambdaResult) evalResult).iterator().next();
			if (first.numKeys() == 1 && (first.get(0) instanceof Action)) {
				final Action action = (Action) first.get(0);
				return new Trace(action.getSteps(), action.getEnd());
			}
		}
		
		return evalResult;
	}
	
	private Object executeSingleStatement(LogicalExpression exp, Task task,
			boolean noImplicit) {
		final NaviEvaluationServices initialServices = servicesFactory.create(
				task, noImplicit);
		
		final Object noImplicitEval = doExecuteSingleStatement(exp,
				initialServices);
		if (!Boolean.FALSE.equals(noImplicitEval) && noImplicitEval != null) {
			// Case evaluated without any need for implicit actions
			return noImplicitEval;
		} else if (servicesFactory.getNaviEvaluationConsts()
				.getMaxImplicitActionsPerTurn() > 0 && !noImplicit) {
			// Case we allow implicit actions
			
			// Get all actions that include implicit actions as prefix
			final List<Action> allInitialActions = initialServices
					.allPossibleActions();
			
			// Remember which positions were already processed, to avoid
			// repeating computation
			final Set<Position> processedPositions = new HashSet<Position>();
			// Initial position was processed without implicit actions
			processedPositions.add(task.getAgent().getPosition());
			
			// Iterate over all actions, incl. these with implicit actions, and
			// only treat the implicit prefix. Take the agent position as the
			// starting point of the explicit part of the action, which is
			// disregarded.
			for (final Action initialAction : allInitialActions) {
				final Position currentStart = initialAction.getStart();
				if (!processedPositions.contains(currentStart)) {
					processedPositions.add(currentStart);
					final Task postImplicitTask = task.updateAgent(new Agent(
							currentStart));
					final Object result = doExecuteSingleStatement(exp,
							servicesFactory
									.create(postImplicitTask, noImplicit));
					
					// Return the first that evaluates to 'true'. The actions
					// are ordered by priority.
					if (!Boolean.FALSE.equals(result) && result != null) {
						// Steps from implicit actions
						final List<Step> steps = new LinkedList<Step>();
						for (final Action implicitAction : initialAction
								.getImplicitActions()) {
							for (final Step step : implicitAction.getSteps()) {
								steps.add(step.cloneAsImplicit());
							}
						}
						return new Trace(steps, currentStart);
					}
				}
			}
			return false;
		} else {
			return noImplicitEval;
		}
	}
}

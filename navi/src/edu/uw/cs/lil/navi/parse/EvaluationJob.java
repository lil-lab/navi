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
package edu.uw.cs.lil.navi.parse;

import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.graph.simple.DeterministicExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;
import edu.uw.cs.utils.log.thread.LoggingCallable;

/**
 * Callable for evaluating a logical expression.
 * 
 * @author Yoav Artzi
 */
public class EvaluationJob
		extends
		LoggingCallable<Pair<LogicalExpression, DeterministicExecResultWrapper<Trace, Trace>>> {
	public static final ILogger									LOG				= LoggerFactory
																						.create(EvaluationJob.class);
	
	private static final List<Step>								EMPTY_STEP_LIST	= Collections
																						.emptyList();
	private final NaviSingleEvaluator							evaluationWrapper;
	private final Instruction									instruction;
	
	private final IJointDataItemModel<LogicalExpression, Trace>	model;
	
	private final LogicalExpression								semantics;
	
	public EvaluationJob(Instruction instruction, LogicalExpression semantics,
			IJointDataItemModel<LogicalExpression, Trace> model,
			NaviSingleEvaluator evaluationWrapper) {
		this.instruction = instruction;
		this.semantics = semantics;
		this.model = model;
		this.evaluationWrapper = evaluationWrapper;
	}
	
	public LogicalExpression getSemantics() {
		return semantics;
	}
	
	@Override
	public Pair<LogicalExpression, DeterministicExecResultWrapper<Trace, Trace>> loggedCall() {
		LOG.debug("Evaluating: %s", semantics);
		
		final Object result = evaluationWrapper.of(semantics,
				instruction.getState());
		
		if (result instanceof Trace) {
			return Pair.of(semantics,
					new DeterministicExecResultWrapper<Trace, Trace>(
							(Trace) result, model, (Trace) result));
		} else if (Boolean.TRUE.equals(result)) {
			final Trace emptyTrace = new Trace(EMPTY_STEP_LIST, instruction
					.getState().getAgent().getPosition());
			return Pair.of(semantics,
					new DeterministicExecResultWrapper<Trace, Trace>(
							emptyTrace, model, emptyTrace));
		} else {
			return Pair.of(semantics,
					new DeterministicExecResultWrapper<Trace, Trace>(null,
							model, null));
		}
	}
	
}

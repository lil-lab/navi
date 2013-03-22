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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.AbstractParser;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.Pruner;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.JointOutput;
import edu.uw.cs.lil.tiny.parser.joint.JointParse;
import edu.uw.cs.lil.tiny.parser.joint.SingleExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.utils.concurrency.ITinyExecutor;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;
import edu.uw.cs.utils.log.thread.LoggingCallable;

public class NaviParser extends AbstractParser<Sentence, LogicalExpression>
		implements
		IJointParser<Sentence, Task, LogicalExpression, Trace, Trace> {
	
	private static final List<Step>						EMPTY_STEP_LIST	= Collections
																				.emptyList();
	
	private static final ILogger						LOG				= LoggerFactory
																				.create(NaviParser.class);
	
	private final IParser<Sentence, LogicalExpression>	baseParser;
	
	private final int									evalTimeoutMilliseconds;
	private final NaviSingleEvaluator					evaluationWrapper;
	
	private final ITinyExecutor							executor;
	
	public NaviParser(IParser<Sentence, LogicalExpression> baseParser,
			NaviSingleEvaluator naviSingleEvaluator, ITinyExecutor executor,
			int evalTimeoutMilliseconds) {
		this.baseParser = baseParser;
		this.evaluationWrapper = naviSingleEvaluator;
		this.executor = executor;
		this.evalTimeoutMilliseconds = evalTimeoutMilliseconds;
	}
	
	@Override
	public IJointOutput<LogicalExpression, Trace> parse(
			IDataItem<Pair<Sentence, Task>> dataItem,
			IJointDataItemModel<LogicalExpression, Trace> model) {
		return parse(dataItem, model, false);
	}
	
	@Override
	public IJointOutput<LogicalExpression, Trace> parse(
			IDataItem<Pair<Sentence, Task>> dataItem,
			IJointDataItemModel<LogicalExpression, Trace> model,
			boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}
	
	@Override
	public IJointOutput<LogicalExpression, Trace> parse(
			IDataItem<Pair<Sentence, Task>> dataItem,
			IJointDataItemModel<LogicalExpression, Trace> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, null);
	}
	
	@Override
	public IJointOutput<LogicalExpression, Trace> parse(
			IDataItem<Pair<Sentence, Task>> dataItem,
			IJointDataItemModel<LogicalExpression, Trace> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon,
			Integer beamSize) {
		LOG.debug("Parsing: %s", dataItem);
		final IParserOutput<LogicalExpression> baseParserOutput = baseParser
				.parse(dataItem.getSample().first(), model, allowWordSkipping,
						tempLexicon, beamSize);
		final long evalStartTime = System.currentTimeMillis();
		final List<IParseResult<LogicalExpression>> allBaseParses = baseParserOutput
				.getAllParses();
		final List<IJointParse<LogicalExpression, Trace>> parses = new ArrayList<IJointParse<LogicalExpression, Trace>>(
				allBaseParses.size());
		LOG.debug("Base parsing complete in %fsec, %d parses generated",
				baseParserOutput.getParsingTime() / 1000.0,
				allBaseParses.size());
		final List<EvaluationJob> evaluationJobs = new ArrayList<EvaluationJob>(
				allBaseParses.size());
		for (final IParseResult<LogicalExpression> baseParse : allBaseParses) {
			evaluationJobs.add(new EvaluationJob(dataItem, baseParse, model));
		}
		try {
			int numTimedOut = 0;
			final Iterator<EvaluationJob> jobIterator = evaluationJobs
					.iterator();
			for (final Future<IJointParse<LogicalExpression, Trace>> future : executor
					.invokeAll(evaluationJobs)) {
				final EvaluationJob job = jobIterator.next();
				if (job.timedOut) {
					++numTimedOut;
				} else {
					parses.add(future.get());
				}
			}
			if (numTimedOut != 0) {
				LOG.warn("%d/%d tasks cancelled due to timeout", numTimedOut,
						evaluationJobs.size());
			}
		} catch (final InterruptedException e) {
			LOG.error("Interrupt exception during base parses evaluation");
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			LOG.error("Execution exception during base parses evaluation");
			throw new RuntimeException(e);
		}
		
		if (parses.isEmpty() && allBaseParses.isEmpty()) {
			LOG.debug("No parses (base and joint)");
		} else if (parses.isEmpty()) {
			LOG.debug("No joint parses (%d base parses)", allBaseParses.size());
		} else {
			LOG.debug("%d joint parses, from %d base parses", parses.size(),
					allBaseParses.size());
		}
		
		final long totalProcessingTime = baseParserOutput.getParsingTime()
				+ (System.currentTimeMillis() - evalStartTime);
		
		LOG.info(
				"Joint parsing complete, total: %fsec, %.2f for base parsing (base parsing %fsec, execution %fsec, %d tokens)",
				totalProcessingTime / 1000.0,
				((double) baseParserOutput.getParsingTime() / (double) totalProcessingTime),
				baseParserOutput.getParsingTime() / 1000.0,
				(totalProcessingTime - baseParserOutput.getParsingTime()) / 1000.0,
				dataItem.getSample().first().getTokens().size());
		
		return new JointOutput<LogicalExpression, Trace>(baseParserOutput,
				parses, totalProcessingTime);
	}
	
	@Override
	public IParserOutput<LogicalExpression> parse(IDataItem<Sentence> dataItem,
			Pruner<Sentence, LogicalExpression> pruner,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return baseParser.parse(dataItem, pruner, model, allowWordSkipping,
				tempLexicon, beamSize);
	}
	
	private class EvaluationJob extends
			LoggingCallable<IJointParse<LogicalExpression, Trace>> {
		
		private final IParseResult<LogicalExpression>				baseParse;
		private final IDataItem<Pair<Sentence, Task>>				dataItem;
		private final IJointDataItemModel<LogicalExpression, Trace>	model;
		private boolean												timedOut	= false;
		
		public EvaluationJob(IDataItem<Pair<Sentence, Task>> dataItem,
				IParseResult<LogicalExpression> baseParse,
				IJointDataItemModel<LogicalExpression, Trace> model) {
			this.dataItem = dataItem;
			this.baseParse = baseParse;
			this.model = model;
		}
		
		@Override
		public IJointParse<LogicalExpression, Trace> loggedCall() {
			LOG.debug("Evaluating: [%f] %s", baseParse.getScore(),
					baseParse.getY());
			
			final Object result;
			if (evalTimeoutMilliseconds > 0) {
				// Case run with timeout
				final TimedEvaluationRunnable runnable = new TimedEvaluationRunnable(
						baseParse, dataItem);
				synchronized (runnable) {
					final Future<Object> future = executor.submit(runnable);
					try {
						executor.wait(runnable, evalTimeoutMilliseconds);
					} catch (final InterruptedException e) {
						throw new IllegalStateException(e);
					}
					result = runnable.result;
					timedOut = !runnable.completed;
					if (timedOut) {
						if (future.cancel(true)) {
							LOG.warn("Cancelled evaluation job successfuly");
						} else {
							LOG.warn("Failed to cancel evaluation job");
						}
					}
				}
			} else {
				// Case run without timeout, no need to spawn another thread
				result = evaluationWrapper.of(baseParse.getY(), dataItem
						.getSample().second());
			}
			
			if (timedOut) {
				LOG.warn("Evaluation timed out for: [%s] %s",
						dataItem.getSample(), baseParse);
			}
			
			if (result instanceof Trace) {
				return new JointParse<LogicalExpression, Trace>(baseParse,
						new SingleExecResultWrapper<Trace, Trace>(
								(Trace) result, model, (Trace) result));
			} else if (Boolean.TRUE.equals(result)) {
				final Trace emptyTrace = new Trace(EMPTY_STEP_LIST, dataItem
						.getSample().second().getAgent().getPosition());
				return new JointParse<LogicalExpression, Trace>(baseParse,
						new SingleExecResultWrapper<Trace, Trace>(emptyTrace,
								model, emptyTrace));
			} else {
				return new JointParse<LogicalExpression, Trace>(baseParse,
						new SingleExecResultWrapper<Trace, Trace>(null, model,
								null));
			}
		}
	}
	
	private class TimedEvaluationRunnable extends LoggingCallable<Object> {
		
		private final IParseResult<LogicalExpression>	baseParse;
		private boolean									completed	= false;
		
		private final IDataItem<Pair<Sentence, Task>>	dataItem;
		private Object									result		= null;
		
		public TimedEvaluationRunnable(
				IParseResult<LogicalExpression> baseParse,
				IDataItem<Pair<Sentence, Task>> dataItem) {
			this.baseParse = baseParse;
			this.dataItem = dataItem;
		}
		
		@Override
		public Object loggedCall() {
			final Object localResult = evaluationWrapper.of(baseParse.getY(),
					dataItem.getSample().second());
			synchronized (this) {
				result = localResult;
				completed = true;
				this.notifyAll();
				return result;
			}
		}
	}
}

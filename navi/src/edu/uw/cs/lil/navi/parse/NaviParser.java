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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.AbstractParser;
import edu.uw.cs.lil.tiny.parser.IParse;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.JointOutput;
import edu.uw.cs.lil.tiny.parser.joint.JointParse;
import edu.uw.cs.lil.tiny.parser.joint.graph.simple.DeterministicExecResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.utils.concurrency.ITinyExecutor;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.filter.IFilter;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Joint inference procedure to map pairs of <instruction,state> to pairs of
 * <trace, logical form>.
 * 
 * @author Yoav Artzi
 */
public class NaviParser extends AbstractParser<Sentence, LogicalExpression>
		implements
		IJointParser<Sentence, Task, LogicalExpression, Trace, Trace> {
	
	private static final ILogger						LOG	= LoggerFactory
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
		final List<? extends IParse<LogicalExpression>> allBaseParses = baseParserOutput
				.getAllParses();
		final List<IJointParse<LogicalExpression, Trace>> parses = new ArrayList<IJointParse<LogicalExpression, Trace>>(
				allBaseParses.size());
		LOG.debug("Base parsing complete in %fsec, %d parses generated",
				baseParserOutput.getParsingTime() / 1000.0,
				allBaseParses.size());
		final List<EvaluationJob> evaluationJobs = new ArrayList<EvaluationJob>(
				allBaseParses.size());
		
		// Create evaluation jobs and mapping from semantics to base parses
		final Map<LogicalExpression, List<IParse<LogicalExpression>>> semanticsToParses = new HashMap<LogicalExpression, List<IParse<LogicalExpression>>>();
		for (final IParse<LogicalExpression> baseParse : allBaseParses) {
			if (!semanticsToParses.containsKey(baseParse.getSemantics())) {
				semanticsToParses.put(baseParse.getSemantics(),
						new LinkedList<IParse<LogicalExpression>>());
				evaluationJobs.add(new EvaluationJob(dataItem, baseParse
						.getSemantics(), model, evaluationWrapper));
			}
			semanticsToParses.get(baseParse.getSemantics()).add(baseParse);
		}
		
		try {
			int numTimedOut = 0;
			final Iterator<EvaluationJob> jobIterator = evaluationJobs
					.iterator();
			for (final Future<Pair<LogicalExpression, DeterministicExecResultWrapper<Trace, Trace>>> future : evalTimeoutMilliseconds > 0 ? executor
					.invokeAllWithUniqueTimeout(evaluationJobs,
							evalTimeoutMilliseconds) : executor
					.invokeAll(evaluationJobs)) {
				final EvaluationJob job = jobIterator.next();
				try {
					final Pair<LogicalExpression, DeterministicExecResultWrapper<Trace, Trace>> evalResult = future
							.get();
					// Create a joint parse for every base parse that is rooted
					// at this logical form
					for (final IParse<LogicalExpression> baseParse : semanticsToParses
							.get(evalResult.first())) {
						parses.add(new JointParse<LogicalExpression, Trace>(
								baseParse, evalResult.second()));
					}
				} catch (final ExecutionException e) {
					++numTimedOut;
					LOG.info(
							"Evaluation interuppted (most likely due to timeout): %s",
							job.getSemantics());
				}
			}
			if (numTimedOut != 0) {
				LOG.warn("%d/%d tasks cancelled due to timeout", numTimedOut,
						evaluationJobs.size());
			}
		} catch (final InterruptedException e) {
			LOG.error("Unexpected interrupt exception during base parses evaluation");
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
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return baseParser.parse(dataItem, pruningFilter, model,
				allowWordSkipping, tempLexicon, beamSize);
	}
	
}

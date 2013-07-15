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
package edu.uw.cs.lil.navi.exec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.exec.IExecOutput;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.utils.collections.BoundedPriorityQueue;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.collections.OrderInvariantBoundedPriorityQueue;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviSeqExecutor implements
		IExec<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>> {
	private static final ILogger												LOG	= LoggerFactory
																							.create(NaviSeqExecutor.class);
	private final int															beam;
	private final boolean														failureRecovery;
	private final JointModel<Sentence, Task, LogicalExpression, Trace>			model;
	private final IJointParser<Sentence, Task, LogicalExpression, Trace, Trace>	parser;
	private final boolean														pruneFails;
	
	public NaviSeqExecutor(
			IJointParser<Sentence, Task, LogicalExpression, Trace, Trace> parser,
			JointModel<Sentence, Task, LogicalExpression, Trace> model,
			int beam, boolean failureRecovery, boolean pruneFails) {
		this.parser = parser;
		this.model = model;
		this.beam = beam;
		this.failureRecovery = failureRecovery;
		this.pruneFails = pruneFails;
	}
	
	private static IDataItem<Pair<Sentence, Task>> createSingleDataItem(
			final Sentence sentence, final Task task) {
		return new IDataItem<Pair<Sentence, Task>>() {
			private final Pair<Sentence, Task>	sample	= Pair.of(sentence,
																task);
			
			@Override
			public Pair<Sentence, Task> getSample() {
				return sample;
			}
			
			@Override
			public String toString() {
				return sample.toString();
			}
		};
	}
	
	private static double score(
			List<IJointParse<LogicalExpression, Trace>> parses) {
		double sum = 0.0;
		for (final IJointParse<LogicalExpression, Trace> parse : parses) {
			sum += (parse == null ? 0.0 : parse.getScore());
		}
		return sum;
	}
	
	@Override
	public IExecOutput<List<Pair<LogicalExpression, Trace>>> execute(
			IDataItem<Pair<List<Sentence>, Task>> dataItem) {
		return execute(dataItem, false);
	}
	
	@Override
	public IExecOutput<List<Pair<LogicalExpression, Trace>>> execute(
			IDataItem<Pair<List<Sentence>, Task>> dataItem, boolean sloppy) {
		final long startTime = System.currentTimeMillis();
		
		final OrderInvariantBoundedPriorityQueue<Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task>> queue = new OrderInvariantBoundedPriorityQueue<Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task>>(
				beam,
				new Comparator<Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task>>() {
					
					@Override
					public int compare(
							Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> o1,
							Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> o2) {
						return Double.compare(score(o1), score(o2));
					}
					
					private double score(
							Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> e) {
						double max = -Double.MAX_VALUE;
						for (final List<IJointParse<LogicalExpression, Trace>> parses : e
								.first()) {
							final double score = NaviSeqExecutor.score(parses);
							if (score > max) {
								max = score;
							}
						}
						return max;
					}
				});
		
		// Init with a single cluster, containing the original agent position
		// and a single empty list
		final List<IJointParse<LogicalExpression, Trace>> initParseList = new ArrayList<IJointParse<LogicalExpression, Trace>>();
		final PriorityQueue<List<IJointParse<LogicalExpression, Trace>>> initQueue = createParseQueue();
		initQueue.add(initParseList);
		queue.add(Pair.of(initQueue, dataItem.getSample().second()));
		
		for (final Sentence sentence : dataItem.getSample().first()) {
			final Map<Task, PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>> postExecTasks = new HashMap<Task, PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>>();
			for (final Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> cluster : queue) {
				final IDataItem<Pair<Sentence, Task>> singleDataItem = createSingleDataItem(
						sentence, cluster.second());
				final IJointDataItemModel<LogicalExpression, Trace> dataItemModel = model
						.createJointDataItemModel(singleDataItem);
				final IJointOutput<LogicalExpression, Trace> parserOutput = parser
						.parse(singleDataItem, dataItemModel, sloppy);
				for (final IJointParse<LogicalExpression, Trace> parse : parserOutput
						.getAllParses(!pruneFails)) {
					// Create task for the next instruction. If this one failed
					// to execute, re-use the previous task.
					final Task newTask = parse.getResult().second() == null ? cluster
							.second() : cluster.second().updateAgent(
							new Agent(parse.getResult().second()
									.getEndPosition()));
					for (final List<IJointParse<LogicalExpression, Trace>> prev : cluster
							.first()) {
						final List<IJointParse<LogicalExpression, Trace>> newList = new ArrayList<IJointParse<LogicalExpression, Trace>>(
								prev.size() + 1);
						newList.addAll(prev);
						newList.add(parse);
						if (!postExecTasks.containsKey(newTask)) {
							postExecTasks.put(newTask, createParseQueue());
						}
						postExecTasks.get(newTask).offer(newList);
					}
				}
			}
			if (postExecTasks.isEmpty()) {
				if (failureRecovery && sloppy) {
					LOG.info("Recovering from failure to parse: %s", sentence);
					for (final Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> cluster : queue) {
						for (final List<IJointParse<LogicalExpression, Trace>> execList : cluster
								.first()) {
							execList.add(null);
						}
					}
				} else {
					LOG.info(
							"Set execution aborted, failed to parse: \"%s\", at %s",
							sentence,
							ListUtils.join(
									ListUtils
											.map(queue,
													new ListUtils.Mapper<Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task>, Position>() {
														
														@Override
														public Position process(
																Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> obj) {
															return obj
																	.second()
																	.getAgent()
																	.getPosition();
														}
													}), ","));
					queue.clear();
					break;
				}
			} else {
				queue.clear();
				for (final Entry<Task, PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>> clusterEntry : postExecTasks
						.entrySet()) {
					queue.offer(Pair.of(clusterEntry.getValue(),
							clusterEntry.getKey()));
				}
				if (queue.isEmpty()) {
					LOG.info("Execution queue is empty due to tie breaking -- execution failed");
					break;
				}
			}
		}
		
		// Concatenate all clusters to a single list
		final List<List<IJointParse<LogicalExpression, Trace>>> parseLists = new LinkedList<List<IJointParse<LogicalExpression, Trace>>>();
		for (final Pair<PriorityQueue<List<IJointParse<LogicalExpression, Trace>>>, Task> cluster : queue) {
			for (final List<IJointParse<LogicalExpression, Trace>> parseList : cluster
					.first()) {
				parseLists.add(parseList);
			}
		}
		
		return new NaviSetExecutionOutput(parseLists, model,
				System.currentTimeMillis() - startTime);
	}
	
	private BoundedPriorityQueue<List<IJointParse<LogicalExpression, Trace>>> createParseQueue() {
		return new BoundedPriorityQueue<List<IJointParse<LogicalExpression, Trace>>>(
				beam,
				new Comparator<List<IJointParse<LogicalExpression, Trace>>>() {
					@Override
					public int compare(
							List<IJointParse<LogicalExpression, Trace>> o1,
							List<IJointParse<LogicalExpression, Trace>> o2) {
						return Double.compare(NaviSeqExecutor.score(o1),
								NaviSeqExecutor.score(o2));
					}
				});
	}
}

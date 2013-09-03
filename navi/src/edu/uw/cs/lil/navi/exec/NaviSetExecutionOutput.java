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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExecOutput;
import edu.uw.cs.lil.tiny.exec.IExecution;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointModelImmutable;
import edu.uw.cs.utils.collections.CollectionUtils;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.filter.IFilter;

public class NaviSetExecutionOutput implements
		IExecOutput<List<Pair<LogicalExpression, Trace>>> {
	private final List<IExecution<List<Pair<LogicalExpression, Trace>>>>	allExecutions;
	private final List<IExecution<List<Pair<LogicalExpression, Trace>>>>	bestExecutions;
	private final long														execTime;
	
	public NaviSetExecutionOutput(
			List<List<IJointParse<LogicalExpression, Trace>>> parseLists,
			final IJointModelImmutable<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace> model,
			long execTime) {
		this.execTime = execTime;
		this.allExecutions = ListUtils
				.map(parseLists,
						new ListUtils.Mapper<List<IJointParse<LogicalExpression, Trace>>, IExecution<List<Pair<LogicalExpression, Trace>>>>() {
							
							@Override
							public IExecution<List<Pair<LogicalExpression, Trace>>> process(
									List<IJointParse<LogicalExpression, Trace>> obj) {
								return new NaviSetExecution(obj, model);
							}
						});
		CollectionUtils
				.filterInPlace(
						allExecutions,
						new IFilter<IExecution<List<Pair<LogicalExpression, Trace>>>>() {
							
							@Override
							public boolean isValid(
									IExecution<List<Pair<LogicalExpression, Trace>>> e) {
								for (final Pair<LogicalExpression, Trace> pair : e
										.getResult()) {
									if (pair != null) {
										return true;
									}
								}
								return false;
							}
						});
		double bestScore = -Double.MAX_VALUE;
		final List<IExecution<List<Pair<LogicalExpression, Trace>>>> bestList = new LinkedList<IExecution<List<Pair<LogicalExpression, Trace>>>>();
		for (final IExecution<List<Pair<LogicalExpression, Trace>>> execution : allExecutions) {
			if (execution.score() == bestScore) {
				bestList.add(execution);
			} else if (execution.score() > bestScore) {
				bestList.clear();
				bestList.add(execution);
				bestScore = execution.score();
			}
		}
		this.bestExecutions = Collections.unmodifiableList(bestList);
	}
	
	@Override
	public List<IExecution<List<Pair<LogicalExpression, Trace>>>> getAllExecutions() {
		return allExecutions;
	}
	
	@Override
	public List<IExecution<List<Pair<LogicalExpression, Trace>>>> getBestExecutions() {
		return bestExecutions;
	}
	
	@Override
	public long getExecTime() {
		return execTime;
	}
	
	@Override
	public List<IExecution<List<Pair<LogicalExpression, Trace>>>> getExecutions(
			List<Pair<LogicalExpression, Trace>> label) {
		final List<IExecution<List<Pair<LogicalExpression, Trace>>>> execs = new LinkedList<IExecution<List<Pair<LogicalExpression, Trace>>>>();
		for (final IExecution<List<Pair<LogicalExpression, Trace>>> execution : allExecutions) {
			if (execution.getResult().equals(label)) {
				execs.add(execution);
			}
		}
		return execs;
	}
	
}

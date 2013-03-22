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
import java.util.Iterator;
import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExecution;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointModelImmutable;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

public class NaviSetExecution implements
		IExecution<List<Pair<LogicalExpression, Trace>>> {
	
	private final IJointModelImmutable<Sentence, Task, LogicalExpression, Trace>	model;
	private final List<IJointParse<LogicalExpression, Trace>>						parses;
	private final List<Pair<LogicalExpression, Trace>>								result;
	private double																	score;
	
	public NaviSetExecution(List<IJointParse<LogicalExpression, Trace>> parses,
			IJointModelImmutable<Sentence, Task, LogicalExpression, Trace> model) {
		this.model = model;
		this.parses = parses;
		this.result = Collections
				.unmodifiableList(ListUtils
						.map(parses,
								new ListUtils.Mapper<IJointParse<LogicalExpression, Trace>, Pair<LogicalExpression, Trace>>() {
									
									@Override
									public Pair<LogicalExpression, Trace> process(
											IJointParse<LogicalExpression, Trace> obj) {
										return obj == null ? null : obj
												.getResult();
									}
								}));
		double sum = 0;
		for (final IJointParse<LogicalExpression, Trace> parse : parses) {
			sum += (parse == null ? 0.0 : parse.getScore());
		}
		this.score = sum;
	}
	
	private static String lexToString(
			Iterable<LexicalEntry<LogicalExpression>> lexicalEntries,
			IJointModelImmutable<Sentence, Task, LogicalExpression, Trace> model,
			String prefix) {
		final StringBuilder sb = new StringBuilder();
		final Iterator<LexicalEntry<LogicalExpression>> iterator = lexicalEntries
				.iterator();
		while (iterator.hasNext()) {
			final LexicalEntry<LogicalExpression> entry = iterator.next();
			sb.append(prefix).append("[").append(model.score(entry))
					.append("] ");
			sb.append(entry);
			sb.append(" [");
			sb.append(model.getTheta()
					.printValues(model.computeFeatures(entry)));
			sb.append("]");
			if (iterator.hasNext()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
	
	@Override
	public List<Pair<LogicalExpression, Trace>> getResult() {
		return result;
	}
	
	@Override
	public double score() {
		return score;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	@Override
	public String toString(boolean verbose) {
		final StringBuilder sb = new StringBuilder();
		
		final Iterator<IJointParse<LogicalExpression, Trace>> iterator = parses
				.iterator();
		
		while (iterator.hasNext()) {
			final IJointParse<LogicalExpression, Trace> parse = iterator.next();
			if (parse == null) {
				sb.append("null\n");
			} else {
				sb.append(String.format("[%.2f] ", parse.getScore()));
				sb.append(parse.getResult().first()).append('\n');
				sb.append(parse.getResult().second());
				if (verbose) {
					sb.append('\n');
					sb.append(String.format(
							"\tFeatures: %s\n",
							model.getTheta().printValues(
									parse.getAverageMaxFeatureVector())));
					sb.append(lexToString(parse.getMaxLexicalEntries(), model,
							"\t"));
				}
			}
			if (iterator.hasNext()) {
				sb.append('\n');
			}
		}
		
		return sb.toString();
	}
}

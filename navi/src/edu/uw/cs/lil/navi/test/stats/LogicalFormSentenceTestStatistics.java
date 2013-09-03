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
package edu.uw.cs.lil.navi.test.stats;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.test.stats.IStatistics;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.composites.Pair;

/**
 * Evaluates logical form correctness only, treats only the sentence as the
 * sample (relevant for statistics that take duplication into account).
 * 
 * @author Yoav Artzi
 */
public class LogicalFormSentenceTestStatistics
		implements
		ITestingStatistics<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final String					metricName;
	private final String					prefix;
	protected final IStatistics<Sentence>	stats;
	
	public LogicalFormSentenceTestStatistics(String prefix, String metricName,
			IStatistics<Sentence> stats) {
		this.prefix = prefix;
		this.metricName = metricName;
		this.stats = stats;
	}
	
	@Override
	public void recordNoParse(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		stats.recordFailure(dataItem.getSample().first());
		
	}
	
	@Override
	public void recordNoParseWithSkipping(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		stats.recordSloppyFailure(dataItem.getSample().first());
	}
	
	@Override
	public void recordParse(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (gold.first().equals(label.second())) {
			stats.recordCorrect(dataItem.getSample().first());
		} else {
			stats.recordIncorrect(dataItem.getSample().first());
		}
	}
	
	@Override
	public void recordParses(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		stats.recordFailure(dataItem.getSample().first());
	}
	
	@Override
	public void recordParsesWithSkipping(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		stats.recordSloppyFailure(dataItem.getSample().first());
	}
	
	@Override
	public void recordParseWithSkipping(
			ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (gold.first().equals(label.second())) {
			stats.recordSloppyCorrect(dataItem.getSample().first());
		} else {
			stats.recordSloppyIncorrect(dataItem.getSample().first());
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder("=== ").append(
				getMetricName()).append(" statistics:\n");
		ret.append("Recall: ").append(stats.getCorrects()).append('/')
				.append(stats.getTotal()).append(" = ").append(stats.recall())
				.append('\n');
		ret.append("Precision: ").append(stats.getCorrects()).append('/')
				.append(stats.getTotal() - stats.getFailures()).append(" = ")
				.append(stats.precision()).append('\n');
		ret.append("F1: ").append(stats.f1()).append('\n');
		ret.append("SKIP Recall: ")
				.append(stats.getSloppyCorrects() + stats.getCorrects())
				.append('/').append(stats.getTotal()).append(" = ")
				.append(stats.sloppyRecall()).append('\n');
		ret.append("SKIP Precision: ")
				.append(stats.getSloppyCorrects() + stats.getCorrects())
				.append('/')
				.append(stats.getTotal() - stats.getSloppyFailures())
				.append(" = ").append(stats.sloppyPrecision()).append('\n');
		ret.append("SKIP F1: ").append(stats.sloppyF1());
		return ret.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		final StringBuilder ret = new StringBuilder(getPrefix())
				.append("\tmetric=").append(getMetricName()).append("\t");
		ret.append("recall=").append(stats.recall()).append('\t');
		ret.append("precision=").append(stats.precision()).append('\t');
		ret.append("f1=").append(stats.f1()).append('\t');
		ret.append("skippingRecall=").append(stats.sloppyRecall()).append('\t');
		ret.append("skippingPrecision=").append(stats.sloppyPrecision())
				.append('\t');
		ret.append("skippingF1=").append(stats.sloppyF1());
		return ret.toString();
	}
	
	protected String getMetricName() {
		return metricName;
	}
	
	protected String getPrefix() {
		return prefix == null ? "" : prefix;
	}
	
}

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
package edu.uw.cs.lil.navi.test.stats.set;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.test.stats.AbstractTestingStatistics;
import edu.uw.cs.lil.tiny.test.stats.IStatistics;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

/**
 * Evaluate the sequence of logical form interpretations against the annotated
 * logical forms. Ignores the actual execution.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class SetLogicalFormTestStatistics<MR>
		extends
		AbstractTestingStatistics<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> {
	
	public SetLogicalFormTestStatistics(
			String prefix,
			String metricName,
			IStatistics<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>>> stats) {
		super(prefix, metricName, stats);
	}
	
	private static <MR> List<MR> getLogicalFormList(List<Pair<MR, Trace>> result) {
		return ListUtils.map(result,
				new ListUtils.Mapper<Pair<MR, Trace>, MR>() {
					@Override
					public MR process(Pair<MR, Trace> obj) {
						return obj.first();
					}
				});
	}
	
	@Override
	public void recordNoParse(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold) {
		stats.recordFailure(dataItem);
	}
	
	@Override
	public void recordNoParseWithSkipping(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold) {
		stats.recordSloppyFailure(dataItem);
	}
	
	@Override
	public void recordParse(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<Pair<MR, Trace>> label) {
		final List<MR> labelLFs = getLogicalFormList(label);
		final List<MR> goldLFs = getLogicalFormList(dataItem.getLabel());
		if (labelLFs.equals(goldLFs)) {
			stats.recordCorrect(dataItem);
		} else {
			stats.recordIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParses(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<List<Pair<MR, Trace>>> labels) {
		stats.recordFailure(dataItem);
	}
	
	@Override
	public void recordParsesWithSkipping(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<List<Pair<MR, Trace>>> labels) {
		stats.recordSloppyFailure(dataItem);
	}
	
	@Override
	public void recordParseWithSkipping(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<Pair<MR, Trace>> label) {
		final List<MR> labelLFs = getLogicalFormList(label);
		final List<MR> goldLFs = getLogicalFormList(dataItem.getLabel());
		if (labelLFs.equals(goldLFs)) {
			stats.recordSloppyCorrect(dataItem);
		} else {
			stats.recordSloppyIncorrect(dataItem);
		}
	}
	
}

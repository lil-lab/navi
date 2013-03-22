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
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.composites.Pair;

public class LogicalFormTestStatistics
		implements
		ITestingStatistics<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final ITestingStatistics<Pair<Sentence, Task>, LogicalExpression>	baseStats;
	
	public LogicalFormTestStatistics(
			ITestingStatistics<Pair<Sentence, Task>, LogicalExpression> baseStats) {
		this.baseStats = baseStats;
	}
	
	@Override
	public void recordNoParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParse(dataItem, gold.first());
		
	}
	
	@Override
	public void recordNoParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParseWithSkipping(dataItem, gold.first());
	}
	
	@Override
	public void recordParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		baseStats.recordParse(dataItem, gold.first(), label.first());
	}
	
	@Override
	public void recordParses(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		baseStats.recordNoParse(dataItem, gold.first());
	}
	
	@Override
	public void recordParsesWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		baseStats.recordNoParseWithSkipping(dataItem, gold.first());
	}
	
	@Override
	public void recordParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		baseStats
				.recordParseWithSkipping(dataItem, gold.first(), label.first());
	}
	
	@Override
	public String toString() {
		return baseStats.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		return baseStats.toTabDelimitedString();
	}
}

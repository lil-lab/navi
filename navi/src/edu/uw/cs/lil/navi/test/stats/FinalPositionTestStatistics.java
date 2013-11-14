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

import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.test.stats.AbstractTestingStatistics;
import edu.uw.cs.lil.tiny.test.stats.IStatistics;
import edu.uw.cs.utils.composites.Pair;

/**
 * Evaluate the final position of the agent, including both (x,y) cooridnates
 * and orientation.
 * 
 * @author Yoav Artzi
 */
public class FinalPositionTestStatistics extends
		AbstractTestingStatistics<Instruction, Pair<LogicalExpression, Trace>> {
	
	public FinalPositionTestStatistics(
			String prefix,
			String metricName,
			IStatistics<ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>>> stats) {
		super(prefix, metricName, stats);
	}
	
	@Override
	public void recordNoParse(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		stats.recordFailure(dataItem);
	}
	
	@Override
	public void recordNoParseWithSkipping(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		stats.recordSloppyFailure(dataItem);
	}
	
	@Override
	public void recordParse(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			stats.recordFailure(dataItem);
		} else if (gold.second().getEndPosition()
				.equals(label.second().getEndPosition())) {
			stats.recordCorrect(dataItem);
		} else {
			stats.recordIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParses(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Position position = clusterPositions(labels);
		if (position == null) {
			stats.recordFailure(dataItem);
		} else if (gold.second().getEndPosition().equals(position)) {
			stats.recordCorrect(dataItem);
		} else {
			stats.recordIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParsesWithSkipping(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Position position = clusterPositions(labels);
		if (position == null) {
			stats.recordSloppyFailure(dataItem);
		} else if (gold.second().getEndPosition().equals(position)) {
			stats.recordSloppyCorrect(dataItem);
		} else {
			stats.recordSloppyIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParseWithSkipping(
			ILabeledDataItem<Instruction, Pair<LogicalExpression, Trace>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			stats.recordSloppyFailure(dataItem);
		} else if (gold.second().getEndPosition()
				.equals(label.second().getEndPosition())) {
			stats.recordSloppyCorrect(dataItem);
		} else {
			stats.recordSloppyIncorrect(dataItem);
		}
	}
	
	private Position clusterPositions(
			List<Pair<LogicalExpression, Trace>> labels) {
		Position position = null;
		for (final Pair<LogicalExpression, Trace> label : labels) {
			if (label.second() == null) {
				return null;
			} else if (position == null) {
				position = label.second().getEndPosition();
			} else if (!position.equals(label.second().getEndPosition())) {
				return null;
			}
		}
		return position;
	}
}

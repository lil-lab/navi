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
import java.util.ListIterator;

import edu.uw.cs.lil.navi.data.InstructionSeq;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.test.stats.AbstractTestingStatistics;
import edu.uw.cs.lil.tiny.test.stats.IStatistics;
import edu.uw.cs.utils.composites.Pair;

/**
 * Evaluate the final (x,y) coordinates of the agent after interpreting and
 * executing a complete sequence of instructions one after the other.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public abstract class AbstractSetTestStatistics<MR> extends
		AbstractTestingStatistics<InstructionSeq, List<Pair<MR, Trace>>> {
	
	public AbstractSetTestStatistics(
			String prefix,
			String metricName,
			IStatistics<ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>>> stats) {
		super(prefix, metricName, stats);
	}
	
	private static <MR> Coordinates clusterCoordinates(
			List<List<Pair<MR, Trace>>> labels) {
		Coordinates coordinates = null;
		for (final List<Pair<MR, Trace>> label : labels) {
			if (getEndCoordinates(label) == null) {
				return null;
			}
			if (coordinates == null) {
				coordinates = getEndCoordinates(label);
			} else if (!coordinates.equals(getEndCoordinates(label))) {
				return null;
			}
		}
		return coordinates;
	}
	
	/**
	 * May return null, indicating there's no parse.
	 * 
	 * @param result
	 * @return
	 */
	protected static <MR> Coordinates getEndCoordinates(
			List<Pair<MR, Trace>> result) {
		final ListIterator<Pair<MR, Trace>> iterator = result
				.listIterator(result.size());
		while (iterator.hasPrevious()) {
			final Pair<MR, Trace> previous = iterator.previous();
			if (previous != null && previous.second() != null) {
				return previous.second().getEndPosition().getPose()
						.getCoordinates();
			}
		}
		return null;
	}
	
	@Override
	public void recordNoParse(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold) {
		stats.recordFailure(dataItem);
	}
	
	@Override
	public void recordNoParseWithSkipping(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold) {
		stats.recordSloppyFailure(dataItem);
	}
	
	@Override
	public void recordParse(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<Pair<MR, Trace>> label) {
		final Coordinates labelEnd = getEndCoordinates(label);
		final Coordinates goal = getGoal(dataItem);
		if (labelEnd == null) {
			stats.recordFailure(dataItem);
		} else if (labelEnd.equals(goal)) {
			stats.recordCorrect(dataItem);
		} else {
			stats.recordIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParses(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<List<Pair<MR, Trace>>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		final Coordinates goal = getGoal(dataItem);
		if (coordinates == null) {
			stats.recordFailure(dataItem);
		} else if (coordinates.equals(goal)) {
			stats.recordCorrect(dataItem);
		} else {
			stats.recordIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParsesWithSkipping(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<List<Pair<MR, Trace>>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		final Coordinates goal = getGoal(dataItem);
		if (coordinates == null) {
			stats.recordSloppyFailure(dataItem);
		} else if (coordinates.equals(goal)) {
			stats.recordSloppyCorrect(dataItem);
		} else {
			stats.recordSloppyIncorrect(dataItem);
		}
	}
	
	@Override
	public void recordParseWithSkipping(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem,
			List<Pair<MR, Trace>> gold, List<Pair<MR, Trace>> label) {
		final Coordinates labelEnd = getEndCoordinates(label);
		final Coordinates goal = getGoal(dataItem);
		if (labelEnd == null) {
			stats.recordSloppyFailure(dataItem);
		} else if (labelEnd.equals(goal)) {
			stats.recordSloppyCorrect(dataItem);
		} else {
			stats.recordSloppyIncorrect(dataItem);
		}
	}
	
	protected abstract Coordinates getGoal(
			ILabeledDataItem<InstructionSeq, List<Pair<MR, Trace>>> dataItem);
	
}

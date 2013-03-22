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

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.composites.Pair;

public class SetFinalCoordinatesTestStatistics<Y> implements
		ITestingStatistics<Pair<List<Sentence>, Task>, List<Pair<Y, Trace>>> {
	
	private final ITestingStatistics<Pair<List<Sentence>, Task>, Coordinates>	baseStats;
	
	public SetFinalCoordinatesTestStatistics(
			ITestingStatistics<Pair<List<Sentence>, Task>, Coordinates> baseStats) {
		this.baseStats = baseStats;
	}
	
	/**
	 * May return null, basically indicating there's no parse.
	 * 
	 * @param result
	 * @return
	 */
	private static <Y> Coordinates getEndCoordinates(List<Pair<Y, Trace>> result) {
		final ListIterator<Pair<Y, Trace>> iterator = result
				.listIterator(result.size());
		while (iterator.hasPrevious()) {
			final Pair<Y, Trace> previous = iterator.previous();
			if (previous != null && previous.second() != null) {
				return previous.second().getEndPosition().getPose()
						.getCoordinates();
			}
		}
		return null;
	}
	
	@Override
	public void recordNoParse(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold) {
		baseStats.recordNoParse(dataItem, getEndCoordinates(gold));
	}
	
	@Override
	public void recordNoParseWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold) {
		baseStats.recordNoParseWithSkipping(dataItem, getEndCoordinates(gold));
	}
	
	@Override
	public void recordParse(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<Pair<Y, Trace>> label) {
		if (getEndCoordinates(label) == null) {
			baseStats.recordNoParse(dataItem, getEndCoordinates(gold));
		} else {
			baseStats.recordParse(dataItem, getEndCoordinates(gold),
					getEndCoordinates(label));
		}
	}
	
	@Override
	public void recordParses(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<List<Pair<Y, Trace>>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		if (coordinates == null) {
			baseStats.recordNoParse(dataItem, getEndCoordinates(gold));
		} else {
			baseStats.recordParse(dataItem, getEndCoordinates(gold),
					coordinates);
		}
	}
	
	@Override
	public void recordParsesWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<List<Pair<Y, Trace>>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		if (coordinates == null) {
			baseStats.recordNoParseWithSkipping(dataItem,
					getEndCoordinates(gold));
		} else {
			baseStats.recordParseWithSkipping(dataItem,
					getEndCoordinates(gold), coordinates);
		}
	}
	
	@Override
	public void recordParseWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<Pair<Y, Trace>> label) {
		if (getEndCoordinates(label) == null) {
			baseStats.recordNoParseWithSkipping(dataItem,
					getEndCoordinates(gold));
		} else {
			baseStats.recordParseWithSkipping(dataItem,
					getEndCoordinates(gold), getEndCoordinates(label));
		}
	}
	
	@Override
	public String toString() {
		return baseStats.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		return baseStats.toTabDelimitedString();
	}
	
	private Coordinates clusterCoordinates(List<List<Pair<Y, Trace>>> labels) {
		Coordinates coordinates = null;
		for (final List<Pair<Y, Trace>> label : labels) {
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
}

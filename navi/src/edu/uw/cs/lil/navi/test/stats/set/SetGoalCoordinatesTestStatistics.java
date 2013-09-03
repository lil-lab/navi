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
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.test.stats.IStatistics;
import edu.uw.cs.utils.composites.Pair;

/**
 * Evaluate the final (x,y) coordinates of the agent after interpreting and
 * executing a complete sequence of instructions one after the other. Compared
 * against the original task goal.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class SetGoalCoordinatesTestStatistics<MR> extends
		AbstractSetTestStatistics<MR> {
	
	public SetGoalCoordinatesTestStatistics(
			String prefix,
			String metricName,
			IStatistics<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>>> stats) {
		super(prefix, metricName, stats);
	}
	
	@Override
	protected Coordinates getGoal(
			ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> dataItem) {
		return dataItem.getSample().second().getPositionX().getAllCoordinates()
				.iterator().next();
	}
	
}

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
package edu.uw.cs.lil.navi.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.utils.assertion.Assert;
import edu.uw.cs.utils.collections.ListUtils;

public class Trace {
	private final Position		endPosition;
	private final Position		startPosition;
	private final List<Step>	steps;
	
	public Trace(List<Step> steps, Position endPosition) {
		this.steps = steps;
		this.endPosition = Assert.ifNull(endPosition);
		this.startPosition = steps.isEmpty() ? endPosition : steps.get(0)
				.getPosition();
	}
	
	public static Trace parseLine(String string, NavigationMap navigationMap) {
		final List<String> split = Arrays.asList(string.split(";"));
		final Iterator<String> iterator = split.iterator();
		final List<Step> steps = new ArrayList<Step>(split.size() - 1);
		Position endPosition = null;
		while (iterator.hasNext()) {
			final String current = iterator.next();
			if (iterator.hasNext()) {
				final Step step = Step.valueOf(current, navigationMap);
				steps.add(step);
			} else {
				// Case last place in the line, just the last position
				endPosition = navigationMap.get(Pose.valueOf(current));
			}
		}
		return new Trace(Collections.unmodifiableList(steps), endPosition);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Trace other = (Trace) obj;
		if (endPosition == null) {
			if (other.endPosition != null) {
				return false;
			}
		} else if (!endPosition.equals(other.endPosition)) {
			return false;
		}
		if (startPosition == null) {
			if (other.startPosition != null) {
				return false;
			}
		} else if (!startPosition.equals(other.startPosition)) {
			return false;
		}
		if (steps == null) {
			if (other.steps != null) {
				return false;
			}
		} else if (!steps.equals(other.steps)) {
			return false;
		}
		return true;
	}
	
	public Position getEndPosition() {
		return endPosition;
	}
	
	public Position getStartPosition() {
		return startPosition;
	}
	
	public List<Step> getSteps() {
		return steps;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endPosition == null) ? 0 : endPosition.hashCode());
		result = prime * result
				+ ((startPosition == null) ? 0 : startPosition.hashCode());
		result = prime * result + ((steps == null) ? 0 : steps.hashCode());
		return result;
	}
	
	public boolean hasImplicitSteps() {
		for (final Step step : steps) {
			if (step.isImplicit()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(ListUtils.join(steps, ";"))
				.append(steps.isEmpty() ? "" : ";").append(endPosition)
				.toString();
	}
	
}

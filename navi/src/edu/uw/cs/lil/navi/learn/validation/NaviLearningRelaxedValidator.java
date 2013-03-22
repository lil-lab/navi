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
package edu.uw.cs.lil.navi.learn.validation;

import java.util.Iterator;
import java.util.List;

import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviLearningRelaxedValidator implements
		IValidator<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	private static final ILogger	LOG	= LoggerFactory
												.create(NaviLearningRelaxedValidator.class);
	
	public NaviLearningRelaxedValidator() {
		LOG.info("Init: %s", NaviLearningRelaxedValidator.class.getName());
	}
	
	private static boolean relaxedTraceComparison(Trace t1, Trace t2) {
		if (t1 == null || t2 == null) {
			return t1 == t2;
		}
		
		final List<Step> steps1 = t1.getSteps();
		final List<Step> steps2 = t2.getSteps();
		if (t1.getStartPosition().equals(t2.getStartPosition())
				&& t1.getEndPosition().equals(t2.getEndPosition())
				&& steps1.size() == steps2.size()) {
			final Iterator<Step> iterator1 = steps1.iterator();
			final Iterator<Step> iterator2 = steps2.iterator();
			while (iterator1.hasNext()) {
				if (!iterator1.next().equalsWithoutImplicit(iterator2.next())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isValid(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> label) {
		if (dataItem instanceof ILabeledDataItem) {
			final Object dataItemLabel = ((ILabeledDataItem<?, ?>) dataItem)
					.getLabel();
			if (dataItemLabel instanceof Trace) {
				return relaxedTraceComparison((Trace) dataItemLabel,
						label.second());
			} else {
				throw new RuntimeException("Invalid dataItem type: " + dataItem);
			}
		} else {
			LOG.error("Can't validate using: %s", dataItem);
			return false;
		}
	}
}

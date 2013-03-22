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

/**
 * Weak validator that only validates the end position.
 * 
 * @author Yoav Artzi
 */
public class NaviLearningWeakValidator implements
		IValidator<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	private static final ILogger	LOG	= LoggerFactory
												.create(NaviLearningWeakValidator.class);
	
	public NaviLearningWeakValidator() {
		LOG.info("Init: %s", NaviLearningWeakValidator.class.getName());
	}
	
	@Override
	public boolean isValid(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> label) {
		if (dataItem instanceof ILabeledDataItem) {
			final Object dataItemLabel = ((ILabeledDataItem<?, ?>) dataItem)
					.getLabel();
			if (dataItemLabel instanceof Trace) {
				return weakTraceComparison((Trace) dataItemLabel,
						label.second());
			} else {
				throw new RuntimeException("Invalid dataItem type: " + dataItem);
			}
		} else {
			LOG.error("Can't validate using: %s", dataItem);
			return false;
		}
	}
	
	private boolean weakTraceComparison(Trace t1, Trace t2) {
		if (t1 == null || t2 == null) {
			return t1 == t2;
		} else {
			return t1.getEndPosition().equals(t2.getEndPosition());
		}
	}
	
}

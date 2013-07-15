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
 * Wrapper for trace validator, which allows to validate the logical form as
 * well, if the data item supports it.
 * 
 * @author Yoav Artzi
 */
public class NaviPairValidatorWrapper
		implements
		IValidator<IDataItem<Pair<Sentence, Task>>, Pair<LogicalExpression, Trace>> {
	private static final ILogger	LOG	= LoggerFactory
												.create(NaviPairValidatorWrapper.class);
	
	private final INaviValidator	resultValidator;
	
	public NaviPairValidatorWrapper(INaviValidator resultValidator) {
		this.resultValidator = resultValidator;
	}
	
	@Override
	public boolean isValid(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> label) {
		if (dataItem instanceof ILabeledDataItem) {
			final Object dataItemLabel = ((ILabeledDataItem<?, ?>) dataItem)
					.getLabel();
			if (dataItemLabel instanceof Trace) {
				return resultValidator.isValid(dataItem, label.second());
			} else if (dataItemLabel instanceof Pair) {
				final Object second = ((Pair<?, ?>) dataItemLabel).second();
				if (second instanceof Trace) {
					return resultValidator.isValid(dataItem, label.second())
							&& ((Pair<?, ?>) dataItemLabel).first().equals(
									label.first());
				}
			}
		}
		LOG.error("Can't validate using: %s", dataItem);
		return false;
	}
	
}

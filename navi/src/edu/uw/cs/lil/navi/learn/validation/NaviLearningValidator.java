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
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Strict validation: uses all the information in the demonstration trace.
 * 
 * @author Yoav Artzi
 */
public class NaviLearningValidator implements INaviValidator {
	private static final ILogger	LOG	= LoggerFactory
												.create(NaviLearningValidator.class);
	
	@Override
	public boolean isValid(IDataItem<Pair<Sentence, Task>> dataItem, Trace label) {
		
		if (dataItem instanceof ILabeledDataItem) {
			final Object dataItemLabel = ((ILabeledDataItem<?, ?>) dataItem)
					.getLabel();
			if (!(dataItemLabel instanceof Pair)) {
				return dataItemLabel.equals(label);
			} else {
				return ((Pair<?, ?>) dataItemLabel).second().equals(label);
			}
		}
		LOG.error("Can't validate using: %s", dataItem);
		return false;
	}
}

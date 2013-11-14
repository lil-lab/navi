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

import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Relaxed validation: doesn't take into account implicit action flag during
 * comparison. Compares the entire trace.
 * 
 * @author Yoav Artzi
 */
public class NaviLearningRelaxedValidator implements
		IValidator<InstructionTrace, Trace> {
	public static final ILogger	LOG	= LoggerFactory
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
	public boolean isValid(InstructionTrace dataItem, Trace label) {
		return relaxedTraceComparison(dataItem.getLabel(), label);
	}
	
	public static class Creator<MR> implements
			IResourceObjectCreator<NaviLearningRelaxedValidator> {
		
		@Override
		public NaviLearningRelaxedValidator create(Parameters params,
				IResourceRepository repo) {
			return new NaviLearningRelaxedValidator();
		}
		
		@Override
		public String type() {
			return "navi.validator.relaxed";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(),
					NaviLearningRelaxedValidator.class)
					.setDescription(
							"Learning validtion function that validates the complete executiont trace (without implicit markings).")
					.build();
		}
		
	}
}

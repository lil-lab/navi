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

import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

/**
 * Strict validation: uses all the information in the demonstration trace,
 * including inofrmation about an action being implicit.
 * 
 * @author Yoav Artzi
 */
public class NaviLearningValidator implements
		IValidator<InstructionTrace, Trace> {
	
	@Override
	public boolean isValid(InstructionTrace dataItem, Trace label) {
		return dataItem.getLabel().equals(label);
	}
	
	public static class Creator implements
			IResourceObjectCreator<NaviLearningValidator> {
		
		@Override
		public NaviLearningValidator create(Parameters params,
				IResourceRepository repo) {
			return new NaviLearningValidator();
		}
		
		@Override
		public String type() {
			return "navi.validator";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(),
					NaviLearningValidator.class)
					.setDescription(
							"Learning validtion function that validates the complete executiont trace (incl. implicit markings).")
					.build();
		}
		
	}
}

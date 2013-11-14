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
import edu.uw.cs.utils.composites.Pair;

/**
 * Wrapper for trace validator, which takes a result that includes the logical
 * form as well, but ignores it.
 * 
 * @author Yoav Artzi
 */
public class NaviPairValidatorWrapper<MR> implements
		IValidator<InstructionTrace, Pair<MR, Trace>> {
	private final IValidator<InstructionTrace, Trace>	resultValidator;
	
	public NaviPairValidatorWrapper(
			IValidator<InstructionTrace, Trace> resultValidator) {
		this.resultValidator = resultValidator;
	}
	
	@Override
	public boolean isValid(InstructionTrace dataItem, Pair<MR, Trace> label) {
		return resultValidator.isValid(dataItem, label.second());
	}
	
	public static class Creator<MR> implements
			IResourceObjectCreator<NaviPairValidatorWrapper<MR>> {
		
		@SuppressWarnings("unchecked")
		@Override
		public NaviPairValidatorWrapper<MR> create(Parameters params,
				IResourceRepository repo) {
			return new NaviPairValidatorWrapper<MR>(
					(IValidator<InstructionTrace, Trace>) repo
							.getResource(params.get("base")));
		}
		
		@Override
		public String type() {
			return "navi.validator.wrapper";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(),
					NaviPairValidatorWrapper.class).addParam("base",
					IValidator.class, "Base validator to to handle traces")
					.build();
		}
		
	}
	
}

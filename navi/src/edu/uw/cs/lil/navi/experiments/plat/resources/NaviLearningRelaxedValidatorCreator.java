package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.learn.validation.NaviLearningRelaxedValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

public class NaviLearningRelaxedValidatorCreator implements
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

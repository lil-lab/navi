package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.learn.validation.NaviLearningValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

public class NaviLearningValidatorCreator implements
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
		return new ResourceUsage.Builder(type(), NaviLearningValidator.class)
				.setDescription(
						"Learning validtion function that validates the complete executiont trace (incl. implicit markings).")
				.build();
	}
	
}

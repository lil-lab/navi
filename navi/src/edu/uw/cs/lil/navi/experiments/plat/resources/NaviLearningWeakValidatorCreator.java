package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.learn.validation.NaviLearningWeakValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

public class NaviLearningWeakValidatorCreator implements
		IResourceObjectCreator<NaviLearningWeakValidator> {
	
	@Override
	public NaviLearningWeakValidator create(Parameters params,
			IResourceRepository repo) {
		return new NaviLearningWeakValidator();
	}
	
	@Override
	public String type() {
		return "navi.validator.weak";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(),
				NaviLearningWeakValidator.class)
				.setDescription(
						"Learning validtion function that validates only the final state of the execution.")
				.build();
	}
	
}

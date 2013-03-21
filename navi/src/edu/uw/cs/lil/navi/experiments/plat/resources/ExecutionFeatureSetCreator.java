package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.features.ExecutionFeatureSet;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

public class ExecutionFeatureSetCreator implements
		IResourceObjectCreator<ExecutionFeatureSet> {
	
	@Override
	public ExecutionFeatureSet create(Parameters params,
			IResourceRepository repo) {
		return new ExecutionFeatureSet(
				(NaviSingleEvaluator) repo
						.getResource(NaviExperiment.SINGLE_EVALUATOR),
				Integer.valueOf(params.get("cache")),
				params.contains("scale") ? Double.valueOf(params.get("scale"))
						: 1.0);
	}
	
	@Override
	public String type() {
		return "feat.exec.lex";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), ExecutionFeatureSet.class)
				.setDescription("Execution features")
				.addParam("scale", "double",
						"Feature scaling factor. Default: 1.0.")
				.addParam("cache", "int", "Cache size").build();
	}
	
}

package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.exec.NaviSingleExecutor;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.parse.NaviParser;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;

public class NaviSingleExecutorCreator implements
		IResourceObjectCreator<NaviSingleExecutor> {
	
	@SuppressWarnings("unchecked")
	@Override
	public NaviSingleExecutor create(Parameters params, IResourceRepository repo) {
		return new NaviSingleExecutor(
				(NaviParser) repo.getResource(NaviExperiment.PARSER_RESOURCE),
				(JointModel<Sentence, Task, LogicalExpression, Trace>) repo
						.getResource(params.get("model")), "true".equals(params
						.get("pruneFails")));
	}
	
	@Override
	public String type() {
		return "exec.single";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), NaviSingleExecutor.class)
				.setDescription("Single instruction executor")
				.addParam("model", "id",
						"Joint model to use for computing features and scores")
				.addParam("pruneFails", "boolean",
						"Consider failed execution as incomplete parses. Default: false.")
				.build();
	}
	
}

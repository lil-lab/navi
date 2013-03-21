package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.exec.NaviNaiveSeqExecutor;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.parse.NaviParser;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;

public class NaviNaiveSeqExecutorCreator implements
		IResourceObjectCreator<NaviNaiveSeqExecutor> {
	
	@SuppressWarnings("unchecked")
	@Override
	public NaviNaiveSeqExecutor create(Parameters params,
			IResourceRepository repo) {
		return new NaviNaiveSeqExecutor(
				(NaviParser) repo.getResource(NaviExperiment.PARSER_RESOURCE),
				(JointModel<Sentence, Task, LogicalExpression, Trace>) repo
						.getResource(params.get("model")),
				"false".equals(params.get("pruneAtionless")));
	}
	
	@Override
	public String type() {
		return "exec.set.naive";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), NaviNaiveSeqExecutor.class)
				.setDescription(
						"Naive (baseline) executor for sequences of instructions.")
				.addParam("model", "id", "Joint model for inference")
				.addParam("pruneActionless", "boolean",
						"Prune parses that fail to generate valid actions. Default: true.")
				.build();
	}
	
}

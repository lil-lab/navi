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
package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.exec.NaviNaiveSeqExecutor;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;

public class NaviNaiveSeqExecutorCreator implements
		IResourceObjectCreator<NaviNaiveSeqExecutor> {
	
	@SuppressWarnings("unchecked")
	@Override
	public NaviNaiveSeqExecutor create(Parameters params,
			IResourceRepository repo) {
		return new NaviNaiveSeqExecutor(
				(IJointParser<Sentence, Task, LogicalExpression, Trace, Trace>) repo
						.getResource(NaviExperiment.PARSER_RESOURCE),
				(JointModel<Sentence, Task, LogicalExpression, Trace>) repo
						.getResource(params.get("model")), "false"
						.equals(params.get("pruneAtionless")));
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

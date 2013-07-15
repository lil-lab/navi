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
import edu.uw.cs.lil.navi.exec.NaviSingleExecutor;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;

public class NaviSingleExecutorCreator implements
		IResourceObjectCreator<NaviSingleExecutor> {
	
	@SuppressWarnings("unchecked")
	@Override
	public NaviSingleExecutor create(Parameters params, IResourceRepository repo) {
		return new NaviSingleExecutor(
				(IJointParser<Sentence, Task, LogicalExpression, Trace, Trace>) repo
						.getResource(NaviExperiment.PARSER_RESOURCE),
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

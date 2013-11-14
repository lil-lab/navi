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
package edu.uw.cs.lil.navi.exec;

import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.exec.JointExecutionOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.utils.composites.Pair;

public class NaviSingleExecutor implements
		IExec<Instruction, Pair<LogicalExpression, Trace>> {
	private final JointModel<Instruction, LogicalExpression, Trace>				model;
	
	private final IJointParser<Instruction, LogicalExpression, Trace, Trace>	parser;
	private final boolean														pruneFails;
	
	public NaviSingleExecutor(
			IJointParser<Instruction, LogicalExpression, Trace, Trace> parser,
			JointModel<Instruction, LogicalExpression, Trace> model,
			boolean pruneActionless) {
		this.parser = parser;
		this.model = model;
		this.pruneFails = pruneActionless;
	}
	
	@Override
	public JointExecutionOutput<LogicalExpression, Trace> execute(
			Instruction dataItem) {
		return execute(dataItem, false);
	}
	
	@Override
	public JointExecutionOutput<LogicalExpression, Trace> execute(
			Instruction dataItem, boolean sloppy) {
		final IJointDataItemModel<LogicalExpression, Trace> dataItemModel = model
				.createJointDataItemModel(dataItem);
		return new JointExecutionOutput<LogicalExpression, Trace>(parser.parse(
				dataItem, dataItemModel, sloppy), dataItemModel, pruneFails);
	}
	
	public static class Creator implements
			IResourceObjectCreator<NaviSingleExecutor> {
		
		@SuppressWarnings("unchecked")
		@Override
		public NaviSingleExecutor create(Parameters params,
				IResourceRepository repo) {
			return new NaviSingleExecutor(
					(IJointParser<Instruction, LogicalExpression, Trace, Trace>) repo
							.getResource(NaviExperiment.PARSER_RESOURCE),
					(JointModel<Instruction, LogicalExpression, Trace>) repo
							.getResource(params.get("model")), "true"
							.equals(params.get("pruneFails")));
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
	
}

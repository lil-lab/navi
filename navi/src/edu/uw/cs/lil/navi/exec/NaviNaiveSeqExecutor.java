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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.data.InstructionSeq;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.exec.IExecOutput;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.utils.composites.Pair;

/**
 * Naive instruction sequences executor. Iterates over the sequence of
 * instructions. Each time executes the instruction, if all top parses lead to
 * the same position, will execute the next instruction from that position.
 * Otherwise, if there's ambiguity, will fail. Considers failed execution as
 * complete parses too.
 * 
 * @author Yoav Artzi
 */
public class NaviNaiveSeqExecutor implements
		IExec<InstructionSeq, List<Pair<LogicalExpression, Trace>>> {
	private final JointModel<Instruction, LogicalExpression, Trace>				model;
	private final IJointParser<Instruction, LogicalExpression, Trace, Trace>	parser;
	private final boolean														pruneActionless;
	
	public NaviNaiveSeqExecutor(
			IJointParser<Instruction, LogicalExpression, Trace, Trace> parser,
			JointModel<Instruction, LogicalExpression, Trace> model,
			boolean pruneActionless) {
		this.parser = parser;
		this.model = model;
		this.pruneActionless = pruneActionless;
	}
	
	@Override
	public IExecOutput<List<Pair<LogicalExpression, Trace>>> execute(
			InstructionSeq dataItem) {
		return execute(dataItem, false);
	}
	
	@Override
	public IExecOutput<List<Pair<LogicalExpression, Trace>>> execute(
			InstructionSeq dataItem, boolean sloppy) {
		final long startTime = System.currentTimeMillis();
		
		Task currentTask = dataItem.getState();
		final List<List<IJointParse<LogicalExpression, Trace>>> parseLists = new LinkedList<List<IJointParse<LogicalExpression, Trace>>>();
		
		// Init with empty list
		parseLists.add(new ArrayList<IJointParse<LogicalExpression, Trace>>());
		
		for (final Sentence sentence : dataItem) {
			final Instruction instruction = new Instruction(sentence,
					currentTask);
			final IJointDataItemModel<LogicalExpression, Trace> dataItemModel = model
					.createJointDataItemModel(instruction);
			final IJointOutput<LogicalExpression, Trace> parserOutput = parser
					.parse(instruction, dataItemModel, sloppy);
			Position currentPosition = null;
			final List<IJointParse<LogicalExpression, Trace>> newParses = new LinkedList<IJointParse<LogicalExpression, Trace>>();
			for (final IJointParse<LogicalExpression, Trace> parse : parserOutput
					.getBestParses(!pruneActionless)) {
				
				if (parse.getResult().second() != null
						&& (currentPosition == null || currentPosition
								.equals(parse.getResult().second()
										.getEndPosition()))) {
					// Case successful, and no ambiguity
					currentPosition = parse.getResult().second()
							.getEndPosition();
					newParses.add(parse);
				} else {
					// Case failed, or ambiguity, fail the entire set
					return new NaviSetExecutionOutput(
							new LinkedList<List<IJointParse<LogicalExpression, Trace>>>(),
							model, System.currentTimeMillis() - startTime);
				}
			}
			
			if (currentPosition == null) {
				// Case failed, not executable parse found
				return new NaviSetExecutionOutput(
						new LinkedList<List<IJointParse<LogicalExpression, Trace>>>(),
						model, System.currentTimeMillis() - startTime);
			} else {
				currentTask = currentTask
						.updateAgent(new Agent(currentPosition));
			}
			
			final List<List<IJointParse<LogicalExpression, Trace>>> newParseLists = new LinkedList<List<IJointParse<LogicalExpression, Trace>>>();
			for (final List<IJointParse<LogicalExpression, Trace>> preParses : parseLists) {
				for (final IJointParse<LogicalExpression, Trace> newParse : newParses) {
					final List<IJointParse<LogicalExpression, Trace>> newParseList = new ArrayList<IJointParse<LogicalExpression, Trace>>(
							preParses.size() + 1);
					newParseList.addAll(preParses);
					newParseList.add(newParse);
					newParseLists.add(newParseList);
				}
			}
			parseLists.clear();
			parseLists.addAll(newParseLists);
		}
		
		return new NaviSetExecutionOutput(parseLists, model,
				System.currentTimeMillis() - startTime);
	}
	
	public static class Creator implements
			IResourceObjectCreator<NaviNaiveSeqExecutor> {
		
		@SuppressWarnings("unchecked")
		@Override
		public NaviNaiveSeqExecutor create(Parameters params,
				IResourceRepository repo) {
			return new NaviNaiveSeqExecutor(
					(IJointParser<Instruction, LogicalExpression, Trace, Trace>) repo
							.getResource(NaviExperiment.PARSER_RESOURCE),
					(JointModel<Instruction, LogicalExpression, Trace>) repo
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
}

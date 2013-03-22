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
package edu.uw.cs.lil.navi.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class InstructionSeqTrace<Y> implements Iterable<InstructionTrace<Y>>,
		ILabeledDataItem<Pair<List<Sentence>, Task>, List<Trace>> {
	private static final String					ID_KEY			= "id";
	
	private static final ILogger				LOG				= LoggerFactory
																		.create(InstructionSeqTrace.class);
	
	private static final String					MAP_NAME_KEY	= "map";
	
	private final List<Pair<Sentence, Trace>>	instructions;
	
	private final List<Trace>					label;
	
	private final Pair<List<Sentence>, Task>	samplePair;
	private final List<InstructionTrace<Y>>			singleTraces;
	private final Task							task;
	
	public InstructionSeqTrace(
			List<Pair<Sentence, Trace>> instructions,
			final Task task,
			final ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator) {
		this.instructions = Collections.unmodifiableList(instructions);
		this.task = task;
		this.samplePair = Pair.of(Collections.unmodifiableList(ListUtils.map(
				instructions,
				new ListUtils.Mapper<Pair<Sentence, Trace>, Sentence>() {
					
					@Override
					public Sentence process(Pair<Sentence, Trace> obj) {
						return obj.first();
					}
				})), task);
		this.label = Collections.unmodifiableList(ListUtils.map(instructions,
				new ListUtils.Mapper<Pair<Sentence, Trace>, Trace>() {
					
					@Override
					public Trace process(Pair<Sentence, Trace> obj) {
						return obj.second();
					}
				}));
		this.singleTraces = Collections.unmodifiableList(ListUtils.map(
				instructions,
				new ListUtils.Mapper<Pair<Sentence, Trace>, InstructionTrace<Y>>() {
					
					@Override
					public InstructionTrace<Y> process(Pair<Sentence, Trace> obj) {
						return new InstructionTrace<Y>(obj.first(), task
								.updateAgent(new Agent(obj.second()
										.getStartPosition())), obj.second(),
								lexiconGenerator);
					}
					
				}));
		
	}
	
	public static <Y> InstructionSeqTrace<Y> parse(
			String string,
			Map<String, NavigationMap> maps,
			ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator) {
		final LinkedList<String> lines = new LinkedList<String>(
				Arrays.asList(string.split("\n")));
		final String id = lines.pollFirst();
		final Map<String, String> properties = parseProperties(lines
				.pollFirst());
		properties.put(ID_KEY, id);
		final NavigationMap map = maps.get(properties.get(MAP_NAME_KEY));
		Position startPosition = null;
		final List<Pair<Sentence, Trace>> instructions = new LinkedList<Pair<Sentence, Trace>>();
		while (!lines.isEmpty()) {
			final Sentence sentence = new Sentence(lines.pollFirst());
			final Trace trace = Trace.parseLine(lines.pollFirst(), map);
			if (startPosition == null) {
				startPosition = trace.getStartPosition();
			}
			instructions.add(Pair.of(sentence, trace));
		}
		
		// If the instruction set is valid but incorrect (leads to the wrong
		// position), use the specified alternative goal
		final Position goal;
		final Position officialGoal = map.get(Integer.valueOf(properties
				.get("x")));
		if ("False".equals(properties.get("correct"))
				&& "True".equals(properties.get("valid"))) {
			goal = map.get(Pose.valueOf(properties.get("xalt")));
			LOG.info("Modified goal for %s: %s -> %s", id,
					officialGoal.toString(), goal.toString());
		} else {
			goal = officialGoal;
		}
		
		final Task task = new Task(new Agent(startPosition), new PositionSet(
				map.get(Integer.valueOf(properties.get("y")))
						.getAllOrientations(), false), new PositionSet(
				goal.getAllOrientations(), false), properties, map);
		return new InstructionSeqTrace<Y>(instructions, task, lexiconGenerator);
	}
	
	private static Map<String, String> parseProperties(String line) {
		final String[] split = line.split("\\t+");
		final Map<String, String> properties = new HashMap<String, String>();
		for (final String entry : split) {
			final String[] entrySplit = entry.split("=", 2);
			properties.put(entrySplit[0], entrySplit[1]);
		}
		return properties;
	}
	
	@Override
	public double calculateLoss(List<Trace> y) {
		return label.equals(y) ? 0.0 : 1.0;
	}
	
	public String getId() {
		return task.getProperty(ID_KEY);
	}
	
	public List<Pair<Sentence, Trace>> getInstructions() {
		return instructions;
	}
	
	@Override
	public List<Trace> getLabel() {
		return label;
	}
	
	@Override
	public Pair<List<Sentence>, Task> getSample() {
		return samplePair;
	}
	
	public Task getTask() {
		return task;
	}
	
	@Override
	public boolean isCorrect(List<Trace> y) {
		return label.equals(y);
	}
	
	@Override
	public Iterator<InstructionTrace<Y>> iterator() {
		return singleTraces.iterator();
	}
	
	@Override
	public boolean prune(List<Trace> y) {
		return !isCorrect(y);
	}
	
	@Override
	public double quality() {
		return 1.0;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(task);
		for (final Pair<Sentence, Trace> instruction : instructions) {
			sb.append('\n');
			sb.append(instruction.first()).append('\n');
			sb.append(instruction.second());
		}
		return sb.toString();
	}
	
}

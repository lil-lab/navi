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
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.genlex.ccg.ILexiconGenerator;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.composites.Triplet;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Sequence of consecutive instructions, each paired with a demonstration and a
 * logical form.
 * 
 * @author Yoav Artzi
 * @see LabeledInstructionTrace
 * @param <MR>
 *            Type of meaning representation
 */
public class LabeledInstructionSeqTrace<MR> implements
		Iterable<LabeledInstructionTrace<MR>>,
		ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<MR, Trace>>> {
	private static final String							ID_KEY			= "id";
	private static final ILogger						LOG				= LoggerFactory
																				.create(LabeledInstructionSeqTrace.class);
	
	private static final String							MAP_NAME_KEY	= "map";
	
	private final List<Triplet<Sentence, MR, Trace>>	instructions;
	
	private final List<Pair<MR, Trace>>					labelList;
	
	private final Pair<List<Sentence>, Task>			samplePair;
	private final List<LabeledInstructionTrace<MR>>		singleTraces;
	private final Task									task;
	
	public LabeledInstructionSeqTrace(
			List<Triplet<Sentence, MR, Trace>> instructions,
			final Task task,
			final ILexiconGenerator<ILabeledDataItem<Pair<Sentence, Task>, Pair<MR, Trace>>, MR> lexiconGenerator) {
		this.instructions = Collections.unmodifiableList(instructions);
		this.task = task;
		this.samplePair = Pair.of(Collections.unmodifiableList(ListUtils.map(
				instructions,
				new ListUtils.Mapper<Triplet<Sentence, MR, Trace>, Sentence>() {
					
					@Override
					public Sentence process(Triplet<Sentence, MR, Trace> obj) {
						return obj.first();
					}
				})), task);
		this.labelList = Collections
				.unmodifiableList(ListUtils
						.map(instructions,
								new ListUtils.Mapper<Triplet<Sentence, MR, Trace>, Pair<MR, Trace>>() {
									
									@Override
									public Pair<MR, Trace> process(
											Triplet<Sentence, MR, Trace> obj) {
										return Pair.of(obj.second(),
												obj.third());
									}
								}));
		this.singleTraces = Collections
				.unmodifiableList(ListUtils
						.map(instructions,
								new ListUtils.Mapper<Triplet<Sentence, MR, Trace>, LabeledInstructionTrace<MR>>() {
									
									@Override
									public LabeledInstructionTrace<MR> process(
											Triplet<Sentence, MR, Trace> obj) {
										return new LabeledInstructionTrace<MR>(
												obj.second(), obj.first(),
												task.updateAgent(new Agent(obj
														.third()
														.getStartPosition())),
												obj.third(), lexiconGenerator);
									}
									
								}));
		
	}
	
	public static <MR> LabeledInstructionSeqTrace<MR> parse(
			String string,
			Map<String, NavigationMap> maps,
			ILexiconGenerator<ILabeledDataItem<Pair<Sentence, Task>, Pair<MR, Trace>>, MR> lexiconGenerator,
			ICategoryServices<MR> categoryServices) {
		final LinkedList<String> lines = new LinkedList<String>(
				Arrays.asList(string.split("\n")));
		final String id = lines.pollFirst();
		final Map<String, String> properties = parseProperties(lines
				.pollFirst());
		properties.put(ID_KEY, id);
		final NavigationMap map = maps.get(properties.get(MAP_NAME_KEY));
		Position startPosition = null;
		final List<Triplet<Sentence, MR, Trace>> instructions = new LinkedList<Triplet<Sentence, MR, Trace>>();
		while (!lines.isEmpty()) {
			final Sentence sentence = new Sentence(lines.pollFirst());
			final MR semantics = categoryServices.parseSemantics(lines
					.pollFirst());
			final Trace trace = Trace.parseLine(lines.pollFirst(), map);
			if (startPosition == null) {
				startPosition = trace.getStartPosition();
			}
			instructions.add(Triplet.of(sentence, semantics, trace));
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
		return new LabeledInstructionSeqTrace<MR>(instructions, task,
				lexiconGenerator);
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
	public double calculateLoss(List<Pair<MR, Trace>> label) {
		return labelList.equals(label) ? 0.0 : 1.0;
	}
	
	public List<Triplet<Sentence, MR, Trace>> getInstructions() {
		return instructions;
	}
	
	@Override
	public List<Pair<MR, Trace>> getLabel() {
		return labelList;
	}
	
	@Override
	public Pair<List<Sentence>, Task> getSample() {
		return samplePair;
	}
	
	public Task getTask() {
		return task;
	}
	
	@Override
	public boolean isCorrect(List<Pair<MR, Trace>> label) {
		return labelList.equals(label);
	}
	
	@Override
	public Iterator<LabeledInstructionTrace<MR>> iterator() {
		return singleTraces.iterator();
	}
	
	@Override
	public boolean prune(List<Pair<MR, Trace>> y) {
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
		for (final Triplet<Sentence, MR, Trace> instruction : instructions) {
			sb.append('\n');
			sb.append(instruction.first()).append('\n');
			sb.append(instruction.second()).append(" --> ")
					.append(instruction.third());
		}
		return sb.toString();
	}
	
}

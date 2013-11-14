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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

/**
 * Sequence of consecutive instructions, each paired with a demonstration.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class InstructionSeqTrace implements Iterable<InstructionTrace>,
		ILabeledDataItem<InstructionSeq, List<Trace>> {
	
	private static final String					MAP_NAME_KEY	= "map";
	
	private final List<Pair<Sentence, Trace>>	instructions;
	
	private InstructionSeq						instructionSeq;
	
	private final List<Trace>					label;
	private final List<InstructionTrace>		singleTraces;
	
	private final Task							task;
	
	public InstructionSeqTrace(List<Pair<Sentence, Trace>> instructions,
			final Task task) {
		this.instructionSeq = new InstructionSeq(ListUtils.map(instructions,
				new ListUtils.Mapper<Pair<Sentence, Trace>, Sentence>() {
					
					@Override
					public Sentence process(Pair<Sentence, Trace> obj) {
						return obj.first();
					}
				}), task);
		this.instructions = Collections.unmodifiableList(instructions);
		this.task = task;
		this.label = Collections.unmodifiableList(ListUtils.map(instructions,
				new ListUtils.Mapper<Pair<Sentence, Trace>, Trace>() {
					
					@Override
					public Trace process(Pair<Sentence, Trace> obj) {
						return obj.second();
					}
				}));
		this.singleTraces = Collections
				.unmodifiableList(ListUtils
						.map(instructions,
								new ListUtils.Mapper<Pair<Sentence, Trace>, InstructionTrace>() {
									
									@Override
									public InstructionTrace process(
											Pair<Sentence, Trace> obj) {
										return new InstructionTrace(
												obj.first(),
												task.updateAgent(new Agent(obj
														.second()
														.getStartPosition())),
												obj.second());
									}
									
								}));
		
	}
	
	public static <MR> InstructionSeqTrace parse(String string,
			Map<String, NavigationMap> maps) {
		final LinkedList<String> lines = new LinkedList<String>(
				Arrays.asList(string.split("\n")));
		final String id = lines.pollFirst();
		final Map<String, String> properties = InstructionTrace
				.parseProperties(lines.pollFirst());
		properties.put(Task.ID_KEY, id);
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
		
		final Task task = new Task(new Agent(startPosition), new PositionSet(
				map.get(Integer.valueOf(properties.get("y")))
						.getAllOrientations(), false), new PositionSet(map.get(
				Integer.valueOf(properties.get("x"))).getAllOrientations(),
				false), properties, map);
		return new InstructionSeqTrace(instructions, task);
	}
	
	@Override
	public double calculateLoss(List<Trace> y) {
		return label.equals(y) ? 0.0 : 1.0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final InstructionSeqTrace other = (InstructionSeqTrace) obj;
		if (instructions == null) {
			if (other.instructions != null) {
				return false;
			}
		} else if (!instructions.equals(other.instructions)) {
			return false;
		}
		if (task == null) {
			if (other.task != null) {
				return false;
			}
		} else if (!task.equals(other.task)) {
			return false;
		}
		return true;
	}
	
	public String getId() {
		return task.getProperty(Task.ID_KEY);
	}
	
	@Override
	public List<Trace> getLabel() {
		return label;
	}
	
	@Override
	public InstructionSeq getSample() {
		return instructionSeq;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instructions == null) ? 0 : instructions.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}
	
	@Override
	public boolean isCorrect(List<Trace> y) {
		return label.equals(y);
	}
	
	@Override
	public Iterator<InstructionTrace> iterator() {
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
		sb.append(task.getProperty(Task.ID_KEY)).append('\n');
		sb.append(task.propertiesToString());
		for (final Pair<Sentence, Trace> instruction : instructions) {
			sb.append('\n');
			sb.append(instruction.first()).append('\n');
			sb.append(instruction.second());
		}
		return sb.toString();
	}
	
}

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

import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.lexicalgen.ILexGenLabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.genlex.ccg.ILexiconGenerator;
import edu.uw.cs.utils.composites.Pair;

/**
 * A single sentence instruction paired with its demonstration.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class InstructionTrace<MR> implements
		ILexGenLabeledDataItem<Pair<Sentence, Task>, MR, Trace> {
	
	private static final String												MAP_NAME_KEY	= "map";
	
	private final ILexiconGenerator<IDataItem<Pair<Sentence, Task>>, MR>	lexiconGenerator;
	
	private final Pair<Sentence, Task>										pair;
	
	private final Sentence													sentence;
	
	private final Task														task;
	
	private final Trace														trace;
	
	public InstructionTrace(
			Sentence sentence,
			Task task,
			Trace trace,
			ILexiconGenerator<IDataItem<Pair<Sentence, Task>>, MR> lexiconGenerator) {
		this.sentence = sentence;
		this.task = task;
		this.trace = trace;
		this.lexiconGenerator = lexiconGenerator;
		this.pair = Pair.of(sentence, task);
	}
	
	public static <MR> InstructionTrace<MR> parse(
			String string,
			Map<String, NavigationMap> maps,
			ILexiconGenerator<IDataItem<Pair<Sentence, Task>>, MR> lexiconGenerator) {
		final String[] split = string.split("\n");
		final Map<String, String> properties = parseProperties(split[1]);
		final NavigationMap map = maps.get(properties.get(MAP_NAME_KEY));
		final Trace trace = Trace.parseLine(split[2], map);
		final Task task = new Task(new Agent(trace.getStartPosition()),
				new PositionSet(map.get(Integer.valueOf(properties.get("y")))
						.getAllOrientations(), false), new PositionSet(map.get(
						Integer.valueOf(properties.get("x")))
						.getAllOrientations(), false), properties, map);
		return new InstructionTrace<MR>(new Sentence(split[0]), task, trace,
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
	public double calculateLoss(Trace label) {
		return trace.equals(label) ? 0.0 : 1.0;
	}
	
	@Override
	public ILexicon<MR> generateLexicon() {
		return lexiconGenerator.generate(this);
	}
	
	@Override
	public Trace getLabel() {
		return trace;
	}
	
	@Override
	public Pair<Sentence, Task> getSample() {
		return pair;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public Trace getTrace() {
		return trace;
	}
	
	@Override
	public boolean isCorrect(Trace label) {
		return trace.equals(label);
	}
	
	@Override
	public boolean prune(Trace y) {
		return !isCorrect(y);
	}
	
	@Override
	public double quality() {
		return 1.0;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(sentence).append('\n').append(task)
				.append('\n').append(trace).toString();
	}
	
}

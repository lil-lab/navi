package edu.uw.cs.lil.navi.data;

import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.lexicalgen.ILexicalGenerationLabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;
import edu.uw.cs.utils.composites.Pair;

public class LabeledInstructionTrace<Y>
		implements
		ILexicalGenerationLabeledDataItem<Pair<Sentence, Task>, Y, Pair<Y, Trace>> {
	
	private static final Object													MAP_NAME_KEY	= "map";
	
	private final Pair<Y, Trace>												labelPair;
	
	private final ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y>	lexiconGenerator;
	private final Pair<Sentence, Task>											pair;
	private final Y																semantics;
	
	private final Sentence														sentence;
	
	private final Task															task;
	
	private final Trace															trace;
	
	public LabeledInstructionTrace(
			Y semantics,
			Sentence sentence,
			Task task,
			Trace trace,
			ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator) {
		this.semantics = semantics;
		this.sentence = sentence;
		this.task = task;
		this.trace = trace;
		this.lexiconGenerator = lexiconGenerator;
		this.pair = Pair.of(sentence, task);
		this.labelPair = Pair.of(semantics, trace);
	}
	
	public static <Y> LabeledInstructionTrace<Y> parse(
			String string,
			Map<String, NavigationMap> maps,
			ICategoryServices<Y> categoryServices,
			ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator) {
		final String[] split = string.split("\n");
		final Map<String, String> properties = parseProperties(split[2]);
		final NavigationMap map = maps.get(properties.get(MAP_NAME_KEY));
		final Trace trace = Trace.parseLine(split[3], map);
		final Task task = new Task(new Agent(trace.getStartPosition()),
				new PositionSet(map.get(Integer.valueOf(properties.get("y")))
						.getAllOrientations(), false), new PositionSet(map.get(
						Integer.valueOf(properties.get("x")))
						.getAllOrientations(), false), properties, map);
		return new LabeledInstructionTrace<Y>(
				categoryServices.parseSemantics(split[1]), new Sentence(
						split[0]), task, trace, lexiconGenerator);
	}
	
	private static Map<String, String> parseProperties(String line) {
		final String[] split = line.split("\\s+");
		final Map<String, String> properties = new HashMap<String, String>();
		for (final String entry : split) {
			final String[] entrySplit = entry.split("=", 2);
			properties.put(entrySplit[0], entrySplit[1]);
		}
		return properties;
	}
	
	@Override
	public double calculateLoss(Pair<Y, Trace> label) {
		return labelPair.equals(label) ? 0.0 : 1.0;
	}
	
	@Override
	public ILexicon<Y> generateLexicon() {
		return lexiconGenerator
				.generate(new JointDataItemWrapper<Sentence, Task>(sentence,
						this));
	}
	
	@Override
	public Pair<Y, Trace> getLabel() {
		return labelPair;
	}
	
	@Override
	public Pair<Sentence, Task> getSample() {
		return pair;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public Task getTask() {
		return task;
	}
	
	public Trace getTrace() {
		return trace;
	}
	
	@Override
	public boolean isCorrect(Pair<Y, Trace> label) {
		return labelPair.equals(label);
	}
	
	@Override
	public boolean prune(Pair<Y, Trace> y) {
		return !isCorrect(y);
	}
	
	@Override
	public double quality() {
		return 1.0;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(sentence).append('\n')
				.append(semantics).append('\n').append(task).append('\n')
				.append(trace).toString();
	}
	
}

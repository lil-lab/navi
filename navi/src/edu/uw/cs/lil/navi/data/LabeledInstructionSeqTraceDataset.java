package edu.uw.cs.lil.navi.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.io.FileUtils;

public class LabeledInstructionSeqTraceDataset<Y> implements
		IDataCollection<LabeledInstructionSeqTrace<Y>> {
	private final List<LabeledInstructionSeqTrace<Y>>	items;
	
	public LabeledInstructionSeqTraceDataset(
			List<LabeledInstructionSeqTrace<Y>> items) {
		this.items = items;
	}
	
	public static <Y> LabeledInstructionSeqTraceDataset<Y> readFromFile(
			File f,
			final Map<String, NavigationMap> maps,
			final ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator,
			final ICategoryServices<Y> categoryServices) throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new LabeledInstructionSeqTraceDataset<Y>(
				Collections.unmodifiableList(Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.replaceAll("//.*\n", "")
								.split("\n\n")),
						new ListUtils.Mapper<String, LabeledInstructionSeqTrace<Y>>() {
							private int	counter	= 0;
							
							@Override
							public LabeledInstructionSeqTrace<Y> process(
									String obj) {
								counter++;
								try {
									return LabeledInstructionSeqTrace.parse(
											obj, maps, lexiconGenerator,
											categoryServices);
								} catch (final Exception e) {
									throw new InstructionTraceDatasetException(e,
											obj, counter);
								}
							}
						}))));
		
	}
	
	@Override
	public Iterator<LabeledInstructionSeqTrace<Y>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<LabeledInstructionSeqTrace<Y>> iterator = items
				.iterator();
		final StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
			if (iterator.hasNext()) {
				sb.append("\n\n");
			}
		}
		return sb.toString();
	}
	
}

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

public class LabeledInstructionTraceDataset<Y> implements
		IDataCollection<LabeledInstructionTrace<Y>> {
	
	private final List<LabeledInstructionTrace<Y>>	items;
	
	public LabeledInstructionTraceDataset(List<LabeledInstructionTrace<Y>> items) {
		this.items = items;
	}
	
	public static <Y> LabeledInstructionTraceDataset<Y> readFromFile(
			File f,
			final Map<String, NavigationMap> maps,
			final ICategoryServices<Y> categoryServices,
			final ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator)
			throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new LabeledInstructionTraceDataset<Y>(
				Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.split("\n\n")),
						new ListUtils.Mapper<String, LabeledInstructionTrace<Y>>() {
							private int	counter	= 0;
							
							@Override
							public LabeledInstructionTrace<Y> process(String obj) {
								counter++;
								try {
									return LabeledInstructionTrace.parse(obj, maps,
											categoryServices, lexiconGenerator);
								} catch (final Exception e) {
									throw new LabeledInstructionTraceDatasetException(
											e, obj, counter);
								}
							}
						})));
		
	}
	
	@Override
	public Iterator<LabeledInstructionTrace<Y>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<LabeledInstructionTrace<Y>> iterator = items.iterator();
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

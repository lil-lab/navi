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
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.io.FileUtils;

public class InstructionSeqTraceDataset<Y> implements
		IDataCollection<InstructionSeqTrace<Y>> {
	private final List<InstructionSeqTrace<Y>>	items;
	
	public InstructionSeqTraceDataset(List<InstructionSeqTrace<Y>> items) {
		this.items = items;
	}
	
	public static <Y> InstructionSeqTraceDataset<Y> readFromFile(
			File f,
			final Map<String, NavigationMap> maps,
			final ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y> lexiconGenerator)
			throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new InstructionSeqTraceDataset<Y>(
				Collections.unmodifiableList(Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.split("\n\n")),
						new ListUtils.Mapper<String, InstructionSeqTrace<Y>>() {
							private int	counter	= 0;
							
							@Override
							public InstructionSeqTrace<Y> process(String obj) {
								counter++;
								try {
									return InstructionSeqTrace.parse(obj, maps,
											lexiconGenerator);
								} catch (final Exception e) {
									throw new InstructionTraceDatasetException(e,
											obj, counter);
								}
							}
						}))));
		
	}
	
	@Override
	public Iterator<InstructionSeqTrace<Y>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<InstructionSeqTrace<Y>> iterator = items.iterator();
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

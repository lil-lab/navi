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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.genlex.ccg.ILexiconGenerator;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.io.FileUtils;

/**
 * Data set of {@link InstructionSeqTrace}.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class InstructionSeqTraceDataset<MR> implements
		IDataCollection<InstructionSeqTrace<MR>> {
	private final List<InstructionSeqTrace<MR>>	items;
	
	public InstructionSeqTraceDataset(List<InstructionSeqTrace<MR>> items) {
		this.items = items;
	}
	
	public static <MR> InstructionSeqTraceDataset<MR> readFromFile(
			File f,
			final Map<String, NavigationMap> maps,
			final ILexiconGenerator<IDataItem<Pair<Sentence, Task>>, MR> lexiconGenerator)
			throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new InstructionSeqTraceDataset<MR>(
				Collections.unmodifiableList(Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.split("\n\n")),
						new ListUtils.Mapper<String, InstructionSeqTrace<MR>>() {
							private int	counter	= 0;
							
							@Override
							public InstructionSeqTrace<MR> process(String obj) {
								counter++;
								try {
									return InstructionSeqTrace.parse(obj, maps,
											lexiconGenerator);
								} catch (final Exception e) {
									throw new InstructionTraceDatasetException(
											e, obj, counter);
								}
							}
						}))));
		
	}
	
	@Override
	public Iterator<InstructionSeqTrace<MR>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<InstructionSeqTrace<MR>> iterator = items.iterator();
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

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
package edu.uw.cs.lil.navi.parse;

import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.exact.ExactMarkAwareCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.graph.AbstractGraphParser;
import edu.uw.cs.lil.tiny.parser.graph.IGraphParserOutput;
import edu.uw.cs.utils.filter.IFilter;

public class WrappedCKYParser extends
		AbstractGraphParser<Sentence, LogicalExpression> {
	
	private final AbstractCKYParser<LogicalExpression>	ckyParser;
	
	public WrappedCKYParser(AbstractCKYParser<LogicalExpression> ckyParser) {
		this.ckyParser = ckyParser;
	}
	
	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			IDataItem<Sentence> dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return ckyParser.parse(dataItem, pruningFilter, model,
				allowWordSkipping, tempLexicon, beamSize,
				new ExactMarkAwareCellFactory<LogicalExpression>(dataItem
						.getSample().getTokens().size()));
	}
	
}

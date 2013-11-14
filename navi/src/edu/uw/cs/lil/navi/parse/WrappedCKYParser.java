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
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.exact.ExactMarkAwareCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.graph.AbstractGraphParser;
import edu.uw.cs.lil.tiny.parser.graph.IGraphParserOutput;
import edu.uw.cs.utils.filter.IFilter;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class WrappedCKYParser extends
		AbstractGraphParser<Sentence, LogicalExpression> {
	public static final ILogger							LOG	= LoggerFactory
																	.create(WrappedCKYParser.class);
	private final AbstractCKYParser<LogicalExpression>	ckyParser;
	
	public WrappedCKYParser(AbstractCKYParser<LogicalExpression> ckyParser) {
		this.ckyParser = ckyParser;
	}
	
	@Override
	public IGraphParserOutput<LogicalExpression> parse(Sentence dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		LOG.info("CKY parsing with marked cells.");
		return ckyParser.parse(dataItem, pruningFilter, model,
				allowWordSkipping, tempLexicon, beamSize,
				new ExactMarkAwareCellFactory<LogicalExpression>(dataItem
						.getSample().getTokens().size()));
	}
	
	public static class Creator implements
			IResourceObjectCreator<WrappedCKYParser> {
		
		private final String	type;
		
		public Creator() {
			this("parser.cky.mark");
		}
		
		public Creator(String type) {
			this.type = type;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public WrappedCKYParser create(Parameters params,
				IResourceRepository repo) {
			return new WrappedCKYParser(
					(AbstractCKYParser<LogicalExpression>) repo
							.getResource(params.get("parser")));
		}
		
		@Override
		public String type() {
			return type;
		}
		
		@Override
		public ResourceUsage usage() {
			return ResourceUsage
					.builder(type, WrappedCKYParser.class)
					.addParam("parser", AbstractCKYParser.class,
							"CKY parser to wrap.").build();
		}
		
	}
	
}

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
package edu.uw.cs.lil.navi.features.init;

import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.FactoredLexicon.FactoredLexicalEntry;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Init lexical template feature weights given the number of times these
 * features are used in a given lexicon.
 * 
 * @author Yoav Artzi
 */
public class TemplateCountModelInit implements
		IModelInit<Sentence, LogicalExpression> {
	
	private final String						lexicalTemplateFeatureTag;
	private final ILexicon<LogicalExpression>	lexicon;
	
	public TemplateCountModelInit(ILexicon<LogicalExpression> lexicon,
			String lexicalTemplateFeatureTag) {
		this.lexicon = lexicon;
		this.lexicalTemplateFeatureTag = lexicalTemplateFeatureTag;
	}
	
	@Override
	public void init(Model<Sentence, LogicalExpression> model) {
		final IHashVector theta = model.getTheta();
		
		final IHashVector templateFeatureCounts = HashVectorFactory.create();
		
		// Iterate over the lexical entries
		for (final LexicalEntry<LogicalExpression> entry : lexicon
				.toCollection()) {
			
			// Only handle factored entries, should only get such
			if (entry instanceof FactoredLexicalEntry) {
				// Compute the features of the entry
				final IHashVector features = model.computeFeatures(entry);
				// Extract all lexical template features from the features over
				// the entry
				final IHashVector feats = features
						.getAll(lexicalTemplateFeatureTag);
				// Increase the counters
				feats.addTimesInto(1.0, templateFeatureCounts);
			} else {
				throw new IllegalStateException("expected a factored entry");
			}
		}
		
		// Add the features into theta
		templateFeatureCounts.addTimesInto(1.0, theta);
	}
	
}

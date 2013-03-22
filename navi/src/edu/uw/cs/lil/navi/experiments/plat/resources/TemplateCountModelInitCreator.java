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
package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.features.init.TemplateCountModelInit;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

public class TemplateCountModelInitCreator implements
		IResourceObjectCreator<TemplateCountModelInit> {
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateCountModelInit create(Parameters params,
			IResourceRepository repo) {
		return new TemplateCountModelInit(
				(ILexicon<LogicalExpression>) repo.getResource(params
						.get("lexicon")), params.get("tag"));
	}
	
	@Override
	public String type() {
		return "init.feats.templates";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), TemplateCountModelInit.class)
				.setDescription(
						"Model initilizer that initalizes lexical template features by computing frequence stats on a seed lexicon")
				.addParam("lexicon", "id",
						"Lexicon to compute frequency stats from")
				.addParam("tag", "string", "Lexical template features tag")
				.build();
	}
	
}

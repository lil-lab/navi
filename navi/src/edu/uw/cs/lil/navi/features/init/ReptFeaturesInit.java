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

import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Init conjunction REPT features.
 * 
 * @author Yoav Artzi
 * @param <DI>
 *            Inference data item.
 */
public class ReptFeaturesInit<DI extends IDataItem<?>> implements
		IModelInit<DI, LogicalExpression> {
	
	private final String	featureName;
	
	private final String	featureTag;
	private final double	initWeight;
	private final Ontology	ontology;
	
	public ReptFeaturesInit(String featureTag, String featureName,
			double initWeight, Ontology ontology) {
		this.featureTag = featureTag;
		this.featureName = featureName;
		this.initWeight = initWeight;
		this.ontology = ontology;
	}
	
	@Override
	public void init(Model<DI, LogicalExpression> model) {
		final IHashVector theta = model.getTheta();
		for (final LogicalConstant pred : ontology.getAllPredicates()) {
			theta.set(featureTag, featureName, LogicLanguageServices
					.getConjunctionPredicate().toString(), pred.toString(),
					initWeight);
		}
	}
	
	public static class Creator<DI extends IDataItem<?>> implements
			IResourceObjectCreator<ReptFeaturesInit<DI>> {
		
		@Override
		public ReptFeaturesInit<DI> create(Parameters params,
				IResourceRepository repo) {
			return new ReptFeaturesInit<DI>(params.get("tag"),
					params.get("name"), Double.valueOf(params.get("weight")),
					(Ontology) repo
							.getResource(NaviExperiment.ONTOLOGY_RESOURCE));
		}
		
		@Override
		public String type() {
			return "init.feats.rept";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(), ReptFeaturesInit.class)
					.setDescription(
							"Intializer for logical expression repetition features")
					.addParam("tag", "string", "Repetition features tag")
					.addParam("name", "string",
							"Repetition features second identifier")
					.addParam("weight", "double",
							"Initial weight to give to each initialized feature")
					.build();
		}
		
	}
	
}

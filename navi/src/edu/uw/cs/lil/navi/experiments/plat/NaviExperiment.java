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
package edu.uw.cs.lil.navi.experiments.plat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants.StateFlag;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.eval.literalevaluators.WrappedGenericEvaluator;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionDirection;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionLength;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionPass;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionPost;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionPrePosition;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionPreState;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionTo;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionType;
import edu.uw.cs.lil.navi.eval.literalevaluators.actions.ActionWhile;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions.PositionSetAgentDistance;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions.PositionSetFrontDistance;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions.PositionSetOrder;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions.PositionSetOrient;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.functions.PositionSetType;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations.PositionSetDistance;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations.PositionSetEnd;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations.PositionSetFront;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations.PositionSetIntersect;
import edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations.PositionSetMiddle;
import edu.uw.cs.lil.navi.eval.literalevaluators.quantifiers.DefiniteArticle;
import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.lil.navi.map.objects.NaviObj;
import edu.uw.cs.lil.navi.map.objects.NaviWall;
import edu.uw.cs.lil.navi.map.objects.metaitems.NaviMetaItem;
import edu.uw.cs.lil.navi.test.stats.FinalCoordinatesTestStatistics;
import edu.uw.cs.lil.navi.test.stats.FinalPositionTestStatistics;
import edu.uw.cs.lil.navi.test.stats.LogicalFormSentenceTestStatistics;
import edu.uw.cs.lil.navi.test.stats.LogicalFormTestStatistics;
import edu.uw.cs.lil.navi.test.stats.TraceTestStatistics;
import edu.uw.cs.lil.navi.test.stats.set.SetFinalCoordinatesTestStatistics;
import edu.uw.cs.lil.navi.test.stats.set.SetGoalCoordinatesTestStatistics;
import edu.uw.cs.lil.navi.test.stats.set.SetLogicalFormTestStatistics;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.ccg.lexicon.LexicalEntry.Origin;
import edu.uw.cs.lil.tiny.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.FactoredLexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.FactoredLexicon.FactoredLexicalEntry;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.FactoredLexiconServices;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.explat.DistributedExperiment;
import edu.uw.cs.lil.tiny.explat.Job;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.learn.situated.AbstractSituatedLearner;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.ArgMax;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.ArgMin;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Equals;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Exists;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Not;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.ModelLogger;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.storage.DecoderHelper;
import edu.uw.cs.lil.tiny.test.exec.ExecTester;
import edu.uw.cs.lil.tiny.test.stats.CompositeTestingStatistics;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.lil.tiny.test.stats.SimpleStats;
import edu.uw.cs.lil.tiny.test.stats.StatsWithDuplicates;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory.Type;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LogLevel;
import edu.uw.cs.utils.log.Logger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Distributed experiment for the NAVI navigation project.
 * 
 * @author Yoav Artzi
 */
public class NaviExperiment extends DistributedExperiment {
	
	public static final String						EVAL_SERVICES_FACTORY	= "evalServicesFactory";
	
	public static final String						SINGLE_EVALUATOR		= "singleEval";
	
	private static final ILogger					LOG						= LoggerFactory
																					.create(NaviExperiment.class);
	
	private final LogicalExpressionCategoryServices	categoryServices;
	
	private final DecoderHelper<LogicalExpression>	decoderHelper;
	
	public NaviExperiment(File initFile) throws IOException, SAXException {
		this(initFile, Collections.<String, String> emptyMap(),
				new NaviResourceCreatorRepository());
	}
	
	public NaviExperiment(File initFile, Map<String, String> envParams,
			ResourceCreatorRepository resCreatorRepo) throws IOException,
			SAXException {
		super(initFile, envParams);
		
		LogLevel.DEV.set();
		Logger.setSkipPrefix(true);
		
		// //////////////////////////////////////////
		// Get parameters
		// //////////////////////////////////////////
		final File typesFile = globalParams.getAsFile("types");
		final List<File> seedLexiconFiles = globalParams.getAsFiles("seedlex");
		final int maxImplicitActions = Integer.parseInt(globalParams
				.get("implicit"));
		
		// //////////////////////////////////////////
		// Executor resource
		// //////////////////////////////////////////
		
		storeResource(EXECUTOR_RESOURCE, this);
		
		// //////////////////////////////////////////
		// Use tree hash vector
		// //////////////////////////////////////////
		
		HashVectorFactory.DEFAULT = Type.TREE;
		
		// //////////////////////////////////////////
		// Init typing system
		// //////////////////////////////////////////
		
		// Init the logical expression type system
		LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(
				new TypeRepository(typesFile)).setNumeralTypeName("n")
				.setTypeComparator(new FlexibleTypeComparator()).build());
		
		// //////////////////////////////////////////////////
		// Category services for logical expressions
		// //////////////////////////////////////////////////
		
		this.categoryServices = new LogicalExpressionCategoryServices(true,
				true);
		storeResource(CATEGORY_SERVICES_RESOURCE, categoryServices);
		
		// //////////////////////////////////////////////////
		// Decoder helper for decoding tasks
		// //////////////////////////////////////////////////
		
		this.decoderHelper = new DecoderHelper<LogicalExpression>(
				categoryServices);
		storeResource(DECODER_HELPER_RESOURCE, decoderHelper);
		
		// //////////////////////////////////////////////////
		// Read ontology (loads all constants)
		// //////////////////////////////////////////////////
		
		try {
			final List<File> ontologyFiles = new LinkedList<File>();
			final File domainOntologyFile = globalParams
					.getAsFile("domain_ont");
			ontologyFiles.add(domainOntologyFile);
			ontologyFiles.add(globalParams.getAsFile("generic_ont"));
			storeResource(DOMAIN_ONTOLOGY_RESOURCE, new Ontology(
					domainOntologyFile));
			storeResource(ONTOLOGY_RESOURCE, new Ontology(ontologyFiles));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
		// //////////////////////////////////////////////////
		// Lexical factoring services
		// //////////////////////////////////////////////////
		
		final Set<LogicalConstant> unfactoredConstants = new HashSet<LogicalConstant>();
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("io:<<e,t>,e>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("a:<<e,t>,e>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("exists:<<e,t>,t>"));
		unfactoredConstants.add((LogicalConstant) categoryServices
				.parseSemantics("eq:<e,<e,t>>"));
		FactoredLexiconServices.set(unfactoredConstants);
		
		// //////////////////////////////////////////////////
		// Initial lexicon
		// //////////////////////////////////////////////////
		
		// Create a static set of lexical entries, which are factored using
		// non-maximal factoring (each lexical entry is factored to multiple
		// entries). This static set is used to init the model with various
		// templates and lexemes.
		
		final Lexicon<LogicalExpression> readLexicon = new Lexicon<LogicalExpression>();
		for (final File file : seedLexiconFiles) {
			readLexicon.addEntriesFromFile(file, categoryServices,
					Origin.FIXED_DOMAIN);
		}
		
		final Lexicon<LogicalExpression> semiFactored = new Lexicon<LogicalExpression>();
		for (final LexicalEntry<LogicalExpression> entry : readLexicon
				.toCollection()) {
			for (final FactoredLexicalEntry factoredEntry : FactoredLexicon
					.factor(entry, true, true, 2)) {
				semiFactored.add(FactoredLexicon.factor(factoredEntry));
			}
		}
		storeResource("initialLexicon", semiFactored);
		
		// //////////////////////////////////////////////////
		// Navi Evaluation objects
		// //////////////////////////////////////////////////
		
		final NaviSingleEvaluator singleEvaluator = createEvaluationProcedure(
				categoryServices, maxImplicitActions);
		
		storeResource(EVAL_SERVICES_FACTORY,
				singleEvaluator.getServicesFactory());
		storeResource(SINGLE_EVALUATOR, singleEvaluator);
		
		// //////////////////////////////////////////////////
		// Read resources
		// //////////////////////////////////////////////////
		
		for (final Parameters params : resourceParams) {
			final String type = params.get("type");
			final String id = params.get("id");
			if (resCreatorRepo.getCreator(type) == null) {
				throw new IllegalArgumentException("Invalid resource type: "
						+ type);
			} else {
				storeResource(id,
						resCreatorRepo.getCreator(type).create(params, this));
			}
		}
		
		// //////////////////////////////////////////////////
		// Create jobs
		// //////////////////////////////////////////////////
		
		for (final Parameters params : jobParams) {
			addJob(createJob(params));
		}
	}
	
	private static NaviSingleEvaluator createEvaluationProcedure(
			ICategoryServices<LogicalExpression> categoryServices,
			int maxImplicitActions) {
		final NaviEvaluationConstants.Builder builder = new NaviEvaluationConstants.Builder(
				LogicLanguageServices.getTypeRepository().getType("a"),
				(LogicalConstant) categoryServices.parseSemantics("x:ps"),
				(LogicalConstant) categoryServices.parseSemantics("y:ps"),
				(LogicalConstant) categoryServices.parseSemantics("you:ps"),
				(LogicalConstant) categoryServices
						.parseSemantics("io:<<e,t>,e>"),
				(LogicalConstant) categoryServices
						.parseSemantics("exists:<<e,t>,t>"),
				(LogicalConstant) categoryServices
						.parseSemantics("a:<<e,t>,e>"),
				(LogicalConstant) categoryServices
						.parseSemantics("exists:<<a,t>,t>"),
				maxImplicitActions, categoryServices, LogicLanguageServices
						.getTypeRepository().getType("m"));
		
		// Equals predicates
		builder.addEquals(LogicLanguageServices.getTypeRepository()
				.getType("e"), (LogicalConstant) categoryServices
				.parseSemantics("eq:<e,<e,t>>"));
		builder.addEquals(LogicLanguageServices.getTypeRepository()
				.getType("a"), (LogicalConstant) categoryServices
				.parseSemantics("eq:<a,<a,t>>"));
		
		// Numbers
		for (int i = 0; i < 10; ++i) {
			builder.addNumber(i);
		}
		
		// Directions
		builder.addDirection(categoryServices.parseSemantics("left:dir"),
				edu.uw.cs.lil.navi.agent.Direction.LEFT);
		builder.addDirection(categoryServices.parseSemantics("right:dir"),
				edu.uw.cs.lil.navi.agent.Direction.RIGHT);
		builder.addDirection(categoryServices.parseSemantics("back:dir"),
				edu.uw.cs.lil.navi.agent.Direction.BACK);
		builder.addDirection(categoryServices.parseSemantics("forward:dir"),
				edu.uw.cs.lil.navi.agent.Direction.FORWARD);
		
		// Literal evaluators
		
		// Quantifiers
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("io:<<e,t>,e>"), new DefiniteArticle());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("exists:<<e,t>,t>"),
				new WrappedGenericEvaluator(new Exists()));
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("exists:<<a,t>,t>"),
				new WrappedGenericEvaluator(new Exists()));
		
		// Equals
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("eq:<e,<e,t>>"), new WrappedGenericEvaluator(
				new Equals()));
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("eq:<a,<a,t>>"), new WrappedGenericEvaluator(
				new Equals()));
		
		// Not
		builder.addEvaluator(
				(LogicalConstant) categoryServices.parseSemantics("not:<t,t>"),
				new WrappedGenericEvaluator(new Not()));
		
		// Argmax
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("argmax:<<e,t>,<<e,n>,e>>"),
				new WrappedGenericEvaluator(new ArgMax()));
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("argmin:<<e,t>,<<e,n>,e>>"),
				new WrappedGenericEvaluator(new ArgMin()));
		
		// Actions
		
		// Action types
		builder.addEvaluator(
				(LogicalConstant) categoryServices.parseSemantics("move:<a,t>"),
				new ActionType(AgentAction.FORWARD));
		builder.addEvaluator(
				(LogicalConstant) categoryServices.parseSemantics("turn:<a,t>"),
				new ActionType(AgentAction.LEFT, AgentAction.RIGHT));
		
		// Action properties
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("dir:<a,<dir,t>>"), new ActionDirection());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("len:<a,<n,t>>"), new ActionLength());
		
		// Action positions
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("pass:<a,<ps,t>>"), new ActionPass());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("to:<a,<ps,t>>"), new ActionTo());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("while:<a,<ps,t>>"), new ActionWhile());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("post:<a,<t,t>>"), new ActionPost());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("pre:<a,<ps,t>>"), new ActionPrePosition());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("pre:<a,<t,t>>"), new ActionPreState());
		
		// Positions
		
		// Position type
		builder.addEvaluator(
				(LogicalConstant) categoryServices
						.parseSemantics("chair:<ps,t>"),
				new PositionSetType(NaviObj.CHAIR))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("easel:<ps,t>"),
						new PositionSetType(NaviObj.EASEL))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("sofa:<ps,t>"),
						new PositionSetType(NaviObj.SOFA))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("hatrack:<ps,t>"),
						new PositionSetType(NaviObj.HATRACK))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("empty:<ps,t>"),
						new PositionSetType(NaviObj.EMPTY))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("lamp:<ps,t>"),
						new PositionSetType(NaviObj.LAMP))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("barstool:<ps,t>"),
						new PositionSetType(NaviObj.BARSTOOL))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("furniture:<ps,t>"),
						new PositionSetType(NaviObj.FURNITURE))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("rose:<ps,t>"),
						new PositionSetType(NaviHall.ROSE))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("wood:<ps,t>"),
						new PositionSetType(NaviHall.WOOD))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("blue:<ps,t>"),
						new PositionSetType(NaviHall.BLUE))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("stone:<ps,t>"),
						new PositionSetType(NaviHall.STONE))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("brick:<ps,t>"),
						new PositionSetType(NaviHall.BRICK))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("grass:<ps,t>"),
						new PositionSetType(NaviHall.GRASS))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("honeycomb:<ps,t>"),
						new PositionSetType(NaviHall.HONEYCOMB))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("cement:<ps,t>"),
						new PositionSetType(NaviHall.CEMENT))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("butterfly_w:<ps,t>"),
						new PositionSetType(NaviWall.BUTTERFLY))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("eiffel_w:<ps,t>"),
						new PositionSetType(NaviWall.EIFFEL))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("fish_w:<ps,t>"),
						new PositionSetType(NaviWall.FISH))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("wall:<ps,t>"),
						new PositionSetType(NaviHall.WALL))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("corner:<ps,t>"),
						new PositionSetType(NaviMetaItem.CORNER))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("deadend:<ps,t>"),
						new PositionSetType(NaviMetaItem.DEADEND))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("intersection:<ps,t>"),
						new PositionSetType(NaviMetaItem.INTERSECTION))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("t_intersection:<ps,t>"),
						new PositionSetType(NaviMetaItem.T_INTERSECTION))
				.addEvaluator(
						(LogicalConstant) categoryServices
								.parseSemantics("hall:<ps,t>"),
						new PositionSetType(NaviHall.HALL));
		
		// Position functions
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("orient:<ps,<dir,ps>>"),
				new PositionSetOrient());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("frontdist:<ps,n>"),
				new PositionSetFrontDistance());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("dist:<ps,n>"), new PositionSetAgentDistance());
		
		// Position relations
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("intersect:<ps,<ps,t>>"),
				new PositionSetIntersect());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("middle:<ps,<ps,t>>"), new PositionSetMiddle());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("order:<<ps,t>,<<ps,n>,<n,ps>>>"),
				new PositionSetOrder());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("distance:<ps,<ps,<n,t>>>"),
				new PositionSetDistance());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("front:<ps,<ps,t>>"), new PositionSetFront());
		builder.addEvaluator((LogicalConstant) categoryServices
				.parseSemantics("end:<ps,<ps,t>>"), new PositionSetEnd());
		
		// Stateful predicates
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("post:<a,<t,t>>"),
				StateFlag.POST);
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("pass:<a,<ps,t>>"),
				StateFlag.POST);
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("pre:<a,<t,t>>"), StateFlag.PRE);
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("pre:<a,<ps,t>>"),
				StateFlag.PRE);
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("while:<a,<ps,t>>"),
				StateFlag.PRE);
		builder.addStatefulPredicate(
				categoryServices.parseSemantics("to:<a,<ps,t>>"),
				StateFlag.POST);
		
		final NaviEvaluationServicesFactory servicesFactory = new NaviEvaluationServicesFactory(
				builder.build());
		
		return new NaviSingleEvaluator(servicesFactory);
	}
	
	private Job createJob(Parameters params) throws FileNotFoundException {
		final String type = params.get("type");
		if (type.equals("train")) {
			return createTrainJob(params);
		} else if (type.equals("test")) {
			return createTestJob(params);
		} else if (type.equals("log")) {
			return createModelLoggingJob(params);
		} else if ("init".equals(type)) {
			return createModelInitJob(params);
		} else {
			throw new RuntimeException("Unsupported job type: " + type);
		}
	}
	
	private Job createModelInitJob(Parameters params)
			throws FileNotFoundException {
		final Model<Sentence, LogicalExpression> model = getResource(params
				.get("model"));
		final List<IModelInit<Sentence, LogicalExpression>> modelInits = ListUtils
				.map(params.getSplit("init"),
						new ListUtils.Mapper<String, IModelInit<Sentence, LogicalExpression>>() {
							
							@Override
							public IModelInit<Sentence, LogicalExpression> process(
									String obj) {
								return getResource(obj);
							}
						});
		
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputFile(params.get("id")),
				createJobLogFile(params.get("id"))) {
			
			@Override
			protected void doJob() {
				for (final IModelInit<Sentence, LogicalExpression> modelInit : modelInits) {
					modelInit.init(model);
				}
			}
		};
	}
	
	private Job createModelLoggingJob(Parameters params)
			throws FileNotFoundException {
		final IModelImmutable<?, ?> model = getResource(params.get("model"));
		final ModelLogger modelLogger = getResource(params.get("logger"));
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputFile(params.get("id")),
				createJobLogFile(params.get("id"))) {
			
			@Override
			protected void doJob() {
				modelLogger.log(model, getOutputStream());
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private <X, Z> ITestingStatistics<X, Z> createTestingStatistics(
			String metricName, String jobId) {
		final String expId = String.format("expId=%s", jobId);
		final String prefix = String.format("%s:", jobId);
		if ("exact.trace".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new TraceTestStatistics(
					expId,
					prefix + "exact.trace",
					new SimpleStats<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>());
		} else if ("exact.position".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new FinalPositionTestStatistics(
					expId,
					prefix + "exact.position",
					new SimpleStats<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>());
		} else if ("exact.coordinates".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new FinalCoordinatesTestStatistics(
					expId,
					prefix + "exact.coordinates",
					new SimpleStats<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>());
		} else if ("exact.lf".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new LogicalFormTestStatistics(
					expId,
					prefix + "exact.lf",
					new SimpleStats<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>());
		} else if ("exact.lf.dup".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new LogicalFormTestStatistics(
					expId,
					prefix + "exact.lf.dup",
					new StatsWithDuplicates<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>());
		} else if ("exact.lf.dup.nl".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new LogicalFormSentenceTestStatistics(
					expId, prefix + "exact.lf.dup.nl",
					new StatsWithDuplicates<Sentence>());
		} else if ("exact.set.coordinates".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new SetFinalCoordinatesTestStatistics<LogicalExpression>(
					expId,
					prefix + "exact.set.coordinates",
					new SimpleStats<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>());
		} else if ("exact.set.xcoordinates".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new SetGoalCoordinatesTestStatistics<LogicalExpression>(
					expId,
					prefix + "exact.set.xcoordinates",
					new SimpleStats<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>());
		} else if ("exact.set.lf".equals(metricName)) {
			return (ITestingStatistics<X, Z>) new SetLogicalFormTestStatistics<LogicalExpression>(
					expId,
					prefix + "exact.set.lf",
					new SimpleStats<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>());
			
		} else {
			throw new RuntimeException("Unknown testing metric: " + metricName);
		}
	}
	
	private <X, Z> Job createTestJob(Parameters params)
			throws FileNotFoundException {
		// Create test statistics
		final List<ITestingStatistics<X, Z>> testingMetrics = new LinkedList<ITestingStatistics<X, Z>>();
		for (final String metricName : params.getSplit("stats")) {
			final ITestingStatistics<X, Z> stat = createTestingStatistics(
					metricName, params.get("id"));
			testingMetrics.add(stat);
		}
		final ITestingStatistics<X, Z> testStatistics = new CompositeTestingStatistics<X, Z>(
				testingMetrics);
		
		// Get the executor
		final IExec<X, Z> exec = getResource(params.get("exec"));
		
		// Get the tester
		final ExecTester<X, Z> tester = getResource(params.get("tester"));
		
		// Get the data
		final IDataCollection<? extends ILabeledDataItem<X, Z>> data = getResource(params
				.get("data"));
		
		// Create and return the job
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputFile(params.get("id")),
				createJobLogFile(params.get("id"))) {
			
			@Override
			protected void doJob() {
				
				// Record start time
				final long startTime = System.currentTimeMillis();
				
				// Job started
				LOG.info("============ (Job %s started)", getId());
				
				// Test the final model
				tester.test(exec, data, testStatistics);
				LOG.info("%s\n", testStatistics);
				getOutputStream()
						.println(testStatistics.toTabDelimitedString());
				
				// Output total run time
				LOG.info("Total run time %.4f seconds",
						(System.currentTimeMillis() - startTime) / 1000.0);
				
				// Job completed
				LOG.info("============ (Job %s completed)", getId());
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private Job createTrainJob(Parameters params) throws FileNotFoundException {
		
		// The model to use
		final JointModel<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace> model = (JointModel<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace>) getResource(params
				.get("model"));
		
		// The learner
		final AbstractSituatedLearner<Task, LogicalExpression, Trace, Trace, InstructionTrace<LogicalExpression>> learner = (AbstractSituatedLearner<Task, LogicalExpression, Trace, Trace, InstructionTrace<LogicalExpression>>) getResource(params
				.get("learner"));
		
		return new Job(params.get("id"), new HashSet<String>(
				params.getSplit("dep")), this,
				createJobOutputFile(params.get("id")),
				createJobLogFile(params.get("id"))) {
			
			@Override
			protected void doJob() {
				final long startTime = System.currentTimeMillis();
				
				// Start job
				LOG.info("============ (Job %s started)", getId());
				
				// Do the learning
				learner.train(model);
				
				// Log the final model
				LOG.info("Final model:\n%s", model);
				
				// Output total run time
				LOG.info("Total run time %.4f seconds",
						(System.currentTimeMillis() - startTime) / 1000.0);
				
				// Job completed
				LOG.info("============ (Job %s completed)", getId());
				
			}
		};
	}
}

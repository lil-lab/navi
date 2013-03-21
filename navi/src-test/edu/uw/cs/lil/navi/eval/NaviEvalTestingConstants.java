package edu.uw.cs.lil.navi.eval;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
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
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.ArgMax;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.ArgMin;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Equals;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Exists;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators.Not;

public class NaviEvalTestingConstants {
	private static final NaviEvalTestingConstants	INSTANCE	= new NaviEvalTestingConstants();
	
	private final NaviEvaluationServicesFactory		servicesFactory;
	
	public NaviEvalTestingConstants() {
		TestingConstants.CATEGORY_SERVICES.parseSemantics("left:dir");
		
		final NaviEvaluationConstants.Builder builder = new NaviEvaluationConstants.Builder(
				LogicLanguageServices.getTypeRepository().getType("a"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("x:ps"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("y:ps"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("you:ps"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("io:<<e,t>,e>"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("exists:<<e,t>,t>"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("a:<<e,t>,e>"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("exists:<<a,t>,t>"), 2,
				TestingConstants.CATEGORY_SERVICES, LogicLanguageServices
						.getTypeRepository().getType("m"));
		
		// Equals predicates
		builder.addEquals(LogicLanguageServices.getTypeRepository()
				.getType("e"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("eq:<e,<e,t>>"));
		builder.addEquals(LogicLanguageServices.getTypeRepository()
				.getType("a"),
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("eq:<a,<a,t>>"));
		
		// Numbers
		for (int i = 0; i < 10; ++i) {
			builder.addNumber(i);
		}
		
		// Directions
		builder.addDirection(
				TestingConstants.CATEGORY_SERVICES.parseSemantics("left:dir"),
				edu.uw.cs.lil.navi.agent.Direction.LEFT);
		builder.addDirection(
				TestingConstants.CATEGORY_SERVICES.parseSemantics("right:dir"),
				edu.uw.cs.lil.navi.agent.Direction.RIGHT);
		builder.addDirection(
				TestingConstants.CATEGORY_SERVICES.parseSemantics("back:dir"),
				edu.uw.cs.lil.navi.agent.Direction.BACK);
		builder.addDirection(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("forward:dir"),
				edu.uw.cs.lil.navi.agent.Direction.FORWARD);
		
		// Literal evaluators
		
		// Quantifiers
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("io:<<e,t>,e>"), new DefiniteArticle());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("exists:<<e,t>,t>"),
				new WrappedGenericEvaluator(new Exists()));
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("exists:<<a,t>,t>"),
				new WrappedGenericEvaluator(new Exists()));
		
		// Equals
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("eq:<e,<e,t>>"),
				new WrappedGenericEvaluator(new Equals()));
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("eq:<a,<a,t>>"),
				new WrappedGenericEvaluator(new Equals()));
		
		// Not
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("not:<t,t>"),
				new WrappedGenericEvaluator(new Not()));
		
		// Argmax
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("argmax:<<e,t>,<<e,n>,e>>"),
				new WrappedGenericEvaluator(new ArgMax()));
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("argmin:<<e,t>,<<e,n>,e>>"),
				new WrappedGenericEvaluator(new ArgMin()));
		
		// Actions
		
		// Action types
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("move:<a,t>"), new ActionType(
						AgentAction.FORWARD));
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("turn:<a,t>"), new ActionType(
						AgentAction.LEFT, AgentAction.RIGHT));
		
		// Action properties
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("dir:<a,<dir,t>>"),
				new ActionDirection());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("len:<a,<n,t>>"), new ActionLength());
		
		// Action positions
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("pass:<a,<ps,t>>"), new ActionPass());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("to:<a,<ps,t>>"), new ActionTo());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("while:<a,<ps,t>>"), new ActionWhile());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("post:<a,<t,t>>"), new ActionPost());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("pre:<a,<ps,t>>"),
				new ActionPrePosition());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("pre:<a,<t,t>>"), new ActionPreState());
		
		// Positions
		
		// Position type
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("chair:<ps,t>"),
				new PositionSetType(NaviObj.CHAIR))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("easel:<ps,t>"),
						new PositionSetType(NaviObj.EASEL))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("sofa:<ps,t>"),
						new PositionSetType(NaviObj.SOFA))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("hatrack:<ps,t>"),
						new PositionSetType(NaviObj.HATRACK))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("empty:<ps,t>"),
						new PositionSetType(NaviObj.EMPTY))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("lamp:<ps,t>"),
						new PositionSetType(NaviObj.LAMP))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("barstool:<ps,t>"),
						new PositionSetType(NaviObj.BARSTOOL))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("furniture:<ps,t>"),
						new PositionSetType(NaviObj.FURNITURE))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("rose:<ps,t>"),
						new PositionSetType(NaviHall.ROSE))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("wood:<ps,t>"),
						new PositionSetType(NaviHall.WOOD))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("blue:<ps,t>"),
						new PositionSetType(NaviHall.BLUE))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("stone:<ps,t>"),
						new PositionSetType(NaviHall.STONE))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("brick:<ps,t>"),
						new PositionSetType(NaviHall.BRICK))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("grass:<ps,t>"),
						new PositionSetType(NaviHall.GRASS))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("honeycomb:<ps,t>"),
						new PositionSetType(NaviHall.HONEYCOMB))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("cement:<ps,t>"),
						new PositionSetType(NaviHall.CEMENT))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("butterfly_w:<ps,t>"),
						new PositionSetType(NaviWall.BUTTERFLY))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("eiffel_w:<ps,t>"),
						new PositionSetType(NaviWall.EIFFEL))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("fish_w:<ps,t>"),
						new PositionSetType(NaviWall.FISH))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("wall:<ps,t>"),
						new PositionSetType(NaviHall.WALL))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("corner:<ps,t>"),
						new PositionSetType(NaviMetaItem.CORNER))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("deadend:<ps,t>"),
						new PositionSetType(NaviMetaItem.DEADEND))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("intersection:<ps,t>"),
						new PositionSetType(NaviMetaItem.INTERSECTION))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("t_intersection:<ps,t>"),
						new PositionSetType(NaviMetaItem.T_INTERSECTION))
				.addEvaluator(
						(LogicalConstant) TestingConstants.CATEGORY_SERVICES
								.parseSemantics("hall:<ps,t>"),
						new PositionSetType(NaviHall.HALL));
		
		// Position functions
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("orient:<ps,<dir,ps>>"),
				new PositionSetOrient());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("frontdist:<ps,n>"),
				new PositionSetFrontDistance());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("dist:<ps,n>"),
				new PositionSetAgentDistance());
		
		// Position relations
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("intersect:<ps,<ps,t>>"),
				new PositionSetIntersect());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("middle:<ps,<ps,t>>"),
				new PositionSetMiddle());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("order:<<ps,t>,<<ps,n>,<n,ps>>>"),
				new PositionSetOrder());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("distance:<ps,<ps,<n,t>>>"),
				new PositionSetDistance());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("front:<ps,<ps,t>>"),
				new PositionSetFront());
		builder.addEvaluator(
				(LogicalConstant) TestingConstants.CATEGORY_SERVICES
						.parseSemantics("end:<ps,<ps,t>>"),
				new PositionSetEnd());
		
		// Stateful predicates
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("post:<a,<t,t>>"),
				NaviEvaluationConstants.POST_ACTION_STATE);
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("pass:<a,<ps,t>>"),
				NaviEvaluationConstants.POST_ACTION_STATE);
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("pre:<a,<t,t>>"),
				NaviEvaluationConstants.PRE_ACTION_STATE);
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("pre:<a,<ps,t>>"),
				NaviEvaluationConstants.PRE_ACTION_STATE);
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("while:<a,<ps,t>>"),
				NaviEvaluationConstants.PRE_ACTION_STATE);
		builder.addStatefulPredicate(TestingConstants.CATEGORY_SERVICES
				.parseSemantics("to:<a,<ps,t>>"),
				NaviEvaluationConstants.POST_ACTION_STATE);
		
		servicesFactory = new NaviEvaluationServicesFactory(builder.build());
		
	}
	
	public static NaviEvaluationServicesFactory getServicesFactory() {
		return INSTANCE.servicesFactory;
	}
	
}

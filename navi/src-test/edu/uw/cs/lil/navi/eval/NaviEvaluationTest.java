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
package edu.uw.cs.lil.navi.eval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.map.PerceptualCluster;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.Pose.Direction;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Tuple;
import edu.uw.cs.utils.collections.ListUtils;

public class NaviEvaluationTest {
	
	@Test
	public void testAQuantifier() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (to:<a,<ps,t>> $0 (a:<<e,t>,e> corner:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		Assert.assertEquals(new Pose(0, 5, Direction.D0), actions.get(0)
				.getEnd().getPose());
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
		
		System.out.println("result:\n" + result);
	}
	
	@Test
	public void testArgMax() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(argmax:<<e,t>,<<e,n>,e>> t_intersection:<ps,t> frontdist:<ps,n>)");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof PositionSet);
		Assert.assertEquals(4, ((PositionSet) result).size());
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D0)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D90)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D180)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D270)));
	}
	
	@Test
	public void testArgMax2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(7)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(argmax:<<e,t>,<<e,n>,e>> hall:<ps,t> dist:<ps,n>)");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof PositionSet);
		Assert.assertEquals(2, ((PositionSet) result).size());
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(1, 5, Direction.D90)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(1, 5, Direction.D270)));
	}
	
	@Test
	public void testArgMax3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(16, 18, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		// final LogicalExpression exp =
		// .of(TestingConstants.CATEGORY_SERVICES
		// .parseSemantics("(lambda $0:a (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $2:e (end:<ps,<ps,t>> $2 (io:<<e,t>,e> hall:<ps,t>))))))"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 (io:<<e,t>,e> hall:<ps,t>))) dist:<ps,n>))))))");
		
		// final LogicalExpression exp =
		// .of(TestingConstants.CATEGORY_SERVICES
		// .parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 (io:<<e,t>,e> hall:<ps,t>))) dist:<ps,n>)))))))"));
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(20, 18, Direction.D90)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.FORWARD));
	}
	
	@Test
	public void testArgMax4() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(21, 21, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a "
						+ "(post:<a,<t,t>> $0 "
						+ "(front:<ps,<ps,t>> you:ps "
						+ "(io:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 "
						+ "(argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 "
						+ "(io:<<e,t>,e> (lambda $3:e (and:<t*,t> "
						+ "(hall:<ps,t> $3) (intersect:<ps,<ps,t>> (a:<<e,t>,e> fish_w:<ps,t>) $3) (not:<t,t> (rose:<ps,t> $3))))))) "
						+ "dist:<ps,n>)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(21, 21, Direction.D180)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.LEFT));
	}
	
	@Test
	@Ignore
	public void testArgMin() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(23, 19, Direction.D180)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (post:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (a:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmin:<<e,t>,<<e,n>,e>> deadend:<ps,t> dist:<ps,n>)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof PositionSet);
		Assert.assertEquals(4, ((PositionSet) result).size());
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D0)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D90)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D180)));
		Assert.assertTrue(((PositionSet) result).contains(TestingConstants.MAPS
				.get("grid").get(0, 6, Direction.D270)));
	}
	
	@Test
	public void testDefQuantifier() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(15, 18, Direction.D180)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(io:<<e,t>,e> (lambda $2:e (end:<ps,<ps,t>> $2 (io:<<e,t>,e> hall:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final Set<Position> expectedPositions = new HashSet<Position>();
		expectedPositions.add(TestingConstants.MAPS.get("jelly").get(15, 20,
				Direction.D180));
		
		for (final Position p : (PerceptualCluster) result) {
			Assert.assertTrue(expectedPositions.contains(p));
			expectedPositions.remove(p);
		}
		Assert.assertTrue(expectedPositions.isEmpty());
	}
	
	@Test
	public void testDefQuantifier2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(io:<<e,t>,e> t_intersection:<ps,t>)");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final Set<Position> expectedPositions = new HashSet<Position>();
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 9,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 9,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 9,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 9,
				Direction.D270));
		
		for (final Position p : (PerceptualCluster) result) {
			Assert.assertTrue(expectedPositions.contains(p));
			expectedPositions.remove(p);
		}
		Assert.assertTrue(expectedPositions.isEmpty());
	}
	
	@Test
	public void testDefQuantifier3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 11, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(intersect:<ps,<ps,t>> x:ps (io:<<e,t>,e> (lambda $0:e (end:<ps,<ps,t>> $0 (io:<<e,t>,e> hall:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertEquals(Boolean.TRUE, result);
	}
	
	@Test
	public void testDistance() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 10, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:e (and:<t*,t> (furniture:<ps,t> $0) (distance:<ps,<ps,<n,t>>> $0 (a:<<e,t>,e> furniture:<ps,t>) 1:n)))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<PositionSet> pss = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, PositionSet>() {
					@Override
					public PositionSet process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true,
								obj.get(0) instanceof PositionSet);
						return (PositionSet) obj.get(0);
					}
				});
		
		Assert.assertEquals(5, pss.size());
		final Set<Position> expectedPositions = new HashSet<Position>();
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 7,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 7,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 7,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 7,
				Direction.D270));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 10,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 10,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 10,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 10,
				Direction.D270));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 8,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 8,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 8,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 8,
				Direction.D270));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 11,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 11,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 11,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(0, 11,
				Direction.D270));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(1, 10,
				Direction.D0));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(1, 10,
				Direction.D90));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(1, 10,
				Direction.D180));
		expectedPositions.add(TestingConstants.MAPS.get("grid").get(1, 10,
				Direction.D270));
		
		for (final PositionSet ps : pss) {
			for (final Position p : ps) {
				Assert.assertTrue(expectedPositions.contains(p));
				expectedPositions.remove(p);
			}
		}
		Assert.assertTrue(expectedPositions.isEmpty());
	}
	
	@Test
	public void testEnd1() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (end:<ps,<ps,t>> $1 (io:<<e,t>,e> (lambda $2:e (and:<t*,t> (blue:<ps,t> $2) (hall:<ps,t> $2))))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 5, Direction.D0)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.FORWARD));
	}
	
	@Test
	public void testEnd3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(21, 23, Direction.D270)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (end:<ps,<ps,t>> $1 (io:<<e,t>,e> (lambda $2:e (and:<t*,t> (honeycomb:<ps,t> $2) (hall:<ps,t> $2))))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(21, 24, Direction.D180)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.FORWARD));
	}
	
	@Test
	public void testEnd4() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 5, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (end:<ps,<ps,t>> $1 (io:<<e,t>,e> hall:<ps,t>)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(3, 5, Direction.D90)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.FORWARD));
	}
	
	@Test
	public void testEq2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(17, 18, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(6).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(7).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $2:e (eq:<e,<e,t>> (io:<<e,t>,e> (lambda $0:e (eq:<e,<e,t>> $0 (order:<<ps,t>,<<ps,n>,<n,ps>>> (lambda $1:e (middle:<ps,<ps,t>> $1 (io:<<e,t>,e> hall:<ps,t>))) frontdist:<ps,n> 1:n)))) $2))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof ILambdaResult);
		Assert.assertEquals(0, ((ILambdaResult) result).size());
	}
	
	@Test
	public void testExists() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(22, 23, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(exists:<<e,t>,t> (lambda $0:e (eq:<e,<e,t>> $0 (a:<<e,t>,e> "
						+ "(lambda $1:e (and:<t*,t> (sofa:<ps,t> $1) (intersect:<ps,<ps,t>> "
						+ "(io:<<e,t>,e> (lambda $2:e (and:<t*,t> (hall:<ps,t> $2) "
						+ "(front:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps right:dir) $2)))) $1)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue((Boolean) result);
	}
	
	@Test
	public void testFront() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (post:<a,<t,t>> $0 "
						+ "(front:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps right:dir) (a:<<e,t>,e> easel:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 8, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
		
		System.out.println("result:\n" + result);
	}
	
	@Test
	public void testFront2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> you:ps (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (rose:<ps,t> $1)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.FALSE.equals(result));
	}
	
	@Test
	public void testFront3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (rose:<ps,t> $1)))) (a:<<e,t>,e> barstool:<ps,t>))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.TRUE.equals(result));
	}
	
	@Test
	public void testFront4() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (rose:<ps,t> $1)))) (a:<<e,t>,e> hatrack:<ps,t>))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.TRUE.equals(result));
	}
	
	@Test
	public void testFront5() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (rose:<ps,t> $1)))) (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (grass:<ps,t> $1)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.FALSE.equals(result));
	}
	
	@Test
	public void testFront6() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(23, 16, Direction.D180)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(and:<t*,t> (front:<ps,<ps,t>> (a:<<e,t>,e> lamp:<ps,t>) (orient:<ps,<dir,ps>> you:ps right:dir)) (front:<ps,<ps,t>> (a:<<e,t>,e> lamp:<ps,t>) (io:<<e,t>,e> hatrack:<ps,t>)))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.FALSE.equals(result));
	}
	
	@Test
	public void testFront7() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(22, 23, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> (io:<<e,t>,e> (lambda $0:e (and:<t*,t> (intersect:<ps,<ps,t>> (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (cement:<ps,t> $1)))) $0) (sofa:<ps,t> $0)))) (orient:<ps,<dir,ps>> you:ps right:dir))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.TRUE.equals(result));
	}
	
	@Test
	public void testFront8() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(22, 23, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(front:<ps,<ps,t>> you:ps (io:<<e,t>,e> (lambda $0:e (and:<t*,t> (blue:<ps,t> $0) (hall:<ps,t> $0)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(Boolean.FALSE.equals(result));
	}
	
	@Test
	public void testHall() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(17, 17, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(7).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(2).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(io:<<e,t>,e> (lambda $1:e (and:<t*,t> (wall:<ps,t> $1) (hall:<ps,t> $1))))");
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		System.out.println(result);
		Assert.assertNull(result);
	}
	
	@Test
	public void testImplicit() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (post:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (lamp:<ps,t> $1) (intersect:<ps,<ps,t>> $1 (a:<<e,t>,e> (lambda $2:e (and:<t*,t> (hall:<ps,t> $2) (brick:<ps,t> $2)))))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		Assert.assertEquals(new Pose(0, 7, Direction.D90), actions.get(0)
				.getEnd().getPose());
		Assert.assertEquals(AgentAction.RIGHT, actions.get(0).getAgentAction());
	}
	
	@Test
	public void testImplicit2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (a:<<e,t>,e> easel:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		Assert.assertEquals(new Pose(3, 8, Direction.D90), actions.get(0)
				.getEnd().getPose());
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testMiddle() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) "
						+ "(to:<a,<ps,t>> $0 (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (chair:<ps,t> $1) "
						+ "(middle:<ps,<ps,t>> $1 (a:<<e,t>,e> (lambda $2:e (and:<t*,t> (blue:<ps,t> $2) (hall:<ps,t> $2)))))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testOrder() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(1, 8, Direction.D270)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (to:<a,<ps,t>> $0 "
						+ "(io:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (order:<<ps,t>,<<ps,n>,<n,ps>>> t_intersection:<ps,t> frontdist:<ps,n> 1:n)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 8, Direction.D270)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPass() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (pass:<a,<ps,t>> $0 (io:<<e,t>,e> barstool:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPass2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (pass:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (stone:<ps,t> $1)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPositionSetFrontDist1() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(frontdist:<ps,n> (io:<<e,t>,e> chair:<ps,t>))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertEquals(2.0, result);
	}
	
	@Test
	public void testPositionSetFrontDist2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(frontdist:<ps,n> (io:<<e,t>,e> corner:<ps,t>))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertEquals(4.0, result);
	}
	
	@Test
	public void testPositionSetIntersect() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) "
						+ "(to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (barstool:<ps,t> $1) (intersect:<ps,<ps,t>> $1 (io:<<e,t>,e> (lambda $2:e (and:<t*,t> (hall:<ps,t> $2) (stone:<ps,t> $2)))))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 8, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPositionSetIntersect2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3, 8, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(2).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(intersect:<ps,<ps,t>> (io:<<e,t>,e> (lambda $0:e (and:<t*,t> (intersection:<ps,t> $0) (intersect:<ps,<ps,t>> (io:<<e,t>,e> easel:<ps,t>) $0)))) x:ps)");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		Assert.assertEquals(Boolean.TRUE, result);
	}
	
	@Test
	public void testPositionSetOrient() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (post:<a,<t,t>> $0 (intersect:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps right:dir) (io:<<e,t>,e> wood:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPositionSetOrient2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 10, Direction.D180)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:m (front:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps $0) (a:<<e,t>,e> chair:<ps,t>)))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final Set<edu.uw.cs.lil.navi.agent.Direction> outputs = new HashSet<edu.uw.cs.lil.navi.agent.Direction>(
				ListUtils
						.map((ILambdaResult) result,
								new ListUtils.Mapper<Tuple, edu.uw.cs.lil.navi.agent.Direction>() {
									@Override
									public edu.uw.cs.lil.navi.agent.Direction process(
											Tuple obj) {
										Assert.assertEquals(obj.numKeys(), 1);
										Assert.assertEquals(
												true,
												obj.get(0) instanceof edu.uw.cs.lil.navi.agent.Direction);
										return (edu.uw.cs.lil.navi.agent.Direction) obj
												.get(0);
									}
								}));
		
		Assert.assertEquals(2, outputs.size());
		Assert.assertTrue(outputs
				.contains(edu.uw.cs.lil.navi.agent.Direction.BACK));
		Assert.assertTrue(outputs
				.contains(edu.uw.cs.lil.navi.agent.Direction.LEFT));
	}
	
	@Test
	public void testPost() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (post:<a,<t,t>> $0 (intersect:<ps,<ps,t>> you:ps (io:<<e,t>,e> chair:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPost2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(2, 5, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(6).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(4).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (dir:<a,<dir,t>> $0 right:dir) (post:<a,<t,t>> $0 (and:<t*,t> (exists:<<e,t>,t> (lambda $1:e (and:<t*,t> (grass:<ps,t> $1) (hall:<ps,t> $1) (front:<ps,<ps,t>> you:ps $1)))) (exists:<<e,t>,t> (lambda $2:e (and:<t*,t> (honeycomb:<ps,t> $2) (hall:<ps,t> $2) (intersect:<ps,<ps,t>> (io:<<e,t>,e> (lambda $3:e (eq:<e,<e,t>> $3 (order:<<ps,t>,<<ps,n>,<n,ps>>> intersection:<ps,t> frontdist:<ps,n> 1:n)))) $2))))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(2, 5, Direction.D90)));
		Assert.assertEquals(AgentAction.RIGHT, actions.get(0).getAgentAction());
	}
	
	@Test
	public void testPost3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(23, 23, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("l").get(6).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(4)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (post:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (wood:<ps,t> $1)))))) (turn:<a,t> $0) (len:<a,<n,t>> $0 1:n)))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(23, 17, Direction.D270)));
		Assert.assertEquals(AgentAction.LEFT, actions.get(0).getAgentAction());
	}
	
	@Test
	public void testPre() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 7, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = (TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (pre:<a,<ps,t>> $0 (io:<<e,t>,e> chair:<ps,t>))))"));
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 6, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPre2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 7, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (pre:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (wood:<ps,t> $1)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 6, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testPreStatus() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) "
						+ "(dir:<a,<dir,t>> $0 forward:dir) "
						+ "(pre:<a,<t,t>> $0 (and:<t*,t> (front:<ps,<ps,t>> you:ps (io:<<e,t>,e> barstool:<ps,t>)) (front:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps back:dir) (io:<<e,t>,e> hatrack:<ps,t>))))"
						+ "(post:<a,<t,t>> $0 "
						+ "(front:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps right:dir) (a:<<e,t>,e> easel:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 8, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
		
		System.out.println("result:\n" + result);
	}
	
	@Test
	public void testPreStatus2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) "
						+ "(dir:<a,<dir,t>> $0 forward:dir) "
						+ "(pre:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (io:<<e,t>,e> lamp:<ps,t>)))"
						+ "(post:<a,<t,t>> $0 "
						+ "(intersect:<ps,<ps,t>> you:ps (a:<<e,t>,e> honeycomb:<ps,t>)))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(3, 8, Direction.D90)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
		
	}
	
	@Test
	public void testSimple() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> "
						+ "(move:<a,t> $0) "
						+ "(dir:<a,<dir,t>> $0 forward:dir) "
						+ "(len:<a,<n,t>> $0 2:n)))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 7, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testTo() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (to:<a,<ps,t>> $0 (io:<<e,t>,e> barstool:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 8, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testTo2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(18, 19, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(18, 19, Direction.D0)
						.getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (rose:<ps,t> $1) (hall:<ps,t> $1)))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(19, 19, Direction.D90)));
		final Set<AgentAction> agentActions = new HashSet<AgentAction>(
				ListUtils.map(actions,
						new ListUtils.Mapper<Action, AgentAction>() {
							
							@Override
							public AgentAction process(Action obj) {
								return obj.getAgentAction();
							}
						}));
		
		Assert.assertEquals(true, agentActions.contains(AgentAction.FORWARD));
	}
	
	@Test
	public void testWall() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (to:<a,<ps,t>> $0 (a:<<e,t>,e> wall:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 5, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testWall2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(2)), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(2).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (post:<a,<t,t>> $0 (and:<t*,t> (intersect:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps left:dir) (a:<<e,t>,e> wall:<ps,t>)) (intersect:<ps,<ps,t>> (orient:<ps,<dir,ps>> you:ps back:dir) (a:<<e,t>,e> wall:<ps,t>))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(23, 16, Direction.D270)));
		Assert.assertEquals(AgentAction.LEFT, actions.get(0).getAgentAction());
	}
	
	@Test
	public void testWhile() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 8, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (len:<a,<n,t>> $0 3:n) (while:<a,<ps,t>> $0 (a:<<e,t>,e> (lambda $1:e (front:<ps,<ps,t>> (a:<<e,t>,e> lamp:<ps,t>) $1))))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(3, 8, Direction.D90)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testWhile2() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(0, 8, Direction.D90)), new PositionSet(
				TestingConstants.MAPS.get("grid").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (len:<a,<n,t>> $0 3:n) (while:<a,<ps,t>> $0 (a:<<e,t>,e> blue:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(0, 5, Direction.D0)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testWhile3() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (len:<a,<n,t>> $0 3:n) (while:<a,<ps,t>> $0 (a:<<e,t>,e> wood:<ps,t>))))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(3, 7, Direction.D90)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
	@Test
	public void testWhile4() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(19, 19, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> "
						+ "(to:<a,<ps,t>> $0 (io:<<e,t>,e> chair:<ps,t>)) "
						+ "(while:<a,<ps,t>> $0 (io:<<e,t>,e> blue:<ps,t>))"
						+ "))");
		
		final Object result = NaviEvaluation.of(exp, NaviEvalTestingConstants
				.getServicesFactory().create(task));
		
		System.out.println("result:\n" + result);
		
		final List<Action> actions = ListUtils.map((ILambdaResult) result,
				new ListUtils.Mapper<Tuple, Action>() {
					@Override
					public Action process(Tuple obj) {
						Assert.assertEquals(obj.numKeys(), 1);
						Assert.assertEquals(true, obj.get(0) instanceof Action);
						return (Action) obj.get(0);
					}
				});
		
		Assert.assertEquals(1, actions.size());
		final Set<Pose> endPoses = new HashSet<Pose>(ListUtils.map(actions,
				new ListUtils.Mapper<Action, Pose>() {
					
					@Override
					public Pose process(Action obj) {
						return obj.getEnd().getPose();
					}
				}));
		Assert.assertEquals(true,
				endPoses.contains(new Pose(17, 18, Direction.D270)));
		Assert.assertEquals(AgentAction.FORWARD, actions.get(0)
				.getAgentAction());
	}
	
}

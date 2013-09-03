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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.data.LabeledInstructionSeqTrace;
import edu.uw.cs.lil.navi.data.LabeledInstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.LabeledInstructionTrace;
import edu.uw.cs.lil.navi.data.LabeledInstructionTraceDataset;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.map.Pose.Direction;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.LambdaWrapped;

public class NaviSingleEvaluatorTest {
	
	@Test
	public void testAction() {
		final NaviSingleEvaluator executor = new NaviSingleEvaluator(
				NaviEvalTestingConstants.getServicesFactory());
		
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("move:<a,t>"));
		
		// final LogicalExpression exp = LambdaWrapped
		// .of(TestingConstants.CATEGORY_SERVICES
		// .parseSemantics("move:<a,t>"));
		
		final Object result = executor.of(exp, task);
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof Trace);
		Assert.assertEquals(
				TestingConstants.MAPS.get("grid").get(0, 8, Direction.D0),
				((Trace) result).getEndPosition());
	}
	
	@Test
	public void testImplicitActions() {
		final NaviSingleEvaluator executor = new NaviSingleEvaluator(
				NaviEvalTestingConstants.getServicesFactory());
		
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(19, 19, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a[] (and:<t*,t> "
								+ "(while:<a,<ps,t>> (i:<a[],<ind,a>> $0 0:ind) (io:<<e,t>,e> blue:<ps,t>)) "
								+ "(move:<a,t> (i:<a[],<ind,a>> $0 0:ind)) "
								+ "(to:<a,<ps,t>> (i:<a[],<ind,a>> $0 0:ind) (io:<<e,t>,e> chair:<ps,t>)) "
								+ "(bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) "
								+ "(dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 1:ind) right:dir) "
								+ "(turn:<a,t> (i:<a[],<ind,a>> $0 1:ind))))"));
		
		final Object result = executor.of(exp, task);
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof Trace);
		Assert.assertEquals(
				TestingConstants.MAPS.get("jelly").get(17, 18, Direction.D0),
				((Trace) result).getEndPosition());
	}
	
	@Test
	public void testImplicitActions2() {
		final NaviSingleEvaluator executor = new NaviSingleEvaluator(
				NaviEvalTestingConstants.getServicesFactory());
		
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("l")
				.get(23, 21, Direction.D270)), new PositionSet(
				TestingConstants.MAPS.get("l").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("l").get(5)
				.getAllOrientations(), false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("l"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a[] (and:<t*,t> "
								+ "(dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 0:ind) left:dir) "
								+ "(turn:<a,t> (i:<a[],<ind,a>> $0 0:ind)) "
								+ "(bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) "
								+ "(len:<a,<n,t>> (i:<a[],<ind,a>> $0 1:ind) 4:n) "
								+ "(move:<a,t> (i:<a[],<ind,a>> $0 1:ind)) "
								+ "(dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 1:ind) forward:dir)))"));
		
		final Object result = executor.of(exp, task);
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof Trace);
		Assert.assertEquals(
				TestingConstants.MAPS.get("l").get(23, 17, Direction.D0),
				((Trace) result).getEndPosition());
	}
	
	@Test
	public void testImplicitActions3() {
		final NaviSingleEvaluator executor = new NaviSingleEvaluator(
				NaviEvalTestingConstants.getServicesFactory());
		
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("jelly")
				.get(20, 17, Direction.D0)), new PositionSet(
				TestingConstants.MAPS.get("jelly").get(3).getAllOrientations(),
				false), new PositionSet(TestingConstants.MAPS.get("jelly")
				.get(5).getAllOrientations(), false),
				new HashMap<String, String>(),
				TestingConstants.MAPS.get("jelly"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> "
								+ "(pass:<a,<ps,t>> $0 (a:<<e,t>,e> sofa:<ps,t>)) "
								+ "(while:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (hall:<ps,t> $1) (brick:<ps,t> $1))))) "
								+ "(move:<a,t> $0)))"));
		
		final Object result = executor.of(exp, task);
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof Trace);
		Assert.assertEquals(
				TestingConstants.MAPS.get("jelly").get(17, 16, Direction.D270),
				((Trace) result).getEndPosition());
	}
	
	@Test
	public void testImplicitStatement() {
		final NaviSingleEvaluator executor = new NaviSingleEvaluator(
				NaviEvalTestingConstants.getServicesFactory());
		
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(intersect:<ps,<ps,t>> you:ps (a:<<e,t>,e> (lambda $0:e (and:<t*,t> (wood:<ps,t> $0) (hall:<ps,t> $0) (intersect:<ps,<ps,t>> (a:<<e,t>,e> lamp:<ps,t>) $0)))))"));
		
		final Object result = executor.of(exp, task);
		
		System.out.println("result:\n" + result);
		
		Assert.assertTrue(result instanceof Trace);
		Assert.assertEquals(
				TestingConstants.MAPS.get("grid").get(0, 7, Direction.D0),
				((Trace) result).getEndPosition());
	}
	
	@Test
	public void testSample30() {
		try {
			final LabeledInstructionTraceDataset<LogicalExpression> dataset = LabeledInstructionTraceDataset
					.readFromFile(
							new File("..", "resources-test/sample.ccgtrc"),
							TestingConstants.MAPS,
							TestingConstants.CATEGORY_SERVICES);
			
			final NaviSingleEvaluator executor = new NaviSingleEvaluator(
					NaviEvalTestingConstants.getServicesFactory());
			
			double timeSum = 0;
			
			for (final LabeledInstructionTrace<LogicalExpression> dataItem : dataset) {
				
				final LogicalExpression exp = LambdaWrapped.of(dataItem
						.getLabel().first());
				final Task task = new Task(new Agent(dataItem.getTrace()
						.getStartPosition()), new PositionSet(
						TestingConstants.MAPS
								.get("grid")
								.get(Integer.valueOf(dataItem.getTask()
										.getProperty("y")))
								.getAllOrientations(), false), new PositionSet(
						TestingConstants.MAPS
								.get(dataItem.getTask().getProperty("map"))
								.get(Integer.valueOf(dataItem.getTask()
										.getProperty("x")))
								.getAllOrientations(), false),
						new HashMap<String, String>(),
						TestingConstants.MAPS.get(dataItem.getTask()
								.getProperty("map")));
				System.out.println(dataItem);
				final long startTime = System.currentTimeMillis();
				final Object result = executor.of(exp, task);
				final long endTime = System.currentTimeMillis();
				System.out.println(String.format("Result:\n%s", result));
				System.out.println(String.format("time: %fsec",
						(endTime - startTime) / 1000.0));
				timeSum += (endTime - startTime) / 1000.0;
				System.out.println();
				if (exp.getType().equals(
						LogicLanguageServices.getTypeRepository()
								.getTruthValueType())) {
					// Case truth type statement
					Assert.assertTrue((Boolean) result);
				} else {
					// Case action instruction
					Assert.assertEquals(dataItem.getTrace(), result);
				}
				
			}
			
			System.out.println(String.format("Total time: %f", timeSum));
			
		} catch (final IOException e) {
			fail("io exception");
		}
	}
	
	@Test
	public void testSeedSet() {
		try {
			final LabeledInstructionSeqTraceDataset<LogicalExpression> sets = LabeledInstructionSeqTraceDataset
					.readFromFile(new File("..", "resources/seed.ccgsettrc"),
							TestingConstants.MAPS,
							TestingConstants.CATEGORY_SERVICES);
			
			final NaviSingleEvaluator evaluator = new NaviSingleEvaluator(
					NaviEvalTestingConstants.getServicesFactory());
			
			final List<LabeledInstructionTrace<LogicalExpression>> items = new LinkedList<LabeledInstructionTrace<LogicalExpression>>();
			for (final LabeledInstructionSeqTrace<LogicalExpression> set : sets) {
				for (final LabeledInstructionTrace<LogicalExpression> lst : set) {
					items.add(lst);
				}
			}
			
			int incorrects = 0;
			
			for (final LabeledInstructionTrace<LogicalExpression> lst : items) {
				System.out.println(lst);
				final long startTime = System.currentTimeMillis();
				final Object evalResult = evaluator.of(lst.getLabel().first(),
						lst.getTask());
				System.out.println(String.format("Evaluation time: %.4f",
						(System.currentTimeMillis() - startTime) / 1000.0));
				System.out.println("Result: " + evalResult);
				if (evalResult instanceof Boolean) {
					if (!lst.getTrace().getSteps().isEmpty()) {
						System.out.println("Incorrect evaluation");
						incorrects++;
					}
				} else {
					if (!lst.getTrace().equals(evalResult)) {
						System.out.println("Incorrect evaluation");
						incorrects++;
					}
				}
				System.out.println();
			}
			Assert.assertEquals(
					String.format(
							"%d incorrect evaluations (5 examples are known not to execute correctly)",
							incorrects), 5, incorrects);
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

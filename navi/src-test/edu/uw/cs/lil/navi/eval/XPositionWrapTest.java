package edu.uw.cs.lil.navi.eval;

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.eval.XPositionWrap;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.LambdaWrapped;

public class XPositionWrapTest {
	
	@Test
	public void test() {
		final LogicalExpression input = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(eq:<e,<e,t>> you:ps x:ps)");
		final LogicalExpression expected = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:e (eq:<e,<e,t>> you:ps $0))"));
		final LogicalExpression output = XPositionWrap.of(input,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		Assert.assertEquals(expected, output);
	}
	
	@Test
	public void test2() {
		final LogicalExpression input = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 (eq:<e,<e,t>> you:ps x:ps))))");
		final LogicalExpression expected = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $1:e (lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 (eq:<e,<e,t>> you:ps $1))))"));
		final LogicalExpression output = XPositionWrap.of(input,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		Assert.assertEquals(expected, output);
	}
	
	@Test
	public void test3() {
		final LogicalExpression input = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 (eq:<e,<e,t>> you:ps y:ps))))");
		final LogicalExpression expected = null;
		final LogicalExpression output = XPositionWrap.of(input,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		Assert.assertEquals(expected, output);
	}
	
}

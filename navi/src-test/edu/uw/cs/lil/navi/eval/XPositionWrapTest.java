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

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class XPositionWrapTest {
	
	@Test
	public void test() {
		final LogicalExpression input = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(eq:<e,<e,t>> you:ps x:ps)");
		final LogicalExpression expected = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:e (eq:<e,<e,t>> you:ps $0))");
		final LogicalExpression output = XPositionWrap.of(input,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		Assert.assertEquals(expected, output);
	}
	
	@Test
	public void test2() {
		final LogicalExpression input = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 (eq:<e,<e,t>> you:ps x:ps))))");
		final LogicalExpression expected = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $1:e (lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 (eq:<e,<e,t>> you:ps $1))))");
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

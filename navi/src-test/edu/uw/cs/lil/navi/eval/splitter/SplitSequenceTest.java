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
package edu.uw.cs.lil.navi.eval.splitter;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.eval.NaviEvalTestingConstants;
import edu.uw.cs.lil.navi.eval.splitter.SplitSequence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class SplitSequenceTest {
	
	@Test
	public void test() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> (turn:<a,t> (i:<a[],<ind,a>> $0 0:ind)) (dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 0:ind) left:dir) (bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) (move:<a,t> (i:<a[],<ind,a>> $0 1:ind)) (to:<a,<ps,t>> (i:<a[],<ind,a>> $0 1:ind) (io:<<e,t>,e> (lambda $2:e (end:<ps,<ps,t>> $2 (io:<<e,t>,e> (lambda $3:e (and:<t*,t> (honeycomb:<ps,t> $3) (hall:<ps,t> $3))))))))))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (dir:<a,<dir,t>> $0 left:dir)))"),
				seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (end:<ps,<ps,t>> $1 (io:<<e,t>,e> (lambda $2:e (and:<t*,t> (honeycomb:<ps,t> $2) (hall:<ps,t> $2))))))))))"),
				seq.get(1));
		
	}
	
	@Test
	public void test2() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> (move:<a,t> (i:<a[],<ind,a>> $0 0:ind)) (to:<a,<ps,t>> (i:<a[],<ind,a>> $0 0:ind) (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (rose:<ps,t> $1) (hall:<ps,t> $1))))) (bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) (turn:<a,t> (i:<a[],<ind,a>> $0 1:ind)) (dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 1:ind) left:dir)))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $1:e (and:<t*,t> (rose:<ps,t> $1) (hall:<ps,t> $1)))))))"),
				seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (dir:<a,<dir,t>> $0 left:dir)))"),
				seq.get(1));
		
	}
	
	@Test
	public void test3() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> (turn:<a,t> (i:<a[],<ind,a>> $0 0:ind)) (dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 0:ind) left:dir) (bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) (move:<a,t> (i:<a[],<ind,a>> $0 1:ind)) (dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 1:ind) forward:dir) (len:<a,<n,t>> (i:<a[],<ind,a>> $0 1:ind) 1:n)))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (dir:<a,<dir,t>> $0 left:dir)))"),
				seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (dir:<a,<dir,t>> $0 forward:dir) (len:<a,<n,t>> $0 1:n)))"),
				seq.get(1));
		
	}
	
	@Test
	public void test4() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> (turn:<a,t> (i:<a[],<ind,a>> $0 0:ind)) (dir:<a,<dir,t>> (i:<a[],<ind,a>> $0 0:ind) right:dir) (bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) (move:<a,t> (i:<a[],<ind,a>> $0 1:ind)) (post:<a,<t,t>> (i:<a[],<ind,a>> $0 1:ind) (and:<t*,t> (exists:<<e,t>,t> (lambda $1:e (and:<t*,t> (grass:<ps,t> $1) (hall:<ps,t> $1) (front:<ps,<ps,t>> you:ps $1)))) (exists:<<e,t>,t> (lambda $2:e (and:<t*,t> (honeycomb:<ps,t> $2) (hall:<ps,t> $2) (intersect:<ps,<ps,t>> (io:<<e,t>,e> (lambda $3:e (eq:<e,<e,t>> $3 (order:<<ps,t>,<<ps,n>,<n,ps>>> intersection:<ps,t> frontdist:<ps,n> 1:n)))) $2))))))))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (turn:<a,t> $0) (dir:<a,<dir,t>> $0 right:dir)))"),
				seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (post:<a,<t,t>> $0 "
								+ "(and:<t*,t> (exists:<<e,t>,t> (lambda $1:e (and:<t*,t> (grass:<ps,t> $1) (hall:<ps,t> $1) (front:<ps,<ps,t>> you:ps $1)))) (exists:<<e,t>,t> (lambda $2:e (and:<t*,t> (honeycomb:<ps,t> $2) (hall:<ps,t> $2) (intersect:<ps,<ps,t>> "
								+ "(io:<<e,t>,e> (lambda $3:e (eq:<e,<e,t>> $3 (order:<<ps,t>,<<ps,n>,<n,ps>>> intersection:<ps,t> frontdist:<ps,n> 1:n)))) $2))))))))"),
				seq.get(1));
		
	}
	
	@Test
	public void test5() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> (post:<a,<t,t>> (i:<a[],<ind,a>> $0 0:ind) (front:<ps,<ps,t>> you:ps (io:<<e,t>,e> hatrack:<ps,t>))) (bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) (move:<a,t> (i:<a[],<ind,a>> $0 1:ind)) (to:<a,<ps,t>> (i:<a[],<ind,a>> $0 1:ind) (io:<<e,t>,e> hatrack:<ps,t>))))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (post:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (io:<<e,t>,e> hatrack:<ps,t>))))"),
				seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (to:<a,<ps,t>> $0 (io:<<e,t>,e> (lambda $3:e (hatrack:<ps,t> $3))))))"),
				seq.get(1));
		
	}
	
	@Test
	public void test6() {
		final LogicalExpression exp = TestingConstants.CATEGORY_SERVICES
				.parseSemantics("(lambda $0:a[] (and:<t*,t> "
						+ "(while:<a,<ps,t>> (i:<a[],<ind,a>> $0 0:ind) (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (blue:<ps,t> $1) (eq:<e,<e,t>> $1 "
						+ "(argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 (a:<<e,t>,e> "
						+ "(lambda $3:e (and:<t*,t> (intersect:<ps,<ps,t>> $3 (a:<<e,t>,e> (lambda $4:e (end:<ps,<ps,t>> $4 (a:<<e,t>,e> hall:<ps,t>))))) "
						+ "(intersect:<ps,<ps,t>> (a:<<e,t>,e> hall:<ps,t>) $3)))))) dist:<ps,n>)))))) "
						+ "(move:<a,t> (i:<a[],<ind,a>> $0 0:ind)) "
						+ "(bef:<a,<a,t>> (i:<a[],<ind,a>> $0 0:ind) (i:<a[],<ind,a>> $0 1:ind)) "
						+ "(post:<a,<t,t>> (i:<a[],<ind,a>> $0 1:ind) (front:<ps,<ps,t>> you:ps (a:<<e,t>,e> easel:<ps,t>))) "
						+ "(to:<a,<ps,t>> (i:<a[],<ind,a>> $0 1:ind) (a:<<e,t>,e> intersection:<ps,t>)) "
						+ "(turn:<a,t> (i:<a[],<ind,a>> $0 1:ind)) "
						+ "(pre:<a,<ps,t>> (i:<a[],<ind,a>> $0 1:ind) (a:<<e,t>,e> corner:<ps,t>))))");
		final List<LogicalExpression> seq = SplitSequence.of(exp,
				NaviEvalTestingConstants.getServicesFactory()
						.getNaviEvaluationConsts());
		for (final LogicalExpression single : seq) {
			System.out.println(single);
		}
		Assert.assertEquals(2, seq.size());
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> "
								+ "(while:<a,<ps,t>> $0 (a:<<e,t>,e> (lambda $1:e (and:<t*,t> (blue:<ps,t> $1) (eq:<e,<e,t>> $1 "
								+ "(argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 (a:<<e,t>,e> "
								+ "(lambda $3:e (and:<t*,t> (intersect:<ps,<ps,t>> $3 (a:<<e,t>,e> (lambda $4:e (end:<ps,<ps,t>> $4 (a:<<e,t>,e> hall:<ps,t>))))) "
								+ "(intersect:<ps,<ps,t>> (a:<<e,t>,e> hall:<ps,t>) $3)))))) dist:<ps,n>)))))) "
								+ "(move:<a,t> $0)))"), seq.get(0));
		Assert.assertEquals(
				TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> "
								+ "(post:<a,<t,t>> $0 (front:<ps,<ps,t>> you:ps (a:<<e,t>,e> easel:<ps,t>))) "
								+ "(to:<a,<ps,t>> $0 (a:<<e,t>,e> intersection:<ps,t>)) "
								+ "(turn:<a,t> $0) "
								+ "(pre:<a,<ps,t>> $0 (a:<<e,t>,e> corner:<ps,t>))))"),
				seq.get(1));
		
	}
}

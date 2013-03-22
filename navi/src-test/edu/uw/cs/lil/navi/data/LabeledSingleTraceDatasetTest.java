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
package edu.uw.cs.lil.navi.data;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class LabeledSingleTraceDatasetTest {
	
	@Test
	public void test() {
		try {
			final LabeledInstructionTraceDataset<LogicalExpression> dataset = LabeledInstructionTraceDataset
					.readFromFile(
							new File("..", "resources-test/sample.ccgtrc"),
							TestingConstants.MAPS,
							TestingConstants.CATEGORY_SERVICES, null);
			System.out.println(dataset);
			Assert.assertEquals(31, dataset.size());
		} catch (final IOException e) {
			fail("Not yet implemented");
		}
	}
	
}

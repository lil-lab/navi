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

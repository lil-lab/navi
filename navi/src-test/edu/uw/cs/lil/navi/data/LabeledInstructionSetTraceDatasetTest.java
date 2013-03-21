package edu.uw.cs.lil.navi.data;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;

public class LabeledInstructionSetTraceDatasetTest {
	
	public LabeledInstructionSetTraceDatasetTest() {
		new TestingConstants();
	}
	
	@Test
	public void test() {
		try {
			LabeledInstructionSeqTraceDataset.readFromFile(new File("..",
					"resources/seed.ccgsettrc"), TestingConstants.MAPS, null,
					TestingConstants.CATEGORY_SERVICES);
		} catch (final IOException e) {
			fail();
		}
	}
	
}

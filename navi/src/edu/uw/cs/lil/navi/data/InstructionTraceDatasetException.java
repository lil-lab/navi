package edu.uw.cs.lil.navi.data;

public class InstructionTraceDatasetException extends RuntimeException {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3851767888884807551L;
	private final Exception		baseException;
	private final int			count;
	private final String		source;
	
	public InstructionTraceDatasetException(Exception baseException, String source,
			int itemNumber) {
		super(baseException);
		this.baseException = baseException;
		this.source = source;
		this.count = itemNumber;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(
				"LabeledSingleTraceDataset exception in item #").append(count)
				.append(":\n").append(source).append("\nbase exception:\n")
				.append(baseException).toString();
	}
}

package edu.uw.cs.lil.navi.data;

import java.util.Iterator;
import java.util.List;

import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.situated.ISituatedDataItem;

/**
 * Sequence of instructions to be executed one from an initial state.
 * 
 * @author Yoav Artzi
 */
public class InstructionSeq implements ISituatedDataItem<List<Sentence>, Task>,
		Iterable<Sentence> {
	
	private final List<Sentence>	instructions;
	private final Task				task;
	
	public InstructionSeq(List<Sentence> instructions, Task task) {
		this.instructions = instructions;
		this.task = task;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final InstructionSeq other = (InstructionSeq) obj;
		if (instructions == null) {
			if (other.instructions != null) {
				return false;
			}
		} else if (!instructions.equals(other.instructions)) {
			return false;
		}
		if (task == null) {
			if (other.task != null) {
				return false;
			}
		} else if (!task.equals(other.task)) {
			return false;
		}
		return true;
	}
	
	public List<Sentence> getInstructions() {
		return instructions;
	}
	
	@Override
	public List<Sentence> getSample() {
		return instructions;
	}
	
	@Override
	public Task getState() {
		return task;
	}
	
	public Task getTask() {
		return task;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instructions == null) ? 0 : instructions.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}
	
	@Override
	public Iterator<Sentence> iterator() {
		return instructions.iterator();
	}
	
}

package edu.uw.cs.lil.navi.data;

import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.situated.sentence.SituatedSentence;

/**
 * Single instruction in a situated context.
 * 
 * @author Yoav Artzi
 */
public class Instruction extends SituatedSentence<Task> {
	
	public Instruction(Sentence sentence, Task state) {
		super(sentence, state);
	}
	
}

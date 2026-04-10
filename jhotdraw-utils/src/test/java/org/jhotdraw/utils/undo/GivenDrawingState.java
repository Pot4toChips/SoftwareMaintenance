package org.jhotdraw.utils.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import javax.swing.undo.AbstractUndoableEdit;

public class GivenDrawingState extends Stage<GivenDrawingState> {

  @ScenarioState
  UndoRedoManager manager;

  public GivenDrawingState a_fresh_drawing() {
    manager = new UndoRedoManager();
    return self();
  }

  public GivenDrawingState a_drawing_with_a_performed_action() {
    manager = new UndoRedoManager();
    manager.addEdit(new AbstractUndoableEdit() {});
    return self();
  }

  public GivenDrawingState a_drawing_action_that_was_then_undone() {
    manager = new UndoRedoManager();
    manager.addEdit(new AbstractUndoableEdit() {});
    manager.undo();
    return self();
  }
}

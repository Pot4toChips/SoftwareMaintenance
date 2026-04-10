package org.jhotdraw.utils.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import javax.swing.undo.AbstractUndoableEdit;

public class WhenDesignerPerforms extends Stage<WhenDesignerPerforms> {

  @ScenarioState
  UndoRedoManager manager;

  public WhenDesignerPerforms the_designer_performs_a_drawing_action() {
    manager.addEdit(new AbstractUndoableEdit() {});
    return self();
  }

  public WhenDesignerPerforms the_designer_undoes_the_last_action() {
    manager.undo();
    return self();
  }

  public WhenDesignerPerforms the_designer_redoes_the_last_action() {
    manager.redo();
    return self();
  }
}

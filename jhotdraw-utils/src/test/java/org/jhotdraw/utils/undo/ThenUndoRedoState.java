package org.jhotdraw.utils.undo;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

public class ThenUndoRedoState extends Stage<ThenUndoRedoState> {

  @ScenarioState
  UndoRedoManager manager;

  public ThenUndoRedoState the_undo_action_is_enabled() {
    assertThat(manager.getUndoAction().isEnabled()).isTrue();
    return self();
  }

  public ThenUndoRedoState the_undo_action_is_disabled() {
    assertThat(manager.getUndoAction().isEnabled()).isFalse();
    return self();
  }

  public ThenUndoRedoState the_redo_action_is_enabled() {
    assertThat(manager.getRedoAction().isEnabled()).isTrue();
    return self();
  }

  public ThenUndoRedoState the_redo_action_is_disabled() {
    assertThat(manager.getRedoAction().isEnabled()).isFalse();
    return self();
  }
}

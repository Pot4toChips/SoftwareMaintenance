package org.jhotdraw.utils.undo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UndoRedoManagerTest {

  private UndoRedoManager manager;

  @BeforeEach
  void setUp() {
    manager = new UndoRedoManager();
  }

  @Test
  void undoActionIsDisabledInitially() {
    assertFalse(manager.getUndoAction().isEnabled());
  }

  @Test
  void redoActionIsDisabledInitially() {
    assertFalse(manager.getRedoAction().isEnabled());
  }

  @Test
  void hasNoSignificantEditsInitially() {
    assertFalse(manager.hasSignificantEdits());
  }

  @Test
  void undoIsEnabledAfterAddingAnEdit() {
    manager.addEdit(new SimpleEdit());
    assertTrue(manager.getUndoAction().isEnabled());
  }

  @Test
  void redoRemainsDisabledAfterAddingAnEdit() {
    manager.addEdit(new SimpleEdit());
    assertFalse(manager.getRedoAction().isEnabled());
  }

  @Test
  void hasSignificantEditsAfterAddingSignificantEdit() {
    manager.addEdit(new SimpleEdit());
    assertTrue(manager.hasSignificantEdits());
  }

  @Test
  void redoIsEnabledAfterUndo() {
    manager.addEdit(new SimpleEdit());
    manager.undo();
    assertTrue(manager.getRedoAction().isEnabled());
  }

  @Test
  void undoIsDisabledAfterUndoingTheOnlyEdit() {
    manager.addEdit(new SimpleEdit());
    manager.undo();
    assertFalse(manager.getUndoAction().isEnabled());
  }

  @Test
  void undoIsEnabledAgainAfterRedo() {
    manager.addEdit(new SimpleEdit());
    manager.undo();
    manager.redo();
    assertTrue(manager.getUndoAction().isEnabled());
  }

  @Test
  void insignificantEditDoesNotSetSignificantEditsFlag() {
    manager.addEdit(new InsignificantEdit());
    assertFalse(manager.hasSignificantEdits());
  }

  @Test
  void bothActionsAreDisabledAfterDiscardAllEdits() {
    manager.addEdit(new SimpleEdit());
    manager.discardAllEdits();

    assert !manager.canUndo() : "canUndo must be false after discard";
    assert !manager.canRedo() : "canRedo must be false after discard";

    assertFalse(manager.getUndoAction().isEnabled());
    assertFalse(manager.getRedoAction().isEnabled());
  }

  @Test
  void significantEditsFlagIsClearedAfterDiscard() {
    manager.addEdit(new SimpleEdit());
    manager.discardAllEdits();
    assertFalse(manager.hasSignificantEdits());
  }

  @Test
  void editAddedWhileUndoIsInProgressIsDiscarded() {
    UndoableEdit concurrentEdit = mock(UndoableEdit.class);

    manager.addEdit(new AbstractUndoableEdit() {
      @Override
      public void undo() {
        super.undo();
        manager.addEdit(concurrentEdit);
      }
    });

    manager.undo();

    verify(concurrentEdit).die();
  }

  @Test
  void propertyChangeIsFiredWhenSignificantEditsBecomesTrue() {
    PropertyChangeListener listener = mock(PropertyChangeListener.class);
    manager.addPropertyChangeListener("hasSignificantEdits", listener);

    manager.addEdit(new SimpleEdit());

    verify(listener).propertyChange(any(PropertyChangeEvent.class));
  }

  @Test
  void discardAllEditsConstantDisablesBothActions() {
    manager.addEdit(new SimpleEdit());
    manager.addEdit(UndoRedoManager.DISCARD_ALL_EDITS);

    assertFalse(manager.getUndoAction().isEnabled());
    assertFalse(manager.getRedoAction().isEnabled());
  }

  private static class SimpleEdit extends AbstractUndoableEdit {}

  private static class InsignificantEdit extends AbstractUndoableEdit {
    @Override
    public boolean isSignificant() {
      return false;
    }
  }
}

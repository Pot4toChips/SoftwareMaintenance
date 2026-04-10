package org.jhotdraw.utils.undo;

import com.tngtech.jgiven.junit5.ScenarioTest;
import org.junit.jupiter.api.Test;

class UndoRedoBDDTest
    extends ScenarioTest<GivenDrawingState, WhenDesignerPerforms, ThenUndoRedoState> {

  @Test
  void undo_and_redo_are_greyed_out_on_a_fresh_drawing() {
    given().a_fresh_drawing();
    then().the_undo_action_is_disabled().and().the_redo_action_is_disabled();
  }

  @Test
  void undo_becomes_available_after_performing_a_drawing_action() {
    given().a_fresh_drawing();
    when().the_designer_performs_a_drawing_action();
    then().the_undo_action_is_enabled();
  }

  @Test
  void redo_is_not_available_right_after_a_drawing_action() {
    given().a_fresh_drawing();
    when().the_designer_performs_a_drawing_action();
    then().the_redo_action_is_disabled();
  }

  @Test
  void undoing_an_action_enables_redo_and_disables_undo() {
    given().a_drawing_with_a_performed_action();
    when().the_designer_undoes_the_last_action();
    then().the_redo_action_is_enabled().and().the_undo_action_is_disabled();
  }

  @Test
  void redoing_an_undone_action_makes_undo_available_again() {
    given().a_drawing_action_that_was_then_undone();
    when().the_designer_redoes_the_last_action();
    then().the_undo_action_is_enabled().and().the_redo_action_is_disabled();
  }
}

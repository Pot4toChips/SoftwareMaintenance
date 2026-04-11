package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.junit5.ScenarioTest;
import org.junit.jupiter.api.Test;

class BezierFigureBDDTest
    extends ScenarioTest<GivenBezierFigure, WhenInteractingWithFigure, ThenFigureBehaves> {

  @Test
  void a_user_can_add_control_points_to_a_bezier_figure() {
    given().a_new_bezier_figure();
    when().the_user_adds_a_node_at(10, 20);
    then().the_figure_has_$_nodes(1).and().the_node_at_index_$_has_coordinates_$(0, 10, 20);
  }

  @Test
  void a_closed_figure_contains_a_point_inside_its_area() {
    given().a_closed_square_figure_from_$(0, 0)._to_$(100, 100);
    when().the_user_checks_if_point_$_is_contained(50, 50);
    then().the_result_is(true);
  }

  @Test
  void a_closed_figure_does_not_contain_a_point_outside_its_area() {
    given().a_closed_square_figure_from_$(0, 0)._to_$(100, 100);
    when().the_user_checks_if_point_$_is_contained(500, 500);
    then().the_result_is(false);
  }

  @Test
  void an_open_path_contains_a_point_lying_on_its_line() {
    given().an_open_path_from_$(0, 0)._to_$(200, 0);
    when().the_user_checks_if_point_$_is_contained(100, 0);
    then().the_result_is(true);
  }

  @Test
  void the_bounding_box_of_a_figure_encloses_all_its_nodes() {
    given().a_closed_square_figure_from_$(0, 0)._to_$(100, 100);
    when().the_user_requests_the_bounding_box();
    then().the_bounding_box_contains_point(0, 0).and().the_bounding_box_contains_point(100, 100);
  }

  @Test
  void closing_a_figure_changes_its_closed_state() {
    given().a_new_bezier_figure();
    when().the_user_closes_the_figure();
    then().the_figure_is_closed();
  }
}

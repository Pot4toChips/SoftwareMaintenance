package org.jhotdraw.draw.constrainer.bdd;

import com.tngtech.jgiven.junit5.ScenarioTest;
import org.junit.jupiter.api.Test;

public class SnapToGridTest
    extends ScenarioTest<GivenAGrid, WhenAPointIsConstrained, ThenThePoint> {

  @Test
  void an_off_grid_point_snaps_to_the_nearest_cell() {
    given().a_grid_with_cell_size_$_x_$(10.0, 10.0);
    when().the_point_$_$_is_constrained(12.0, 18.0);
    then().it_snaps_to_$_$(10.0, 20.0);
  }

  @Test
  void a_point_already_on_the_grid_does_not_move() {
    given().a_grid_with_cell_size_$_x_$(10.0, 10.0);
    when().the_point_$_$_is_constrained(20.0, 20.0);
    then().it_snaps_to_$_$(20.0, 20.0);
  }

  @Test
  void a_negative_point_snaps_to_the_nearest_cell() {
    given().a_grid_with_cell_size_$_x_$(10.0, 10.0);
    when().the_point_$_$_is_constrained(-12.0, -18.0);
    then().it_snaps_to_$_$(-10.0, -20.0);
  }
}

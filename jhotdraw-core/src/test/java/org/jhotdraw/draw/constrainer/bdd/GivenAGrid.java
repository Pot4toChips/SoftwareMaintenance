package org.jhotdraw.draw.constrainer.bdd;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.jhotdraw.draw.constrainer.GridConstrainer;

public class GivenAGrid extends Stage<GivenAGrid> {

  @ScenarioState
  GridConstrainer grid;

  public GivenAGrid a_grid_with_cell_size_$_x_$(double width, double height) {
    grid = new GridConstrainer(width, height);
    return self();
  }
}

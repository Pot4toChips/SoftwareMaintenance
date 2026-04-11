package org.jhotdraw.draw.constrainer.bdd;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import java.awt.geom.Point2D;
import org.jhotdraw.draw.constrainer.GridConstrainer;

public class WhenAPointIsConstrained extends Stage<WhenAPointIsConstrained> {

  @ScenarioState
  GridConstrainer grid;

  @ScenarioState
  Point2D.Double result;

  public WhenAPointIsConstrained the_point_$_$_is_constrained(double x, double y) {
    result = grid.constrainPoint(new Point2D.Double(x, y));
    return self();
  }
}

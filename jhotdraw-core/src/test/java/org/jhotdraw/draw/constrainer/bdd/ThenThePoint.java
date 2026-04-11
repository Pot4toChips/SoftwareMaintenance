package org.jhotdraw.draw.constrainer.bdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import java.awt.geom.Point2D;

public class ThenThePoint extends Stage<ThenThePoint> {

  private static final double TOLERANCE = 0.0001;

  @ScenarioState
  Point2D.Double result;

  public ThenThePoint it_snaps_to_$_$(double x, double y) {
    assertThat(result.x).isCloseTo(x, within(TOLERANCE));
    assertThat(result.y).isCloseTo(y, within(TOLERANCE));
    return self();
  }
}

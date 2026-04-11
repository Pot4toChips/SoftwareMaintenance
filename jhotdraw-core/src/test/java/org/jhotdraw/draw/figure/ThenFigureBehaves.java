package org.jhotdraw.draw.figure;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.utils.geom.path.BezierPath;

class ThenFigureBehaves extends Stage<ThenFigureBehaves> {

  @ExpectedScenarioState
  BezierFigure figure;

  @ExpectedScenarioState
  boolean containsResult;

  @ExpectedScenarioState
  Rectangle2D.Double bounds;

  ThenFigureBehaves the_figure_has_$_nodes(int expected) {
    assertThat(figure.getNodeCount()).isEqualTo(expected);
    return self();
  }

  ThenFigureBehaves the_node_at_index_$_has_coordinates_$(int index, double x, double y) {
    BezierPath.Node node = figure.getNode(index);
    assertThat(node.x[0]).isEqualTo(x);
    assertThat(node.y[0]).isEqualTo(y);
    return self();
  }

  ThenFigureBehaves the_result_is(boolean expected) {
    assertThat(containsResult).isEqualTo(expected);
    return self();
  }

  ThenFigureBehaves the_bounding_box_contains_point(double x, double y) {
    assertThat(bounds.x).isLessThanOrEqualTo(x);
    assertThat(bounds.y).isLessThanOrEqualTo(y);
    assertThat(bounds.x + bounds.width).isGreaterThanOrEqualTo(x);
    assertThat(bounds.y + bounds.height).isGreaterThanOrEqualTo(y);
    return self();
  }

  ThenFigureBehaves the_figure_is_closed() {
    assertThat(figure.isClosed()).isTrue();
    return self();
  }
}

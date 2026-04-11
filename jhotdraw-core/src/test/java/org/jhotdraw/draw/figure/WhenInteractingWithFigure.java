package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.utils.geom.path.BezierPath;

class WhenInteractingWithFigure extends Stage<WhenInteractingWithFigure> {

  @ExpectedScenarioState
  BezierFigure figure;

  @ProvidedScenarioState
  boolean containsResult;

  @ProvidedScenarioState
  Rectangle2D.Double bounds;

  WhenInteractingWithFigure the_user_adds_a_node_at(double x, double y) {
    figure.addNode(new BezierPath.Node(x, y));
    return self();
  }

  WhenInteractingWithFigure _to_$(double x2, double y2) {
    figure.addNode(new BezierPath.Node(x2, 0));
    figure.addNode(new BezierPath.Node(x2, y2));
    figure.addNode(new BezierPath.Node(0, y2));
    figure.setClosed(true);
    return self();
  }

  WhenInteractingWithFigure the_user_checks_if_point_$_is_contained(double x, double y) {
    containsResult = figure.contains(new Point2D.Double(x, y), 1.0);
    return self();
  }

  WhenInteractingWithFigure the_user_requests_the_bounding_box() {
    bounds = figure.getBounds(1.0);
    return self();
  }

  WhenInteractingWithFigure the_user_closes_the_figure() {
    figure.setClosed(true);
    return self();
  }
}

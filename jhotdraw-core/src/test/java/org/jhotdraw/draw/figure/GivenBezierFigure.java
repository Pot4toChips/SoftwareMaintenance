package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.jhotdraw.utils.geom.path.BezierPath;

class GivenBezierFigure extends Stage<GivenBezierFigure> {

  @ProvidedScenarioState
  BezierFigure figure;

  private double x1Stored, y1Stored;

  GivenBezierFigure a_new_bezier_figure() {
    figure = new BezierFigure();
    return self();
  }

  GivenBezierFigure a_closed_square_figure_from_$(double x1, double y1) {
    x1Stored = x1;
    y1Stored = y1;
    figure = new BezierFigure();
    figure.addNode(new BezierPath.Node(x1, y1));
    return self();
  }

  GivenBezierFigure _to_$(double x2, double y2) {
    figure.addNode(new BezierPath.Node(x2, y1Stored));
    figure.addNode(new BezierPath.Node(x2, y2));
    figure.addNode(new BezierPath.Node(x1Stored, y2));
    figure.setClosed(true);
    return self();
  }

  GivenBezierFigure an_open_path_from_$(double x1, double y1) {
    figure = new BezierFigure();
    figure.addNode(new BezierPath.Node(x1, y1));
    return self();
  }
}

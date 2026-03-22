package org.jhotdraw.draw.constrainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.draw.DrawingView;
import org.junit.jupiter.api.Test;

public class GridConstrainerTest {

  private static final double DELTA = 0.0001;

  @Test
  void constrainPointSnapsToNearestCell() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Point2D.Double result = grid.constrainPoint(new Point2D.Double(12, 18));
    assertEquals(10, result.x, DELTA);
    assertEquals(20, result.y, DELTA);
  }

  @Test
  void constrainPointLeavesPointAlreadyOnGrid() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Point2D.Double result = grid.constrainPoint(new Point2D.Double(20, 20));
    assertEquals(20, result.x, DELTA);
    assertEquals(20, result.y, DELTA);
  }

  @Test
  void constrainPointRoundsHalfCellUpwards() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Point2D.Double result = grid.constrainPoint(new Point2D.Double(15, 25));
    assertEquals(20, result.x, DELTA);
    assertEquals(30, result.y, DELTA);
  }

  @Test
  void constrainPointHandlesNegativeCoordinates() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Point2D.Double result = grid.constrainPoint(new Point2D.Double(-12, -18));
    assertEquals(-10, result.x, DELTA);
    assertEquals(-20, result.y, DELTA);
  }

  @Test
  void constrainAngleReturnsAngleUnchangedWhenThetaIsZero() {
    GridConstrainer grid = new GridConstrainer();
    assertEquals(1.234, grid.constrainAngle(1.234), DELTA);
  }

  @Test
  void constrainAngleSnapsToNearestThetaStep() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    double theta = grid.getTheta();
    assertEquals(theta, grid.constrainAngle(theta * 0.9), DELTA);
  }

  @Test
  void constrainRectangleSnapsCornerToGrid() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Rectangle2D.Double result = grid.constrainRectangle(new Rectangle2D.Double(11, 19, 30, 30));
    assertEquals(10, result.x, DELTA);
    assertEquals(20, result.y, DELTA);
    assertEquals(30, result.width, DELTA);
    assertEquals(30, result.height, DELTA);
  }

  @Test
  void translatePointMovesEastByOneCell() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    Point2D.Double result =
        grid.translatePoint(new Point2D.Double(12, 18), TranslationDirection.EAST);
    assertEquals(20, result.x, DELTA);
    assertEquals(18, result.y, DELTA);
  }

  @Test
  void constrainPointFailsInvariantWhenWidthNotPositive() {
    GridConstrainer grid = new GridConstrainer(10, 10);
    grid.setWidth(0);
    assertThrows(AssertionError.class, () -> grid.constrainPoint(new Point2D.Double(5, 5)));
  }

  @Test
  void drawDoesNothingWhenGridIsNotVisible() {
    GridConstrainer grid = new GridConstrainer(10, 10, false);
    DrawingView view = mock(DrawingView.class);
    Graphics2D g = mock(Graphics2D.class);
    grid.draw(g, view);
    verifyNoInteractions(view);
  }

  @Test
  void drawQueriesViewAndRendersLinesWhenVisible() {
    GridConstrainer grid = new GridConstrainer(10, 10, true);
    DrawingView view = mock(DrawingView.class);
    Graphics2D g = mock(Graphics2D.class);
    when(g.getClipBounds()).thenReturn(new Rectangle(0, 0, 100, 100));
    when(view.viewToDrawing(any(Rectangle.class))).thenReturn(new Rectangle2D.Double(0, 0, 100, 100));
    when(view.getDrawingToViewTransform()).thenReturn(new AffineTransform());
    when(view.getScaleFactor()).thenReturn(1.0);
    grid.draw(g, view);
    verify(view).getScaleFactor();
    verify(g, atLeastOnce()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
  }
}

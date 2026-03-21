package org.jhotdraw.draw.figure;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.utils.geom.path.BezierPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BezierFigureTest {

  private BezierFigure figure;

  @BeforeEach
  void setUp() {
    figure = new BezierFigure();
  }

  @Test
  void getBounds_singleNode_returnsDegenerateBounds() {
    figure.addNode(new BezierPath.Node(10, 20));
    Rectangle2D.Double bounds = figure.getBounds(1.0);
    assertNotNull(bounds);
    assertEquals(10.0, bounds.x, 1e-6);
    assertEquals(20.0, bounds.y, 1e-6);
  }

  @Test
  void getBounds_twoNodes_enclosesEndpoints() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(100, 50));
    Rectangle2D.Double bounds = figure.getBounds(1.0);
    assertTrue(bounds.x <= 0);
    assertTrue(bounds.y <= 0);
    assertTrue(bounds.x + bounds.width >= 100);
    assertTrue(bounds.y + bounds.height >= 50);
  }

  @Test
  void getBounds_scaleParameterIgnored_sameResultForDifferentScales() {
    figure.addNode(new BezierPath.Node(5, 5));
    figure.addNode(new BezierPath.Node(15, 15));
    Rectangle2D.Double b1 = figure.getBounds(1.0);
    Rectangle2D.Double b2 = figure.getBounds(2.0);
    assertEquals(b1.x, b2.x, 1e-6);
    assertEquals(b1.y, b2.y, 1e-6);
    assertEquals(b1.width, b2.width, 1e-6);
    assertEquals(b1.height, b2.height, 1e-6);
  }

  @Test
  void getNodeCount_emptyFigure_isZero() {
    assertEquals(0, figure.getNodeCount());
  }

  @Test
  void addNode_incrementsNodeCount() {
    figure.addNode(new BezierPath.Node(0, 0));
    assertEquals(1, figure.getNodeCount());
    figure.addNode(new BezierPath.Node(1, 1));
    assertEquals(2, figure.getNodeCount());
  }

  @Test
  void getNode_returnsCorrectCoordinates() {
    figure.addNode(new BezierPath.Node(3.0, 7.0));
    BezierPath.Node node = figure.getNode(0);
    assertEquals(3.0, node.x[0], 1e-6);
    assertEquals(7.0, node.y[0], 1e-6);
  }

  @Test
  void isClosed_default_isFalse() {
    assertFalse(figure.isClosed());
  }

  @Test
  void setClosed_true_figureReportsClosed() {
    figure.setClosed(true);
    assertTrue(figure.isClosed());
  }

  @Test
  void setClosed_toggleBackToFalse_figureReportsOpen() {
    figure.setClosed(true);
    figure.setClosed(false);
    assertFalse(figure.isClosed());
  }

  @Test
  void contains_closedFigureInsidePoint_returnsTrue() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(100, 0));
    figure.addNode(new BezierPath.Node(100, 100));
    figure.addNode(new BezierPath.Node(0, 100));
    figure.setClosed(true);

    assertTrue(figure.contains(new Point2D.Double(50, 50), 1.0));
  }

  @Test
  void contains_closedFigureOutsidePoint_returnsFalse() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(100, 0));
    figure.addNode(new BezierPath.Node(100, 100));
    figure.addNode(new BezierPath.Node(0, 100));
    figure.setClosed(true);

    assertFalse(figure.contains(new Point2D.Double(500, 500), 1.0));
  }

  @Test
  void contains_openPathPointOnLine_returnsTrue() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(200, 0));

    assertTrue(figure.contains(new Point2D.Double(100, 0), 1.0));
  }

  @Test
  void contains_openPathPointFarOff_returnsFalse() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(200, 0));

    assertFalse(figure.contains(new Point2D.Double(100, 500), 1.0));
  }

  @Test
  void getBezierPath_returnsClone_mutatingDoesNotAffectFigure() {
    figure.addNode(new BezierPath.Node(10, 10));
    BezierPath clone = figure.getBezierPath();
    clone.add(new BezierPath.Node(99, 99));
    assertEquals(1, figure.getNodeCount());
  }

  @Test
  void setBezierPath_replacesNodes() {
    figure.addNode(new BezierPath.Node(0, 0));
    BezierPath newPath = new BezierPath();
    newPath.add(new BezierPath.Node(5, 5));
    newPath.add(new BezierPath.Node(6, 6));
    figure.setBezierPath(newPath);
    assertEquals(2, figure.getNodeCount());
  }

  @Test
  void invariant_nodeCountNeverNegative() {
    assert figure.getNodeCount() >= 0 : "nodeCount must never be negative";
    assertEquals(0, figure.getNodeCount());
  }

  @Test
  void invariant_boundsNeverNull() {
    figure.addNode(new BezierPath.Node(0, 0));
    Rectangle2D.Double b = figure.getBounds(1.0);
    assert b != null : "getBounds must never return null";
    assertNotNull(b);
  }

  @Test
  void getBounds_noNodes_returnsEmptyOrZeroBounds() {
    assertDoesNotThrow(() -> figure.getBounds(1.0));
  }

  @Test
  void contains_emptyFigure_returnsFalse() {
    assertFalse(figure.contains(new Point2D.Double(0, 0), 1.0));
  }

  @Test
  void contains_singleNode_closedFigure_returnsFalse() {
    figure.addNode(new BezierPath.Node(50, 50));
    figure.setClosed(true);
    assertFalse(figure.contains(new Point2D.Double(500, 500), 1.0));
  }

  @Test
  void contains_pointExactlyOnBoundary_returnsTrue() {
    figure.addNode(new BezierPath.Node(0, 0));
    figure.addNode(new BezierPath.Node(100, 0));
    figure.addNode(new BezierPath.Node(100, 100));
    figure.addNode(new BezierPath.Node(0, 100));
    figure.setClosed(true);

    assertTrue(figure.contains(new Point2D.Double(0, 0), 1.0));
  }
}

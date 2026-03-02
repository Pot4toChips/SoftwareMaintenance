/*
 * @(#)BezierControlPointHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.event.BezierNodeEdit;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.path.BezierPath;
import org.jhotdraw.utils.undo.CompositeEdit;
import org.jhotdraw.utils.util.*;

/** A {@link Handle} which allows to interactively change a control point of a bezier path. */
public class BezierControlPointHandle extends AbstractHandle {

  protected int index;
  protected int controlPointIndex;
  private CompositeEdit edit;
  private Figure transformOwner;
  private BezierPath.Node oldNode;

  public BezierControlPointHandle(BezierFigure owner, int index, int coord) {
    this(owner, index, coord, owner);
  }

  public BezierControlPointHandle(BezierFigure owner, int index, int coord, Figure transformOwner) {
    super(owner);
    this.index = index;
    this.controlPointIndex = coord;
    this.transformOwner = transformOwner;
    transformOwner.addFigureListener(FIGURE_LISTENER);
  }

  @Override
  public void dispose() {
    super.dispose();
    transformOwner.removeFigureListener(FIGURE_LISTENER);
    transformOwner = null;
  }

  protected BezierFigure getBezierFigure() {
    return getOwner();
  }

  protected Figure getTransformOwner() {
    return transformOwner;
  }

  @Override
  protected Point2D.Double getDrawingLocation() {
    if (getBezierFigure().getNodeCount() > index) {
      Point2D.Double p = getBezierFigure().getPoint(index, controlPointIndex);
      if (getTransformOwner().attr().get(TRANSFORM) != null) {
        getTransformOwner().attr().get(TRANSFORM).transform(p, p);
      }
      return p;
    } else {
      return null;
    }
  }

  protected BezierPath.Node getBezierNode() {
    return getBezierFigure().getNodeCount() > index ? getBezierFigure().getNode(index) : null;
  }

  private record HandleDrawStyle(
      Color fillColor,
      Color strokeColor,
      Stroke stroke1,
      Color strokeColor1,
      Stroke stroke2,
      Color strokeColor2) {}

  private HandleDrawStyle resolveHandleDrawStyle() {
    if (getEditor().getTool().supportsHandleInteraction()) {
      return new HandleDrawStyle(
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR),
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_STROKE_1),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_COLOR_1),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_STROKE_2),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_COLOR_2));
    } else {
      return new HandleDrawStyle(
          getEditor()
              .getHandleAttribute(
                  HandleAttributeKeys.BEZIER_CONTROL_POINT_HANDLE_FILL_COLOR_DISABLED),
          getEditor()
              .getHandleAttribute(
                  HandleAttributeKeys.BEZIER_CONTROL_POINT_HANDLE_STROKE_COLOR_DISABLED),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_STROKE_1_DISABLED),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_COLOR_1_DISABLED),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_STROKE_2_DISABLED),
          getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_TANGENT_COLOR_2_DISABLED));
    }
  }

  private void drawTangentLine(
      Graphics2D g, Stroke stroke, Color color, Point2D.Double p0, Point2D.Double pc) {
    if (stroke != null && color != null) {
      g.setStroke(stroke);
      g.setColor(color);
      g.draw(new Line2D.Double(view.drawingToView(p0), view.drawingToView(pc)));
    }
  }

  /** Draws this handle. */
  @Override
  public void draw(Graphics2D g) {
    BezierFigure f = getBezierFigure();
    if (f.getNodeCount() > index) {
      BezierPath.Node v = f.getNode(index);
      Point2D.Double p0 = new Point2D.Double(v.x[0], v.y[0]);
      Point2D.Double pc = new Point2D.Double(v.x[controlPointIndex], v.y[controlPointIndex]);
      Figure tOwner = getTransformOwner();
      if (tOwner.attr().get(TRANSFORM) != null) {
        tOwner.attr().get(TRANSFORM).transform(p0, p0);
        tOwner.attr().get(TRANSFORM).transform(pc, pc);
      }
      HandleDrawStyle style = resolveHandleDrawStyle();
      drawTangentLine(g, style.stroke1(), style.strokeColor1(), p0, pc);
      drawTangentLine(g, style.stroke2(), style.strokeColor2(), p0, pc);
      if (v.keepColinear
          && v.mask == BezierPath.C1C2_MASK
          && (index > 0 && index < f.getNodeCount() - 1 || f.isClosed())) {
        drawCircle(g, style.strokeColor(), style.fillColor());
      } else {
        drawCircle(g, style.fillColor(), style.strokeColor());
      }
    }
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    BezierFigure figure = getOwner();
    edit = new CompositeEdit("Punkt verschieben");
    view.getDrawing().fireUndoableEditHappened(edit);
    oldNode = figure.getNode(index);
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    BezierFigure figure = getBezierFigure();
    Point2D.Double p = view.getConstrainer() == null
        ? view.viewToDrawing(lead)
        : view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
    BezierPath.Node v = figure.getNode(index);
    fireAreaInvalidated(v);
    figure.willChange();
    Figure tOwner = getTransformOwner();
    if (tOwner.attr().get(TRANSFORM) != null) {
      try {
        tOwner.attr().get(TRANSFORM).inverseTransform(p, p);
      } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
      }
    }
    if (!v.keepColinear) {
      // move control point independently
      figure.setPoint(index, controlPointIndex, p);
    } else {
      // move control point and opposite control point on same line
      double a = Math.PI + Math.atan2(p.y - v.y[0], p.x - v.x[0]);
      int c2 = (controlPointIndex == 1) ? 2 : 1;
      double r = Math.sqrt(
          (v.x[c2] - v.x[0]) * (v.x[c2] - v.x[0]) + (v.y[c2] - v.y[0]) * (v.y[c2] - v.y[0]));
      double sina = Math.sin(a);
      double cosa = Math.cos(a);
      Point2D.Double p2 = new Point2D.Double(r * cosa + v.x[0], r * sina + v.y[0]);
      figure.setPoint(index, controlPointIndex, p);
      figure.setPoint(index, c2, p2);
    }
    figure.changed();
    fireAreaInvalidated(figure.getNode(index));
  }

  private void fireAreaInvalidated(BezierPath.Node v) {
    Rectangle2D.Double dr = new Rectangle2D.Double(v.x[0], v.y[0], 0, 0);
    for (int i = 1; i < 3; i++) {
      dr.add(v.x[i], v.y[i]);
    }
    Rectangle vr = view.drawingToView(dr);
    vr.grow(getHandlesize(), getHandlesize());
    fireAreaInvalidated(vr);
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    final BezierFigure figure = getBezierFigure();
    BezierPath.Node oldValue = (BezierPath.Node) oldNode.clone();
    BezierPath.Node newValue = figure.getNode(index);
    if ((modifiersEx
            & (InputEvent.META_DOWN_MASK
                | InputEvent.CTRL_DOWN_MASK
                | InputEvent.ALT_DOWN_MASK
                | InputEvent.SHIFT_DOWN_MASK))
        != 0) {
      figure.willChange();
      newValue.keepColinear = !newValue.keepColinear;
      if (newValue.keepColinear) {
        // move control point and opposite control point on same line
        Point2D.Double p = figure.getPoint(index, controlPointIndex);
        double a = Math.PI + Math.atan2(p.y - newValue.y[0], p.x - newValue.x[0]);
        int c2 = (controlPointIndex == 1) ? 2 : 1;
        double r = Math.sqrt((newValue.x[c2] - newValue.x[0]) * (newValue.x[c2] - newValue.x[0])
            + (newValue.y[c2] - newValue.y[0]) * (newValue.y[c2] - newValue.y[0]));
        double sina = Math.sin(a);
        double cosa = Math.cos(a);
        Point2D.Double p2 = new Point2D.Double(r * cosa + newValue.x[0], r * sina + newValue.y[0]);
        newValue.x[c2] = p2.x;
        newValue.y[c2] = p2.y;
      }
      figure.setNode(index, newValue);
      figure.changed();
    }
    view.getDrawing()
        .fireUndoableEditHappened(new BezierNodeEdit(figure, index, oldValue, newValue) {
          private static final long serialVersionUID = 1L;

          @Override
          public void redo() throws CannotRedoException {
            super.redo();
            fireHandleRequestSecondaryHandles();
          }

          @Override
          public void undo() throws CannotUndoException {
            super.undo();
            fireHandleRequestSecondaryHandles();
          }
        });
    view.getDrawing().fireUndoableEditHappened(edit);
  }

  @Override
  public boolean isCombinableWith(Handle h) {
    if (super.isCombinableWith(h)) {
      BezierControlPointHandle that = (BezierControlPointHandle) h;
      return that.index == this.index
          && that.controlPointIndex == this.controlPointIndex
          && that.getBezierFigure().getNodeCount() == this.getBezierFigure().getNodeCount();
    }
    return false;
  }

  @Override
  public String getToolTipText(Point p) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    BezierPath.Node node = getBezierNode();
    if (node == null) {
      return null;
    }
    if (node.mask == BezierPath.C1C2_MASK) {
      return labels.getFormatted(
          "handle.bezierControlPoint.toolTipText",
          labels.getFormatted(
              node.keepColinear
                  ? "handle.bezierControlPoint.cubicColinear.value"
                  : "handle.bezierControlPoint.cubicUnconstrained.value"));
    } else {
      return labels.getString("handle.bezierControlPoint.quadratic.toolTipText");
    }
  }

  @Override
  public BezierFigure getOwner() {
    return (BezierFigure) super.getOwner();
  }

  @Override
  public void keyPressed(KeyEvent evt) {
    final BezierFigure f = getOwner();
    BezierPath.Node pressedNode = f.getNode(index);
    switch (evt.getKeyCode()) {
      case KeyEvent.VK_UP:
        f.willChange();
        f.setPoint(
            index,
            controlPointIndex,
            new Point2D.Double(
                pressedNode.x[controlPointIndex], pressedNode.y[controlPointIndex] - 1d));
        f.changed();
        view.getDrawing()
            .fireUndoableEditHappened(new BezierNodeEdit(f, index, pressedNode, f.getNode(index)));
        evt.consume();
        break;
      case KeyEvent.VK_DOWN:
        f.willChange();
        f.setPoint(
            index,
            controlPointIndex,
            new Point2D.Double(
                pressedNode.x[controlPointIndex], pressedNode.y[controlPointIndex] + 1d));
        f.changed();
        view.getDrawing()
            .fireUndoableEditHappened(new BezierNodeEdit(f, index, pressedNode, f.getNode(index)));
        evt.consume();
        break;
      case KeyEvent.VK_LEFT:
        f.willChange();
        f.setPoint(
            index,
            controlPointIndex,
            new Point2D.Double(
                pressedNode.x[controlPointIndex] - 1d, pressedNode.y[controlPointIndex]));
        f.changed();
        view.getDrawing()
            .fireUndoableEditHappened(new BezierNodeEdit(f, index, pressedNode, f.getNode(index)));
        evt.consume();
        break;
      case KeyEvent.VK_RIGHT:
        f.willChange();
        f.setPoint(
            index,
            controlPointIndex,
            new Point2D.Double(
                pressedNode.x[controlPointIndex] + 1d, pressedNode.y[controlPointIndex]));
        f.changed();
        view.getDrawing()
            .fireUndoableEditHappened(new BezierNodeEdit(f, index, pressedNode, f.getNode(index)));
        evt.consume();
        break;
      case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE:
        evt.consume();
        break;
      default:
        break;
    }
  }
}

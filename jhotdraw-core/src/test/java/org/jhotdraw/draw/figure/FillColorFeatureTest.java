package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.undo.UndoableEdit;
import org.junit.jupiter.api.Test;

public class FillColorFeatureTest {

  @Test
  void setFillColorStoresSelectedColorOnFigure() {
    RectangleFigure figure = new RectangleFigure(0, 0, 20, 20);

    FILL_COLOR.set(figure, Color.BLUE);

    assertEquals(Color.BLUE, figure.attr().get(FILL_COLOR));
  }

  @Test
  void undoableFillColorChangeRestoresAndReappliesColor() {
    RectangleFigure figure = new RectangleFigure(0, 0, 20, 20);
    FILL_COLOR.set(figure, Color.RED);

    UndoableEdit edit = FILL_COLOR.setUndoable(figure, Color.BLUE);

    assertEquals(Color.BLUE, figure.attr().get(FILL_COLOR));
    edit.undo();
    assertEquals(Color.RED, figure.attr().get(FILL_COLOR));
    edit.redo();
    assertEquals(Color.BLUE, figure.attr().get(FILL_COLOR));
  }

  @Test
  void drawingFigureUsesFillColorInsideShape() {
    RectangleFigure figure = new RectangleFigure(0, 0, 20, 20);
    FILL_COLOR.set(figure, Color.BLUE);
    STROKE_COLOR.set(figure, null);

    BufferedImage image = drawFigure(figure);

    assertEquals(Color.BLUE.getRGB(), image.getRGB(10, 10));
  }

  @Test
  void nullFillColorDoesNotPaintFigureInterior() {
    RectangleFigure figure = new RectangleFigure(0, 0, 20, 20);
    FILL_COLOR.set(figure, null);
    STROKE_COLOR.set(figure, null);

    BufferedImage image = drawFigure(figure);

    assertEquals(0, image.getRGB(10, 10));
  }

  private BufferedImage drawFigure(RectangleFigure figure) {
    BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    figure.draw(graphics);
    graphics.dispose();
    return image;
  }
}

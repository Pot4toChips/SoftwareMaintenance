/**
 * @(#)SelectionColorChooserHandler.java
 *
 * <p>Copyright (c) 2010 The authors and contributors of JHotDraw. You may not use, copy or modify
 * this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;

/** SelectionColorChooserHandler. */
public class SelectionColorChooserHandler extends AbstractSelectedAction implements ChangeListener {

  private static final long serialVersionUID = 1L;
  protected AttributeKey<Color> key;
  protected JColorChooser colorChooser;
  protected JPopupMenu popupMenu;
  protected int isUpdating;

  public SelectionColorChooserHandler(
      DrawingEditor editor,
      AttributeKey<Color> key,
      JColorChooser colorChooser,
      JPopupMenu popupMenu) {
    super(editor);
    this.key = key;
    this.colorChooser = colorChooser;
    this.popupMenu = popupMenu;
    colorChooser.getSelectionModel().addChangeListener(this);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent evt) {
    popupMenu.setVisible(false);
  }

  protected void applySelectedColorToFigures() {
    final ArrayList<Figure> selectedFigures = getSelectedFigures();
    final ArrayList<Object> restoreData = collectRestoreData(selectedFigures);
    Color selectedColor = getSelectedColor();
    applyColorToFigures(selectedFigures, selectedColor);
    getEditor().setDefaultAttribute(key, selectedColor);
    fireUndoableEditHappened(createColorChangeEdit(selectedFigures, restoreData, selectedColor));
  }

  private ArrayList<Figure> getSelectedFigures() {
    return new ArrayList<>(getView().getSelectedFigures());
  }

  private ArrayList<Object> collectRestoreData(ArrayList<Figure> selectedFigures) {
    final ArrayList<Object> restoreData = new ArrayList<>(selectedFigures.size());
    for (Figure figure : selectedFigures) {
      restoreData.add(figure.attr().getAttributesRestoreData());
    }
    return restoreData;
  }

  private Color getSelectedColor() {
    Color selectedColor = colorChooser.getColor();
    return selectedColor != null && selectedColor.getAlpha() == 0 ? null : selectedColor;
  }

  private void applyColorToFigures(ArrayList<Figure> selectedFigures, Color selectedColor) {
    for (Figure figure : selectedFigures) {
      figure.willChange();
      figure.attr().set(key, selectedColor);
      figure.changed();
    }
  }

  private UndoableEdit createColorChangeEdit(
      final ArrayList<Figure> selectedFigures,
      final ArrayList<Object> restoreData,
      final Color undoValue) {
    UndoableEdit edit = new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        return AttributeKeys.FONT_FACE.getPresentationName();
      }

      @Override
      public void undo() {
        super.undo();
        Iterator<Object> iRestore = restoreData.iterator();
        for (Figure figure : selectedFigures) {
          figure.willChange();
          figure.attr().restoreAttributesTo(iRestore.next());
          figure.changed();
        }
      }

      @Override
      public void redo() {
        super.redo();
        applyColorToFigures(selectedFigures, undoValue);
      }
    };
    return edit;
  }

  @Override
  protected void updateEnabledState() {
    setEnabled(getEditor().isEnabled());
    if (getView() != null && colorChooser != null && popupMenu != null) {
      colorChooser.setEnabled(getView().getSelectionCount() > 0);
      popupMenu.setEnabled(getView().getSelectionCount() > 0);
      isUpdating++;
      if (getView().getSelectionCount() > 0) {
        for (Figure f : getView().getSelectedFigures()) {
          Color figureColor = f.attr().get(key);
          colorChooser.setColor(figureColor == null ? new Color(0, true) : figureColor);
          break;
        }
      }
      isUpdating--;
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (isUpdating++ == 0) {
      applySelectedColorToFigures();
    }
    isUpdating--;
  }
}

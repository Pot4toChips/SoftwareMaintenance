package org.jhotdraw.draw.figure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit5.ScenarioTest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;

class FillColorFeatureBddTest
    extends ScenarioTest<
        FillColorFeatureBddTest.GivenSelectedFigures,
        FillColorFeatureBddTest.WhenFillColorIsChanged,
        FillColorFeatureBddTest.ThenFiguresShowTheFillColor> {

  @Test
  void selected_figure_visually_changes_to_the_chosen_fill_color() {
    given().a_selected_rectangle_figure();

    when().the_user_applies_the_fill_color(Color.BLUE);

    then()
        .the_selected_figure_stores_the_fill_color(Color.BLUE)
        .and()
        .the_selected_figure_is_drawn_with_the_fill_color(Color.BLUE);
  }

  @Test
  void selected_figures_can_receive_multiple_different_fill_colors() {
    given().a_selected_rectangle_figure();

    when()
        .the_user_applies_the_fill_color(Color.BLUE)
        .and()
        .the_user_applies_the_fill_color(Color.GREEN);

    then()
        .the_selected_figure_stores_the_fill_color(Color.GREEN)
        .and()
        .the_selected_figure_is_drawn_with_the_fill_color(Color.GREEN);
  }

  @Test
  void several_selected_figures_change_to_the_same_fill_color() {
    given().two_selected_rectangle_figures();

    when().the_user_applies_the_fill_color(Color.ORANGE);

    then().all_selected_figures_store_the_fill_color(Color.ORANGE);
  }

  public static class GivenSelectedFigures extends Stage<GivenSelectedFigures> {

    @ScenarioState
    List<RectangleFigure> selectedFigures;

    public GivenSelectedFigures a_selected_rectangle_figure() {
      selectedFigures = List.of(rectangleFigure());
      return self();
    }

    public GivenSelectedFigures two_selected_rectangle_figures() {
      selectedFigures = List.of(rectangleFigure(), rectangleFigure());
      return self();
    }

    private RectangleFigure rectangleFigure() {
      RectangleFigure figure = new RectangleFigure(0, 0, 20, 20);
      STROKE_COLOR.set(figure, null);
      return figure;
    }
  }

  public static class WhenFillColorIsChanged extends Stage<WhenFillColorIsChanged> {

    @ScenarioState
    List<RectangleFigure> selectedFigures;

    public WhenFillColorIsChanged the_user_applies_the_fill_color(Color color) {
      selectedFigures.forEach(figure -> FILL_COLOR.set(figure, color));
      return self();
    }
  }

  public static class ThenFiguresShowTheFillColor extends Stage<ThenFiguresShowTheFillColor> {

    @ScenarioState
    List<RectangleFigure> selectedFigures;

    public ThenFiguresShowTheFillColor the_selected_figure_stores_the_fill_color(Color color) {
      assertThat(selectedFigures).hasSize(1);
      assertThat(selectedFigures.get(0).attr().get(FILL_COLOR)).isEqualTo(color);
      return self();
    }

    public ThenFiguresShowTheFillColor the_selected_figure_is_drawn_with_the_fill_color(
        Color color) {
      BufferedImage image = drawFigure(selectedFigures.get(0));
      assertThat(image.getRGB(10, 10)).isEqualTo(color.getRGB());
      return self();
    }

    public ThenFiguresShowTheFillColor all_selected_figures_store_the_fill_color(Color color) {
      assertThat(selectedFigures).hasSize(2);
      assertThat(selectedFigures)
          .allSatisfy(figure -> assertThat(figure.attr().get(FILL_COLOR)).isEqualTo(color));
      return self();
    }

    private BufferedImage drawFigure(RectangleFigure figure) {
      BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      figure.draw(graphics);
      graphics.dispose();
      return image;
    }
  }
}

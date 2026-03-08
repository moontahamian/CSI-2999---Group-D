package com.example.demo2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CalendarController {

    @FXML private Label calendar_text_month;
    @FXML private Label calendar_text_year;
    @FXML private Button calendar_button_left;
    @FXML private Button calendar_button_right;
    @FXML private GridPane calendar_grid;

    @FXML private VBox calendar_popup;
    @FXML private Button calendar_popup_button_x;
    @FXML private Label calendar_popup_date_label;
    @FXML private TextArea calendar_popup_title_textarea;
    @FXML private TextArea calendar_popup_notes_textarea;

    private LocalDate display_anchor;
    private String clicked_date;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    // INITIALIZE
    @FXML
    public void initialize() {

        display_anchor = LocalDate.now();
        calendar_popup.setVisible(false);

        // Close popup
        calendar_popup_button_x.setOnAction(event -> {

            Integer userId = Session.getCurrentUserId();
            if (userId == null) return;

            DButils.updateCalendarDate(
                    userId,
                    clicked_date,
                    calendar_popup_title_textarea.getText(),
                    calendar_popup_notes_textarea.getText()
            );

            calendar_popup.setVisible(false);
            draw_calendar(display_anchor);
        });

        // Navigation
        calendar_button_left.setOnAction(event -> button_left());
        calendar_button_right.setOnAction(event -> button_right());

        draw_calendar(display_anchor);
    }

    // DRAW CALENDAR
    public void draw_calendar(LocalDate date_anchor) {

        calendar_grid.getChildren().clear();

        calendar_text_month.setText(
                date_anchor.getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        );

        calendar_text_year.setText(String.valueOf(date_anchor.getYear()));

        int startOffset =
                date_anchor.withDayOfMonth(1)
                        .getDayOfWeek()
                        .getValue() % 7;

        LocalDate index_date =
                date_anchor.withDayOfMonth(1)
                        .minusDays(startOffset);

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {

                boolean inMonth =
                        index_date.getMonth() == date_anchor.getMonth();

                String formattedDate =
                        index_date.format(FORMATTER);

                DButils.CalendarEntry entry =
                        DButils.getCalendarData(userId, formattedDate);

                VBox cell =
                        draw_cell(index_date,
                                entry.getTitle(),
                                entry.getNotes(),
                                inMonth);

                calendar_grid.add(cell, col, row);
                index_date = index_date.plusDays(1);
            }
        }
    }

    // DRAW CELL
    private VBox draw_cell(LocalDate date,
                           String titleText,
                           String notesText,
                           boolean isInMonth) {

        VBox cell = new VBox();

        String idleColor = isInMonth ? "#ffffff" : "#f4f4f4";
        String hoverColor = isInMonth ? "#e0e0e0" : "#d1d1d1";

        cell.setStyle("-fx-background-color: " + idleColor +
                "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px;");

        cell.setOnMouseEntered(e ->
                cell.setStyle("-fx-background-color: " + hoverColor +
                        "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px; -fx-cursor: hand;")
        );

        cell.setOnMouseExited(e ->
                cell.setStyle("-fx-background-color: " + idleColor +
                        "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px;")
        );

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-font-weight: bold;");

        Label titleLabel = new Label(titleText);
        titleLabel.setWrapText(true);
        VBox.setVgrow(titleLabel, Priority.ALWAYS);

        cell.setOnMouseClicked(e -> {

            clicked_date = date.format(FORMATTER);

            Integer userId = Session.getCurrentUserId();
            if (userId == null) return;

            DButils.CalendarEntry entry =
                    DButils.getCalendarData(userId, clicked_date);

            calendar_popup_date_label.setText(clicked_date);
            calendar_popup_title_textarea.setText(entry.getTitle());
            calendar_popup_notes_textarea.setText(entry.getNotes());

            calendar_popup.setVisible(true);
        });

        cell.getChildren().addAll(dayLabel, titleLabel);
        return cell;
    }

    // NAVIGATION
    private void button_left() {
        display_anchor =
                display_anchor.minusMonths(1).withDayOfMonth(1);
        draw_calendar(display_anchor);
    }

    private void button_right() {
        display_anchor =
                display_anchor.plusMonths(1).withDayOfMonth(1);
        draw_calendar(display_anchor);
    }
}
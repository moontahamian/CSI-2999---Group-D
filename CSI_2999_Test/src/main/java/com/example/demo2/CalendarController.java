package com.example.demo2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    // ISO format used by applications (yyyy-MM-dd)
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {

        display_anchor = LocalDate.now();
        calendar_popup.setVisible(false);

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

        calendar_button_left.setOnAction(event -> button_left());
        calendar_button_right.setOnAction(event -> button_right());

        draw_calendar(display_anchor);
    }

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

        // Load all applications once for the whole calendar draw
        List<ApplicationModel> applications =
                DButils.getApplicationsByUser(userId);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {

                boolean inMonth =
                        index_date.getMonth() == date_anchor.getMonth();

                String formattedDate =
                        index_date.format(FORMATTER);

                DButils.CalendarEntry entry =
                        DButils.getCalendarData(userId, formattedDate);

                VBox cell = draw_cell(
                        index_date,
                        entry.getTitle(),
                        entry.getNotes(),
                        inMonth,
                        applications
                );

                calendar_grid.add(cell, col, row);
                index_date = index_date.plusDays(1);
            }
        }
    }

    private VBox draw_cell(LocalDate date,
                           String titleText,
                           String notesText,
                           boolean isInMonth,
                           List<ApplicationModel> applications) {

        VBox cell = new VBox(2);
        cell.setPadding(new Insets(4));

        // Background color based on in/out of month
        String idleColor = isInMonth ? "#ffffff" : "#f8fafc";
        String hoverColor = isInMonth ? "#E6E4FF" : "#ede9fe";

        cell.setStyle("-fx-background-color: " + idleColor +
                "; -fx-border-color: #e2e8f0; -fx-border-width: 0.5px;");

        cell.setOnMouseEntered(e ->
                cell.setStyle("-fx-background-color: " + hoverColor +
                        "; -fx-border-color: #e2e8f0; -fx-border-width: 0.5px; -fx-cursor: hand;")
        );

        cell.setOnMouseExited(e ->
                cell.setStyle("-fx-background-color: " + idleColor +
                        "; -fx-border-color: #e2e8f0; -fx-border-width: 0.5px;")
        );

        // Highlight today
        if (date.equals(LocalDate.now())) {
            cell.setStyle(
                    "-fx-background-color: #E6E4FF;" +
                            "-fx-border-color: #112c7e;" +
                            "-fx-border-width: 2px;"
            );
            cell.setOnMouseEntered(e ->
                    cell.setStyle(
                            "-fx-background-color: #d4d0ff;" +
                                    "-fx-border-color: #112c7e;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-cursor: hand;"
                    )
            );
            cell.setOnMouseExited(e ->
                    cell.setStyle(
                            "-fx-background-color: #E6E4FF;" +
                                    "-fx-border-color: #112c7e;" +
                                    "-fx-border-width: 2px;"
                    )
            );
        }

        // Day number
        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: " + (isInMonth ? "#1a1a2e" : "#94A3B8") + ";"
        );

        // Manual title
        Label titleLabel = new Label(titleText);
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #475569;");
        VBox.setVgrow(titleLabel, Priority.ALWAYS);

        cell.getChildren().addAll(dayLabel, titleLabel);

        // Application events
        String isoDate = date.format(ISO_FORMATTER);

        for (ApplicationModel app : applications) {

            if ("Interview Scheduled".equals(app.getStatus())
                    && isoDate.equals(app.getDateApplied())) {
                HBox dot = createEventDot(
                        "⬤ Interview: " + app.getCompanyName(), "#112c7e"
                );
                cell.getChildren().add(dot);
            }

            if (app.getDeadline() != null
                    && isoDate.equals(app.getDeadline())) {
                HBox dot = createEventDot(
                        "⬤ Deadline: " + app.getCompanyName(), "#EF4444"
                );
                cell.getChildren().add(dot);
            }
        }

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

        return cell;
    }

    // Creates a colored event label
    private HBox createEventDot(String text, String color) {
        HBox box = new HBox();
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 9px;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-font-weight: bold;"
        );
        label.setWrapText(true);
        box.getChildren().add(label);
        return box;
    }

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
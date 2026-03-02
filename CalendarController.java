package com.example.demo2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML private javafx.scene.control.TextArea calendar_popup_title_textarea;
    @FXML private javafx.scene.control.TextArea calendar_popup_notes_textarea;


//variables for the "default" month being shown on the calendar
    public LocalDate display_anchor;
    public String current_popup_date;
    public String clicked_date;

    
//loads on scene startup
    @FXML
    public void initialize(){
        display_anchor = LocalDate.now();
    //popup code
        calendar_popup.setVisible(false);

        calendar_popup_button_x.setOnAction(event -> {
            calendar_popup.setVisible(false); //closing popup menu
            String updated_title = calendar_popup_title_textarea.getText();
            String updated_notes = calendar_popup_notes_textarea.getText();
            
            DButils.update_calendar_date(DButils.current_user_id, clicked_date, updated_title, updated_notes);
            draw_calendar(display_anchor);

            System.out.println("Popup closed via X button.");
        });
    //left button
        calendar_button_left.setOnAction(event -> {
            button_left();
        });
        //hover stuff
        calendar_button_left.setOnMouseEntered(event -> {
            calendar_button_left.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #0f172a; -fx-border-radius: 4; -fx-border-width: 4; -fx-cursor: hand;");
        });
        calendar_button_left.setOnMouseExited(event -> {
            calendar_button_left.setStyle("-fx-background-color: white; -fx-border-color: #0f172a; -fx-border-radius: 4; -fx-border-width: 4;");
        });
    //right button
        calendar_button_right.setOnAction(event -> {
            button_right();
        });
        //hover stuff
        calendar_button_right.setOnMouseEntered(event -> {
            calendar_button_right.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #0f172a; -fx-border-radius: 4; -fx-border-width: 4; -fx-cursor: hand;");
        });
        calendar_button_right.setOnMouseExited(event -> {
            calendar_button_right.setStyle("-fx-background-color: white; -fx-border-color: #0f172a; -fx-border-radius: 4; -fx-border-width: 4;");
        });


        draw_calendar(display_anchor);
    }

//table creation methods
    public void draw_calendar(LocalDate date_anchor) {
        calendar_grid.getChildren().clear();
        
        String anchor_month = date_anchor.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        calendar_text_month.setText(anchor_month);
        String anchor_year = String.valueOf(date_anchor.getYear());
        calendar_text_year.setText(String.valueOf(anchor_year));

        int current_start_of_month = date_anchor.withDayOfMonth(1).getDayOfWeek().getValue() % 7; // % 7 makes it so 0 is sunday, etc. (lines up perfectly with grid)
        LocalDate index_date = date_anchor.withDayOfMonth(1).minusDays(current_start_of_month);


        
        for(int row = 0; row < 6; row++) {
            for(int column = 0; column < 7; column++) {
                boolean in_month_status = index_date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(anchor_month);

                DButils.Calendar_Entry entry = DButils.get_calendar_data(
                    DButils.current_user_id, 
                    index_date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                );
                
                VBox cell_pane = draw_cell(index_date, entry.title_text(), entry.notes_text(), in_month_status);
                calendar_grid.add(cell_pane, column, row);
                index_date = index_date.plusDays(1);
            }
        }
    }

    public VBox draw_cell(LocalDate date, String titles_text, String notes_text, boolean is_in_anchor_month) {
        VBox cell = new VBox();
    //hover indication stuff
        String idle_color = is_in_anchor_month ? "#ffffff" : "#f4f4f4";
        String hover_color = is_in_anchor_month ? "#e0e0e0" : "#d1d1d1";
    
        cell.setStyle("-fx-background-color: " + idle_color + "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px;");

        cell.setOnMouseEntered(event -> {
            cell.setStyle("-fx-background-color: " + hover_color + "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px; -fx-cursor: hand;");
        });

        cell.setOnMouseExited(event -> {
            cell.setStyle("-fx-background-color: " + idle_color + "; -fx-border-color: #dcdcdc; -fx-border-width: 0.2px;");
        });

    //Label initialization
        String day = String.valueOf(date.getDayOfMonth());

        Label label_day = new Label(day);
        label_day.setStyle("-fx-font-weight: bold;");

        Label label_title = new Label(titles_text);
        label_title.setStyle("-fx-font-weight: bold;");
        label_title.setWrapText(true);
        VBox.setVgrow(label_title, Priority.ALWAYS);

    //popup stuff
        cell.setOnMouseClicked(event -> {
            if (!calendar_popup.isVisible()) {
                System.out.println("the " + day + " cell was clicked" );
                calendar_popup.setVisible(true);
                String full_day = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                clicked_date = full_day;
                
                DButils.Calendar_Entry date_data = DButils.get_calendar_data(DButils.current_user_id, day);

                calendar_popup_date_label.setText(full_day);
                calendar_popup_title_textarea.setText(date_data.title_text());
                calendar_popup_notes_textarea.setText(date_data.notes_text());

            }
        });

        cell.getChildren().addAll(label_day, label_title);
        return cell;
    }
//updating the table methods


    public void button_left() {
        display_anchor = display_anchor.minusMonths(1).withDayOfMonth(1);
        draw_calendar(display_anchor);

    }

    public void button_right() {
        display_anchor = display_anchor.plusMonths(1).withDayOfMonth(1);
        draw_calendar(display_anchor);

    }

}

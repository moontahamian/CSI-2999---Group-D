package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ResumeController {

    @FXML private VBox resumeListContainer;

    @FXML
    public void initialize() {
        loadResumes();
    }

    @FXML
    private void uploadResume(ActionEvent event) {
        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Resume");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(resolveWindow(event));
        if (selectedFile == null) return;

        try {
            byte[] pdfData = Files.readAllBytes(selectedFile.toPath());
            DButils.saveResume(userId, selectedFile.getName(), pdfData);
            loadResumes();
        } catch (RuntimeException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not save the PDF to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not read the selected PDF.");
        }
    }

    private void loadResumes() {
        resumeListContainer.getChildren().clear();

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        List<DButils.ResumeFile> resumes = DButils.getResumesForUser(userId);

        if (resumes.isEmpty()) {
            Label empty = new Label("No resumes uploaded yet.");
            empty.getStyleClass().add("resume-empty-label");
            resumeListContainer.getChildren().add(empty);
            return;
        }

        for (DButils.ResumeFile resume : resumes) {
            resumeListContainer.getChildren().add(createResumeCard(resume));
        }
    }

    private HBox createResumeCard(DButils.ResumeFile resume) {

        HBox card = new HBox();
        card.setSpacing(10);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.getStyleClass().add("resume-card");



        // File name — click to open
        Label nameLabel = new Label(resume.getFileName());
        nameLabel.getStyleClass().add("resume-name");
        nameLabel.setOnMouseClicked(e -> openResume(resume.getPdfData(), resume.getFileName()));
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Download button
        Button downloadBtn = new Button("Download");
        downloadBtn.getStyleClass().add("btn-download");
        downloadBtn.setOnAction(e -> downloadResume(resume));

        // Delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            DButils.deleteResume(resume.getId());
            loadResumes();
        });

        card.getChildren().addAll(nameLabel, downloadBtn, deleteBtn);
        return card;
    }

    private void openResume(byte[] pdfData, String fileName) {
        try {
            File tempFile = File.createTempFile("resume_preview_", ".pdf");
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), pdfData);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            } else {
                showAlert(Alert.AlertType.ERROR, "Resume", "Cannot open PDF on this system.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not open the PDF.");
        }
    }

    private void downloadResume(DButils.ResumeFile resume) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Download Resume");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName(ensurePdfExtension(resume.getFileName()));

        File targetFile = fileChooser.showSaveDialog(null);
        if (targetFile == null) return;

        try {
            Files.write(
                    new File(ensurePdfExtension(targetFile.getAbsolutePath())).toPath(),
                    resume.getPdfData()
            );
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not save the PDF to disk.");
        }
    }

    private Window resolveWindow(ActionEvent event) {
        if (event != null && event.getSource() instanceof Node sourceNode) {
            return sourceNode.getScene().getWindow();
        }
        return null;
    }

    private String ensurePdfExtension(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf") ? fileName : fileName + ".pdf";
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
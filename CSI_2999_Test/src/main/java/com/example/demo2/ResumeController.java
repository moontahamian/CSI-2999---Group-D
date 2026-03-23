package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResumeController {

    @FXML private Label resumeNameLabel;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        refreshResumeStatus();
    }

    // upload button -> #uploadResume, download button -> #downloadResume.
    @FXML
    private void uploadResume(ActionEvent event) {
        Integer userId = requireCurrentUser();
        if (userId == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Resume");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(resolveWindow(event));
        if (selectedFile == null) {
            return;
        }

        if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
            showAlert(Alert.AlertType.ERROR, "Resume", "Only PDF files can be uploaded.");
            return;
        }

        try {
            byte[] pdfData = Files.readAllBytes(selectedFile.toPath());
            DButils.saveResume(userId, selectedFile.getName(), pdfData);
            refreshResumeStatus();
            updateStatus("Resume uploaded successfully.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not save the PDF to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not read the selected PDF.");
        }
    }

    @FXML
    private void downloadResume(ActionEvent event) {
        Integer userId = requireCurrentUser();
        if (userId == null) {
            return;
        }

        DButils.ResumeFile resumeFile = DButils.getResumeForUser(userId);
        if (resumeFile == null) {
            showAlert(Alert.AlertType.INFORMATION, "Resume", "No resume is stored for this account.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Download Resume");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName(ensurePdfExtension(resumeFile.getFileName()));

        File targetFile = fileChooser.showSaveDialog(resolveWindow(event));
        if (targetFile == null) {
            return;
        }

        try {
            Files.write(new File(ensurePdfExtension(targetFile.getAbsolutePath())).toPath(), resumeFile.getPdfData());
            updateStatus("Resume downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resume", "Could not save the PDF to disk.");
        }
    }

    private void refreshResumeStatus() {
        Integer userId = Session.getCurrentUserId();
        if (userId == null) {
            setResumeName("No user logged in");
            updateStatus("Log in to manage a resume.");
            return;
        }

        DButils.ResumeFile resumeFile = DButils.getResumeForUser(userId);
        if (resumeFile == null) {
            setResumeName("No resume uploaded");
            updateStatus("Upload a PDF resume to store it in the database.");
            return;
        }

        setResumeName(resumeFile.getFileName());
        updateStatus("Resume ready for download.");
    }

    private Integer requireCurrentUser() {
        Integer userId = Session.getCurrentUserId();
        if (userId == null) {
            showAlert(Alert.AlertType.WARNING, "Resume", "You must be logged in to manage resumes.");
        }
        return userId;
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

    private void setResumeName(String text) {
        if (resumeNameLabel != null) {
            resumeNameLabel.setText(text);
        }
    }

    private void updateStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

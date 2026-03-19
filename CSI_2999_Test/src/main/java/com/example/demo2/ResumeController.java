package com.example.demo2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResumeController {

    @FXML private ListView<DButils.ResumeEntry> resumeListView;
    @FXML private Label statusLabel;
    @FXML private Button uploadResumeButton;
    @FXML private Button downloadResumeButton;

    @FXML
    public void initialize() {

        if (resumeListView != null) {
            resumeListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(DButils.ResumeEntry item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.getFileName() + "  |  Uploaded: " + item.getUploadedAt());
                }
            });
        }

        loadResumes();
    }

    @FXML
    private void handleUploadResume() {

        Integer userId = Session.getCurrentUserId();
        if (userId == null) {
            setStatus("You must be logged in to upload a resume.");
            return;
        }

        Stage stage = getStage();
        if (stage == null) {
            setStatus("Unable to open the file picker.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        Path sourcePath = selectedFile.toPath();

        try {
            if (!isPdfFile(sourcePath)) {
                setStatus("Selected file is not a valid PDF.");
                return;
            }

            byte[] pdfData = Files.readAllBytes(sourcePath);
            boolean saved = DButils.insertResume(userId, selectedFile.getName(), pdfData);

            if (!saved) {
                setStatus("Resume upload failed.");
                return;
            }

            loadResumes();
            setStatus("Uploaded " + selectedFile.getName() + ".");

        } catch (IOException e) {
            setStatus("Could not read the selected PDF.");
        }
    }

    @FXML
    private void handleDownloadResume() {

        if (resumeListView == null) {
            setStatus("Resume list is not available.");
            return;
        }

        DButils.ResumeEntry selectedResume =
                resumeListView.getSelectionModel().getSelectedItem();

        if (selectedResume == null) {
            setStatus("Select a resume to download.");
            return;
        }

        Stage stage = getStage();
        if (stage == null) {
            setStatus("Unable to open the save dialog.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Resume PDF");
        fileChooser.setInitialFileName(ensurePdfExtension(selectedResume.getFileName()));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File targetFile = fileChooser.showSaveDialog(stage);
        if (targetFile == null) {
            return;
        }

        Path targetPath = appendPdfExtensionIfNeeded(targetFile.toPath());

        try {
            Files.write(targetPath, selectedResume.getPdfData());
            setStatus("Downloaded to " + targetPath.getFileName() + ".");
        } catch (IOException e) {
            setStatus("Could not save the selected resume.");
        }
    }

    private void loadResumes() {

        if (resumeListView == null) {
            return;
        }

        Integer userId = Session.getCurrentUserId();
        if (userId == null) {
            resumeListView.setItems(FXCollections.observableArrayList());
            setStatus("Log in to access resumes.");
            return;
        }

        List<DButils.ResumeEntry> resumes = DButils.getResumesByUser(userId);
        resumeListView.setItems(FXCollections.observableArrayList(resumes));

        if (!resumes.isEmpty()) {
            resumeListView.getSelectionModel().selectFirst();
            setStatus("Loaded " + resumes.size() + " resume(s).");
            return;
        }

        setStatus("No resumes uploaded yet.");
    }

    private boolean isPdfFile(Path path) throws IOException {

        String fileName = path.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".pdf")) {
            return false;
        }

        byte[] header = new byte[5];
        try (var inputStream = Files.newInputStream(path)) {
            int bytesRead = inputStream.read(header);
            if (bytesRead < 5) {
                return false;
            }
        }

        return header[0] == '%'
                && header[1] == 'P'
                && header[2] == 'D'
                && header[3] == 'F'
                && header[4] == '-';
    }

    private Path appendPdfExtensionIfNeeded(Path path) {

        String fileName = path.getFileName().toString();
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return path;
        }

        return path.resolveSibling(fileName + ".pdf");
    }

    private String ensurePdfExtension(String fileName) {

        if (fileName.toLowerCase().endsWith(".pdf")) {
            return fileName;
        }

        return fileName + ".pdf";
    }

    private Stage getStage() {

        if (resumeListView != null && resumeListView.getScene() != null) {
            return (Stage) resumeListView.getScene().getWindow();
        }

        if (uploadResumeButton != null && uploadResumeButton.getScene() != null) {
            return (Stage) uploadResumeButton.getScene().getWindow();
        }

        if (downloadResumeButton != null && downloadResumeButton.getScene() != null) {
            return (Stage) downloadResumeButton.getScene().getWindow();
        }

        return null;
    }

    private void setStatus(String message) {

        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}

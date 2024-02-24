package com.aesthetic.wrangler.controllers;

import com.aesthetic.wrangler.core.encryption.EncryptionHandler;
import com.aesthetic.wrangler.core.FileHandler;
import com.aesthetic.wrangler.controllers.helpers.KeyAndValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.util.List;

public class MainController {
    // Files
    // Path Field
    @FXML
    private Label filePath;

    private void updateFilesPath() {
        try {
            filePath.setText(FileHandler.getSelectedFiles().get(0).getParent());
        } catch (IndexOutOfBoundsException ex) {
            filePath.setText("");
        }
    }

    // File List
    @FXML
    private ListView<String> fileList;
    private ObservableList<String> selectedFilesNames;

    private void updateSelectedFileNames() {
        selectedFilesNames.clear();
        for (File file : FileHandler.getSelectedFiles()) {
            selectedFilesNames.add(file.getName());
        }
        fileList.setItems(selectedFilesNames);
    }
    @FXML
    protected void onSelectPathButtonClick(ActionEvent e) {
        Window window = ((Node)e.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(window);
        FileHandler.addFiles(files);

        updateAllContexts();
        e.consume();
    }
    @FXML
    protected void onClearFilesButtonClick(ActionEvent e) {
        FileHandler.clearFiles();
        fileList.setItems(null);

        updateAllContexts();
        e.consume();
    }

    // Encryption
    // Choice Boxes
    @FXML
    private ChoiceBox hashList;
    @FXML
    private ChoiceBox encryptionList;
    @FXML
    private ChoiceBox modeList;

    // Password Fields
    @FXML
    private TextField passwordField;

    // Buttons
    @FXML
    protected void onEncryptButtonClick(ActionEvent e) {
        passValuesToHandler();
        EncryptionHandler.encrypt();
        e.consume();
    }
    @FXML
    protected void onDecryptButtonClick(ActionEvent e) {
        passValuesToHandler();
        EncryptionHandler.decrypt();
        e.consume();
    }

    // Log Area
    @FXML
    private TextArea logArea;
    private LogAreaController logAreaController;

    // Init
    @FXML
    public void initialize() {
        selectedFilesNames = FXCollections.observableArrayList();
        filePath.setText("");

        hashList.getItems().addAll(
                new KeyAndValue("MD5", "MD5"),
                new KeyAndValue("SHA-256", "SHA-256")
        );
        encryptionList.getItems().addAll(
                new KeyAndValue("AES", "AES")
        );
        modeList.getItems().addAll(
                new KeyAndValue("ECB", "ECB"),
                new KeyAndValue("CBC", "CBC")
        );

        logAreaController = new LogAreaController(logArea);
        logAreaController.start();

        setDefaults();
        updateAllContexts();
    }

    public void setDefaults() {
        hashList.setValue(hashList.getItems().get(1));
        encryptionList.setValue(encryptionList.getItems().get(0));
        modeList.setValue(modeList.getItems().get(1));
    }
    public void updateAllContexts() {
        updateFilesPath();
        updateSelectedFileNames();
    }
    public void passValuesToHandler() {
        EncryptionHandler.init(
                (String) ((KeyAndValue) hashList.getSelectionModel().getSelectedItem()).value,
                (String) ((KeyAndValue) encryptionList.getSelectionModel().getSelectedItem()).value,
                (String) ((KeyAndValue) modeList.getSelectionModel().getSelectedItem()).value,
                passwordField.getText()
        );
    }
}
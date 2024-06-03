package org.translator.translatorpythonruby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;

public class ResultsController {
    @FXML
    private TextField dragFile;

    @FXML
    private Button searchButton;

    @FXML
    private Button translateButton;

    @FXML
    protected void onSearchButtonClick() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Python and Ruby files (*.py, *.rb)", "*.py", "*.rb");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            boolean isAccepted = false;
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".py") || filePath.endsWith(".rb")) {
                dragFile.setText(filePath);
                isAccepted = true;
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) or Ruby (.rb) file.");
            }
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    @FXML
    protected void onTranslateButtonClick() {
        System.out.println("onTranslateButtonClick");
    }

    @FXML
    protected void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    protected void onDragFileDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean isAccepted = false;

        if (db.hasFiles()) {
            String filePath = db.getFiles().get(0).getAbsolutePath();
            if (filePath.endsWith(".py") || filePath.endsWith(".rb")) {
                dragFile.setText(filePath);
                isAccepted = true;
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) or Ruby (.rb) file.");
            }
        }
        event.setDropCompleted(isAccepted);
        event.consume();
    }
}

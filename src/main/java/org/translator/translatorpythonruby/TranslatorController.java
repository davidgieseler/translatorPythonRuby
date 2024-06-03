package org.translator.translatorpythonruby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TranslatorController {
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
                translateButton.setDisable(false);
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) or Ruby (.rb) file.");
            }
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    @FXML
    protected void onTranslateButtonClick() {
        String sourceFilePath = dragFile.getText();
        if (sourceFilePath.isEmpty() || (!sourceFilePath.endsWith(".py") && !sourceFilePath.endsWith(".rb"))) {
            System.out.println("Please select a Python file to translate.");
            return;
        }

        if (sourceFilePath.endsWith(".py")) {
            translateToRuby(sourceFilePath);
        }
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
                translateButton.setDisable(false);
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) or Ruby (.rb) file.");
            }
        }
        event.setDropCompleted(isAccepted);
        event.consume();
    }

    private void translateToRuby(String sourceFilePath){
        try {
//          Le o conteúdo do arquivo Python
            String pythonCode = Files.readString(Paths.get(sourceFilePath));

//          Traduzir para Ruby
            String rubyCode = new TranslatePythonToRuby().translatePythonToRuby(pythonCode);

//          Salva o resultado em um novo arquivo .rb
            String targetFilePath = sourceFilePath.replace(".py", ".rb");
            Files.writeString(Paths.get(targetFilePath), rubyCode);
            dragFile.setText("Translation completed. Ruby file created: " + targetFilePath);

            translateButton.setDisable(true);

            new ComparisonWindow().display(pythonCode, rubyCode);
        } catch (IOException e) {
            dragFile.setPromptText("Translation Error: " + e.getMessage());
            System.out.println("Error reading or writing files: " + e.getMessage());
        }
    }
}

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

//    Botão de Procurar
    @FXML
    protected void onSearchButtonClick() {
        FileChooser fileChooser = new FileChooser();

//        Filtros de ext e qual pasta abre no inicio
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Python files (*.py)", "*.py");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(null);

//        Verificação se o arquivo é aceito ou não
        if (file != null) {
            boolean isAccepted = false;
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".py")) {
                dragFile.setText(filePath);
                isAccepted = true;
                translateButton.setDisable(false);
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) file.");
            }
        } else {
            System.out.println("File selection cancelled.");
        }
    }
//  Botão de tradução para funcionar apenas com py
    @FXML
    protected void onTranslateButtonClick() {
        String sourceFilePath = dragFile.getText();
        if (sourceFilePath.isEmpty() || !sourceFilePath.endsWith(".py") ) {
            System.out.println("Please select a Python file to translate.");
            return;
        }

        if (sourceFilePath.endsWith(".py")) {
            translateToRuby(sourceFilePath);
        }
    }

//  Função de Arrastar que detecta que tem algo em cima
    @FXML
    protected void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

//   Função que identifica o arquivo que foi arrastado e largado
    @FXML
    protected void onDragFileDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean isAccepted = false;

        if (db.hasFiles()) {
            String filePath = db.getFiles().get(0).getAbsolutePath();
            if (filePath.endsWith(".py")) {
                dragFile.setText(filePath);
                isAccepted = true;
                translateButton.setDisable(false);
            } else {
                dragFile.setPromptText("File not accepted. Please select a Python (.py) file.");
            }
        }
        event.setDropCompleted(isAccepted);
        event.consume();
    }

//    Função da tradução
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

//            Desativa botão de tradução
            translateButton.setDisable(true);

//            Abre a janela de comparação
            new ComparisonWindow().display(pythonCode, rubyCode);
        } catch (IOException e) {
            dragFile.setPromptText("Translation Error: " + e.getMessage());
            System.out.println("Error reading or writing files: " + e.getMessage());
        }
    }
}

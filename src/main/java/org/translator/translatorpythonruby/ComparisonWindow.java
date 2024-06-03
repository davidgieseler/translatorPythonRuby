package org.translator.translatorpythonruby;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ComparisonWindow {
    public void display(String originalText, String translatedText) {
        Stage stage = new Stage();
        stage.setTitle("File Comparison");

        // Área de texto para o conteúdo original
        TextArea originalContent = new TextArea(originalText);
        originalContent.setEditable(false);

        // Área de texto para o conteúdo traduzido
        TextArea translatedContent = new TextArea(translatedText);
        translatedContent.setEditable(true);

        HBox hbox = new HBox(originalContent, translatedContent);
        hbox.setSpacing(10);  // Espaço entre as áreas de texto

        Scene scene = new Scene(hbox, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
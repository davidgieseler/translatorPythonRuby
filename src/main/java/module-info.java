module org.translator.translatorpythonruby {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.translator.translatorpythonruby to javafx.fxml;
    exports org.translator.translatorpythonruby;
}
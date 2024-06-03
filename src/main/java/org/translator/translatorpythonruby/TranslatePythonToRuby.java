package org.translator.translatorpythonruby;

import java.util.ArrayList;
import java.util.List;

public class TranslatePythonToRuby {
    public String translatePythonToRuby(String pythonCode) {
        String[] lines = pythonCode.split("\\n");
        List<String> processedLines = new ArrayList<>();

        int blockCount = 0;  // Contador de blocos que precisam de 'end'

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Tradução de list comprehensions antes de ajustes finais
            line = translateListComprehension(line);

            int currentIndent = getIndentationLevel(line);

            if (isNewBlock(line)) {
                line = adjustLineForRuby(line);
                blockCount++;
            } else {
                line = adjustContinuationLineForRuby(line);
            }

            line = adjustLineWithCommonSyntaxes(line);
            processedLines.add(line);

            if (i == lines.length - 1 || (i + 1 < lines.length && lines[i + 1].trim().isEmpty())) {
                while (blockCount > 0) {
                    processedLines.add(indentString("end", currentIndent));
                    blockCount--;
                }
            }
        }

        return String.join("\n", processedLines);
    }

    private boolean isNewBlock(String line) {
        return line.trim().matches("^(def|if|while|for|unless|try).*:$");
    }

    private String adjustLineForRuby(String line) {
        if (line.contains("def")) {
            line = line.replace("def", "def").replace(":", "");
        } else if (line.contains("if") || line.contains("elif") || line.contains("else")) {
            line = line.replace(":", "").replace("elif", "elsif") + " then";
        } else if (line.contains("for")) {
            line = line.replaceAll("for (\\w+) in (\\w+):", "$2.each do |$1|");
        } else if (line.contains("while") || line.contains("unless")) {
            line = line.replace(":", " do");
        } else if (line.contains("try")) {
            line = "begin";
        } else if (line.contains("except")) {
            line = line.replace("except", "rescue");
        } else if (line.contains("finally")) {
            line = "ensure";
        }
        return line;
    }

    private String adjustContinuationLineForRuby(String line) {
        if (line.contains("elif")) {
            line = line.replace("elif", "elsif").replace(":", " then");
        } else if (line.contains("else")) {
            line = line.replace(":", "");  // Remove ':' for 'else'
        }
        return line;
    }

    private String adjustLineWithCommonSyntaxes(String line) {
        line = line.replaceAll("print\\s*\\(\"(.*?)\"\\)", "puts \"$1\"");
        line = line.replaceAll("\\band\\b", "&&");
        line = line.replaceAll("\\bor\\b", "||");
        line = line.replaceAll("\\bnot\\b", "!");
        line = line.replaceAll("\\bTrue\\b", "true");
        line = line.replaceAll("\\bFalse\\b", "false");
        return line;
    }

    private String translateListComprehension(String line) {
        if (line.matches(".*\\[.* for .* in .*\\].*")) {
            line = line.replaceAll("\\[(.*) for (\\w+) in (\\w+)\\]", "$3.map { |$2| $1 }");
        }
        return line;
    }

    private int getIndentationLevel(String line) {
        int indent = 0;
        for (char ch : line.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                indent++;
            } else {
                break;
            }
        }
        return indent;
    }

    private String indentString(String content, int indentLevel) {
        return " ".repeat(indentLevel) + content;
    }
}
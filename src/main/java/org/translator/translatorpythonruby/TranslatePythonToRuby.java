package org.translator.translatorpythonruby;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TranslatePythonToRuby {
    private static class Block {
        int indentLevel;
        String type;  // "function", "conditional", "loop"
        boolean isCompleteConditional; // Usado para marcar se o bloco condicional está completo

        Block(int indentLevel, String type) {
            this.indentLevel = indentLevel;
            this.type = type;
            this.isCompleteConditional = false; // Inicializa como falso
        }
    }

    public String translatePythonToRuby(String pythonCode) {
        String[] lines = pythonCode.split("\\n");
        List<String> processedLines = new ArrayList<>();
        Stack<Block> blockStack = new Stack<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int currentIndent = getIndentationLevel(line);

            if (isMainCheckLine(line)) {
                continue;  // Ignora a linha "__main__"
            }

            line = translateListComprehension(line);
            line = adjustLineWithCommonSyntaxes(line);
            boolean isNewBlock = isNewBlock(line);
            if (isNewBlock) {
                line = adjustLineForRuby(line);
                String type = determineBlockType(line);
                blockStack.push(new Block(currentIndent, type));
            } else {
                line = adjustLineForRuby(line);
                line = adjustContinuationLineForRuby(line);
            }
            processedLines.add(line);

            // Fechar blocos conforme necessário
            int nextIndent = (i < lines.length - 1) ? getIndentationLevel(lines[i + 1]) : 0;
            while (!blockStack.isEmpty() && blockStack.peek().indentLevel >= nextIndent) {
                Block lastBlock = blockStack.peek();
                if (lastBlock.type.equals("conditional") && !lastBlock.isCompleteConditional) {
                    if (i < lines.length - 1 && (lines[i + 1].trim().startsWith("elsif") || lines[i + 1].trim().startsWith("else"))) {

                        break;
                    }
                }
                lastBlock = blockStack.pop();
                processedLines.add(indentString("end", lastBlock.indentLevel));
            }
        }

        // Fechar todos os blocos restantes
        while (!blockStack.isEmpty()) {
            Block lastBlock = blockStack.pop();
            processedLines.add(indentString("end", lastBlock.indentLevel));
        }

        return String.join("\n", processedLines);
    }

    private String adjustLineForRuby(String line) {
        // Trata declarações de método
        if (line.trim().startsWith("def")) {
            line = line.replace(":", "");
        }
        // Trata declarações condicionais
        if (line.trim().startsWith("if ") || line.trim().startsWith("elif ")) {
            line = line.replace("elif", "elsif").replace(":", "") + " then";
        } else if (line.trim().startsWith("else")) {
            line = line.replace(":", ""); // "else" não usa "then"
        }
        // Trata loops
        if (line.trim().startsWith("while ") || line.trim().startsWith("for ")) {
            line = line.replace(":", " do");
        }
        // Trata blocos de tratamento de erro
        if (line.trim().startsWith("try")) {
            line = "begin";
        }
        if (line.trim().startsWith("except")) {
            line = line.replace("except", "rescue");
        }
        if (line.trim().startsWith("finally")) {
            line = "ensure";
        }
        return line;
    }

    private String adjustContinuationLineForRuby(String line) {
        if (line.contains("elif")) {
            return line.replace("elif", "elsif") + " then";
        } else if (line.contains("else")) {
            return line.replace(":", ""); // Remove ':' for 'else'
        }
        return line;
    }

    private boolean isNewBlock(String line) {
        return line.trim().matches("^(def|if|while|for|unless|try).*:$");
    }

    private String determineBlockType(String line) {
        if (line.trim().startsWith("def")) {
            return "function";
        } else if (line.trim().matches("^(if).*")) {
            return "conditional";
        } else if (line.trim().matches("^(while|for).*")) {
            return "loop";
        }
        return "other";
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

    private boolean isMainCheckLine(String line) {
        return line.trim().equals("if __name__ == \"__main__\":");
    }

    private String translateListComprehension(String line) {
        if (line.matches(".*\\[.* for .* in .*\\].*")) {
            return line.replaceAll("\\[(.*) for (\\w+) in (\\w+)\\]", "$3.map { |$2| $1 }");
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
}
package cn.dzangfan.code.eson.data.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.exn.EsonCannotPrettyPrintException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;

public class PrettyPrint extends CaseFunction<List<String>> {
    private int beginColumn;
    private int beginLineIndent;
    private int indentSize = 2;

    private PrettyPrint(int beginColumn, int beginLineIndent, int indentSize) {
        super();
        this.beginColumn = beginColumn;
        this.beginLineIndent = beginLineIndent;
        this.indentSize = indentSize;
    }

    private static final PrettyPrint INSTANCE = from(0, 0);

    public static String the(EsonValue value) {
        return value.on(INSTANCE).stream().collect(Collectors.joining("\n"));
    }

    public static PrettyPrint from() {
        return INSTANCE;
    }

    public static PrettyPrint from(int columnNumber, int curIndent) {
        return new PrettyPrint(columnNumber, curIndent, 2);
    }

    public PrettyPrint useIndent(int indentSize) {
        return new PrettyPrint(beginColumn, beginLineIndent, indentSize);
    }

    private static String withSpace(int amount, String content) {
        return Stream.generate(() -> " ").limit(amount)
                .collect(Collectors.joining()) + content;
    }

    @Override
    public List<String> whenSymbol(EsonSymbol symbol) {
        return List.of("'" + symbol.getContent());
    }

    private static final Map<Character, String> escapeTable
            = Map.of('\b', "\\b", '\f', "\\f", '\n', "\\n", '\r', "\\r", '\t',
                     "\\t", '"', "\\\"", '\\', "\\\\");

    @Override
    public List<String> whenString(EsonString string) {
        String content = string.getContent();
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        content.chars().forEach(value -> {
            char c = (char) value;
            String escape = escapeTable.get(c);
            if (escape == null) {
                sb.append(c);
            } else {
                sb.append(escape);
            }
        });
        sb.append('"');
        return List.of(sb.toString());
    }

    @Override
    public List<String> whenNumber(EsonNumber number) {
        if (number.isInteger()) {
            Integer integer = number.floor();
            return List.of(integer.toString());
        } else {
            return List.of(number.getValue().toString());
        }
    }

    @Override
    public List<String> whenBoolean(boolean value) {
        return List.of(value ? "true" : "false");
    }

    @Override
    public List<String> whenNull() {
        return List.of("null");
    }

    @Override
    public List<String> whenID(EsonID id) {
        throw EsonRuntimeException
                .causedBy(new EsonCannotPrettyPrintException());
    }

    @Override
    public List<String> whenObject(EsonObject object) {
        if (object.getContent().isEmpty()) {
            return List.of("{ }");
        }
        List<String> lines = new ArrayList<String>();
        lines.add("{");

        int innerIndentSpace = beginLineIndent + indentSize;
        for (Entry entry : object.getContent()) {
            String key = entry.getKey().getName();
            String prefix = withSpace(innerIndentSpace, key + ": ");
            List<String> valueLines = entry.getValue()
                    .on(PrettyPrint.from(prefix.length(), innerIndentSpace));
            lines.add(prefix + valueLines.get(0));
            for (int i = 1; i < valueLines.size(); ++i) {
                lines.add(valueLines.get(i));
            }
        }
        lines.add(withSpace(beginLineIndent, "}"));
        return lines;
    }

    @Override
    public List<String> whenArray(EsonArray array) {
        if (array.getContent().isEmpty()) {
            return List.of("[ ]");
        }
        List<String> lines = new ArrayList<String>();
        lines.add("[");
        int innerIndentSpace = beginLineIndent + indentSize;

        for (EsonValue value : array.getContent()) {
            String prefix = withSpace(innerIndentSpace, "");
            List<String> valueLines = value
                    .on(PrettyPrint.from(prefix.length(), prefix.length()));
            lines.add(prefix + valueLines.get(0));
            for (int i = 1; i < valueLines.size(); ++i) {
                lines.add(valueLines.get(i));
            }
        }
        lines.add(withSpace(beginLineIndent, "]"));
        return lines;
    }

    @Override
    public List<String> whenLambda(EsonLambda lambda) {
        return List.of(String.format("<lambda with %d branch(es)>",
                                     lambda.getContent().size()));
    }

    @Override
    public List<String> whenApplication(EsonApplication application) {
        throw EsonRuntimeException
                .causedBy(new EsonCannotPrettyPrintException());
    }

}

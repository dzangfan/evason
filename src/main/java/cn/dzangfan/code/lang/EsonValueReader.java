package cn.dzangfan.code.lang;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import cn.dzangfan.code.data.EsonValue;

public class EsonValueReader {
    private CharStream charStream;
    private EsonVisitor<EsonValue> visitor = new ToEsonValueVisitor();

    private EsonValueReader(CharStream charStream) {
        super();
        this.charStream = charStream;
    }

    public static EsonValueReader from(InputStream inputStream)
            throws IOException {
        return new EsonValueReader(CharStreams.fromStream(inputStream));
    }

    public static EsonValueReader from(String string) {
        return new EsonValueReader(CharStreams.fromString(string));
    }

    public static EsonValueReader from(CharStream charStream) {
        return new EsonValueReader(charStream);
    }

    public EsonValue toEsonValue() {
        EsonLexer esonLexer = new EsonLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(esonLexer);
        EsonParser esonParser = new EsonParser(tokenStream);
        ParseTree parseTree = esonParser.value();
        return visitor.visit(parseTree);
    }

}

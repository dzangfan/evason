package cn.dzangfan.code;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import cn.dzangfan.code.lang.EsonLexer;
import cn.dzangfan.code.lang.EsonParser;

public class Program {

    public static void main(String[] args) {
        CharStream charStream = CharStreams.fromString("({ a: {b: c}} 'a 'b)");
        EsonLexer esonLexer = new EsonLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(esonLexer);
        EsonParser esonParser = new EsonParser(tokenStream);
        ParseTree parseTree = esonParser.value();

        System.out.println(parseTree.toStringTree(esonParser));
    }

}

package cn.dzangfan.code.eson.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonLambda.Branch;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;

class EsonValueReaderTest {

    private void read(String source, EsonValue expected) {
        assertEquals(expected, EsonValueReader.from(source).toEsonValue());
    }

    @Test
    void testSymbol() {
        read("'name", EsonSymbol.from("name"));
    }

    @Test
    void testString() {
        read("\"simple\"", EsonString.from("simple"));
        read("\"\\\"quoted\\\"\"", EsonString.from("\"quoted\""));
        read("\"\\\\ \\/ \\b \\f \\n \\r \\t\"",
             EsonString.from("\\ / \b \f \n \r \t"));
    }

    @Test
    void testNumber() {
        read("100", EsonNumber.fromInteger(100));
        read("100.10", EsonNumber.fromDouble(100.10));
        read("2.5E010", EsonNumber.fromDouble(2.5e10));
    }

    @Test
    void testSpecialValue() {
        read("true", EsonSpecialValue.TRUE);
        read("false", EsonSpecialValue.FALSE);
        read("null", EsonSpecialValue.NULL);
    }

    @Test
    void testID() {
        read("True", EsonID.from("True"));
        read("CONST", EsonID.from("CONST"));
        read("__BURN__", EsonID.from("__BURN__"));
        read("g4", EsonID.from("g4"));
    }

    @Test
    void testObject() {
        read("{}", EsonObject.from());
        read("{ x: 10, y: 20 }",
             EsonObject.from(Entry.from("x", EsonNumber.fromInteger(10)),
                             Entry.from("y", EsonNumber.fromInteger(20))));
        read("{ name: \"Joe\"\nage: 20 }",
             EsonObject.from(Entry.from("name", EsonString.from("Joe")),
                             Entry.from("age", EsonNumber.fromInteger(20))));
        EsonValue objectWithRest
                = EsonObject.from(Entry.from("x", EsonNumber.fromInteger(10)))
                        .withRest(EsonID.from("continue"));
        read("{ x: 10, ...continue }", objectWithRest);
        read("{ x: 10 ... continue }", objectWithRest);
        EsonValue objectNeedExpand = EsonObject
                .from(Entry.from("x", EsonID.from("x")),
                      Entry.from("y", EsonID.from("y")))
                .withRest(EsonID.from("z"));
        read("{ x, y, ...z }", objectNeedExpand);
        read("{ x y ...z}", objectNeedExpand);
    }

    @Test
    void testArray() {
        read("[]", EsonArray.from());
        EsonArray array
                = EsonArray.from(EsonSpecialValue.TRUE, EsonID.from("is"),
                                 EsonSpecialValue.NULL);
        read("[true, is, null]", array);
        read("[true is null]", array);
        read("[true is null ...sigh", array.withRest(EsonID.from("sigh")));
        read("[true is null, ...sign]", array.withRest(EsonID.from("sign")));
    }

    @Test
    void testLambda() {
        String basic = """
                    # { x } => x
                    |   _   => 0
                    #
                """;
        read(basic,
             EsonLambda.from(
                             Branch.from(EsonObject.from(Entry
                                     .from(EsonID.from("x"), EsonID.from("x"))),
                                         EsonID.from("x")),
                             Branch.from(EsonID.from("_"),
                                         EsonNumber.fromInteger(0))));
        String expand = """
                            # ({ x } 0 [1 ..._]) => x
                            | (_     n _)        => n
                            #
                """;
        read(expand, EsonLambda
                .from(Branch.from(EsonObject.from(Entry.from("x", EsonID
                        .from("x"))), EsonLambda.from(Branch
                                .from(EsonNumber.fromInteger(0),
                                      EsonLambda.from(Branch
                                              .from(EsonArray.from(EsonNumber
                                                      .fromInteger(1))
                                                      .withRest(EsonID
                                                              .from("_")),
                                                    EsonID.from("x")))))),
                      Branch.from(EsonID.from("_"), EsonLambda.from(Branch
                              .from(EsonID.from("n"),
                                    EsonLambda.from(Branch
                                            .from(EsonID.from("_"),
                                                  EsonID.from("n"))))))));
    }

    @Test
    void testApplication() {
        read("(f x)", EsonApplication.from(EsonID.from("f"), EsonID.from("x")));

        read("(a b c d e)", // ((((a b) c) d) e)
             EsonApplication.from(EsonApplication
                     .from(EsonApplication.from(EsonApplication
                             .from(EsonID.from("a"), EsonID.from("b")),
                                                EsonID.from("c")),
                           EsonID.from("d")), EsonID.from("e")));
    }

}

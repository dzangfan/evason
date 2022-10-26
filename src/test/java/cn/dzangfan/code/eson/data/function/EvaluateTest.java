package cn.dzangfan.code.eson.data.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
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
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.rumtime.Environment;

class EvaluateTest {

    private Environment ROOT;

    @BeforeEach
    void setup() throws EsonRedefinitionException {
        ROOT = Environment.ROOT.extend();
        ROOT.define(EsonID.from("param"), EsonString.from("/usr/bin"));
    }

    private EsonValue eval(EsonValue value, Environment environment) {
        EsonValue result = value.on(Evaluate.in(environment));
        assertTrue(result.on(IsFinalValue.getInstance()));
        return result;
    }

    private void evalTo(EsonValue expected, EsonValue value) {
        evalTo(expected, value, ROOT);
    }

    private void evalTo(EsonValue expected, EsonValue value,
                        Environment environment) {
        EsonValue result = eval(value, environment);
        assertEquals(expected, result);
    }

    private void selfEval(EsonValue value) {
        evalTo(value, value);
    }

    @Test
    void testWhenSymbolEsonSymbol() {
        selfEval(EsonSymbol.from("ok"));
    }

    @Test
    void testWhenStringEsonString() {
        selfEval(EsonString.from("ok"));
    }

    @Test
    void testWhenNumberEsonNumber() {
        selfEval(EsonNumber.fromInteger(10));
        selfEval(EsonNumber.fromDouble(.10));
    }

    @Test
    void testWhenBooleanBoolean() {
        selfEval(EsonSpecialValue.TRUE);
        selfEval(EsonSpecialValue.FALSE);
    }

    @Test
    void testWhenNull() {
        selfEval(EsonSpecialValue.NULL);
    }

    @Test
    void testWhenIDEsonID() {
        evalTo(EsonString.from("/usr/bin"), EsonID.from("param"));
    }

    @Test
    void testWhenObjectEsonObject() throws EsonRedefinitionException {

        evalTo(EsonObject
                .from(Entry.from("x", EsonString.from("hell")),
                      Entry.from("y", EsonString.from("o")),
                      Entry.from("z", EsonString.from("hello")),
                      Entry.from("_", EsonObject
                              .from(Entry.from("a", EsonString.from("!")),
                                    Entry.from("b",
                                               EsonString.from("hello!"))))),
               EsonObject
                       .from(Entry.from("x", EsonString.from("hell")),
                             Entry.from("y", EsonString.from("o")),
                             // z depends on other variables in the same level
                             Entry.from("z",
                                        EsonApplication.from(EsonID.from("x"),
                                                             EsonID.from("y"))),
                             Entry.from("_", EsonObject
                                     .from(Entry.from("a",
                                                      EsonString.from("!")),
                                           // b depends on other variables in
                                           // the same level and other levels
                                           Entry.from("b", EsonApplication
                                                   .from(EsonID.from("z"),
                                                         EsonID.from("a")))))));
    }

    @Test
    void testWhenArrayEsonArray() {
        evalTo(EsonArray.from(EsonString.from("rm"),
                              EsonString.from("/usr/bin")),
               EsonArray.from(
                              EsonApplication.from(EsonString.from("r"),
                                                   EsonString.from("m")),
                              EsonID.from("param")));
    }

    @Test
    void testWhenLambdaEsonLambda() {
        selfEval(EsonLambda.from().in(ROOT));
        selfEval(EsonLambda.from(
                                 Branch.from(EsonNumber.fromInteger(0),
                                             EsonNumber.fromInteger(1)),
                                 Branch.from(EsonID.from("others"),
                                             EsonNumber.fromInteger(0)))
                .in(ROOT));
    }

    private EsonValue apply(EsonValue operator, EsonValue operand,
                            Environment environment) {
        EsonApplication app = EsonApplication.from(operator, operand);
        return eval(app, environment);
    }

    private EsonValue apply(EsonValue operator, EsonValue operand) {
        return apply(operator, operand, ROOT);
    }

    private void applyTo(Environment environment, EsonValue expected,
                         EsonValue operator, EsonValue operand) {
        assertEquals(expected, apply(operator, operand, environment));
    }

    @Test
    void testWhenApplicationEsonApplication() throws EsonRedefinitionException {
        Environment localEnvironment = ROOT.extend();
        EsonObject object = EsonObject
                .from(Entry.from("x", EsonString.from("hell")),
                      Entry.from("y", EsonString.from("o")),
                      Entry.from("z", EsonString.from("hello")),
                      Entry.from("_", EsonObject
                              .from(Entry.from("a", EsonString.from("!")), Entry
                                      .from("b", EsonString.from("hello!")))));
        EsonArray array = EsonArray
                .from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(1),
                      EsonNumber.fromInteger(2), EsonNumber.fromInteger(3),
                      EsonNumber.fromInteger(4));
        localEnvironment.define(EsonID.from("repeat5"), EsonLambda.from(Branch
                .from(EsonID.from("value"),
                      EsonApplication.from(EsonNumber.fromInteger(5), EsonLambda
                              .from(Branch.from(EsonID.from("$_"),
                                                EsonID.from("value")))))));
        // ID, LAMBDA, NUMBER
        applyTo(localEnvironment, EsonArray
                .from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(0),
                      EsonNumber.fromInteger(0), EsonNumber.fromInteger(0),
                      EsonNumber.fromInteger(0)),
                EsonID.from("repeat5"), EsonNumber.fromInteger(0));

        // STRING
        applyTo(localEnvironment, EsonString.from("Hello world!"),
                EsonString.from("Hello "), EsonString.from("world!"));

        // BOOLEAN
        applyTo(ROOT, EsonNumber.fromInteger(1),
                apply(EsonSpecialValue.TRUE, EsonNumber.fromInteger(1)),
                EsonNumber.fromInteger(0));
        applyTo(ROOT, EsonNumber.fromInteger(0),
                apply(EsonSpecialValue.FALSE, EsonNumber.fromInteger(1)),
                EsonNumber.fromInteger(0));

        // OBJECT
        applyTo(localEnvironment, EsonString.from("o"), object,
                EsonSymbol.from("y"));
        applyTo(ROOT, EsonString.from("hello!"),
                apply(object, EsonSymbol.from("_")), EsonSymbol.from("b"));

        // ARRAY
        applyTo(localEnvironment, EsonNumber.fromInteger(2), array,
                EsonNumber.fromInteger(2));
        assertThrows(EsonRuntimeException.class, () -> {
            apply(array, EsonNumber.fromInteger(100));
        });

        // NULL
        assertThrows(EsonRuntimeException.class, () -> {
            apply(EsonSpecialValue.NULL, EsonString.from("bye~"));
        });

        // SYMBOL
        applyTo(localEnvironment, EsonSpecialValue.TRUE, EsonSymbol.from("z"),
                object);
        applyTo(localEnvironment, EsonSpecialValue.FALSE,
                EsonSymbol.from("bar"), object);

        // APPLICATION
        EsonApplication innerApp = EsonApplication
                .from(EsonID.from("param"), EsonString.from("/X11"));
        EsonApplication outerApp
                = EsonApplication.from(innerApp, EsonString.from("/X11"));
        applyTo(localEnvironment, EsonString.from("/usr/bin/X11/X11/X11"),
                outerApp, EsonString.from("/X11"));
    }

    @Test
    void testWhenObjectWithRest() throws EsonRedefinitionException {
        Environment localEnvironment = ROOT.extend();
        localEnvironment.define(EsonID.from("rest"), EsonObject
                .from(Entry.from("y", EsonNumber.fromInteger(10)),
                      Entry.from("z", EsonNumber.fromInteger(2))));
        EsonObject object = EsonObject
                .from(Entry.from("x", EsonNumber.fromInteger(0)),
                      Entry.from("y", EsonNumber.fromInteger(1)))
                .withRest(EsonID.from("rest"));
        evalTo(EsonObject.from(Entry.from("x", EsonNumber.fromInteger(0)),
                               Entry.from("y", EsonNumber.fromInteger(1)),
                               Entry.from("z", EsonNumber.fromInteger(2))),
               object, localEnvironment);
    }

    @Test
    void testWhenArrayWithRest() throws EsonRedefinitionException {
        Environment localEnvironment = ROOT.extend();
        localEnvironment.define(EsonID.from("rest"), EsonArray
                .from(EsonNumber.fromInteger(1), EsonNumber.fromInteger(2),
                      EsonNumber.fromInteger(3), EsonNumber.fromInteger(4)));
        EsonArray array = EsonArray.from(EsonNumber.fromInteger(0))
                .withRest(EsonID.from("rest"));
        evalTo(EsonArray
                .from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(1),
                      EsonNumber.fromInteger(2), EsonNumber.fromInteger(3),
                      EsonNumber.fromInteger(4)),
               array, localEnvironment);
    }

}

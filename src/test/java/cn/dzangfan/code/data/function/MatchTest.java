package cn.dzangfan.code.data.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonNumber;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;
import cn.dzangfan.code.data.EsonSpecialValue;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonSymbol;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.data.function.Match.Result;
import cn.dzangfan.code.exn.EsonNotConditionException;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.rumtime.SymbolTable;

class MatchTest {

    private SymbolTable match(EsonValue condition, EsonValue value) {
        try {
            Result result = value.on(Match.with(condition));
            assertTrue(result.isMatched());
            assertNotNull(result.getSymbolTable());
            return result.getSymbolTable();
        } catch (EsonNotConditionException e) {
            fail(e);
        }
        return null;
    }

    private void unmatch(EsonValue condition, EsonValue value) {
        try {
            Result result = value.on(Match.with(condition));
            assertFalse(result.isMatched());
        } catch (EsonNotConditionException e) {
            fail(e);
        }
    }

    @Test
    void testBindSymbol() {
        SymbolTable table = match(EsonID.from("x"), EsonString.from("string"));
        assertEquals(1, table.size());
        assertEquals(EsonString.from("string"),
                     table.get("x").get().getValue());
    }

    @Test
    void testWhenSymbolEsonSymbol() {
        SymbolTable table = match(EsonSymbol.from("x"), EsonSymbol.from("x"));
        assertEquals(0, table.size());
        unmatch(EsonSymbol.from("x"), EsonSymbol.from("y"));
    }

    @Test
    void testWhenStringEsonString() {
        SymbolTable table
                = match(EsonString.from("string"), EsonString.from("string"));
        assertEquals(0, table.size());
        unmatch(EsonString.from("string"), EsonString.from("stream"));
    }

    @Test
    void testWhenNumberEsonNumber() {
        SymbolTable table
                = match(EsonNumber.fromInteger(10), EsonNumber.fromInteger(10));
        assertEquals(0, table.size());
        table = match(EsonNumber.fromDouble(10.0), EsonNumber.fromDouble(10.0));
        assertEquals(0, table.size());
        table = match(EsonNumber.fromDouble(10.0), EsonNumber.fromInteger(10));
        assertEquals(0, table.size());
        unmatch(EsonNumber.fromInteger(10), EsonNumber.fromInteger(20));
    }

    @Test
    void testWhenBooleanBoolean() {
        SymbolTable table = match(EsonSpecialValue.TRUE, EsonSpecialValue.TRUE);
        assertEquals(0, table.size());
        table = match(EsonSpecialValue.FALSE, EsonSpecialValue.FALSE);
        assertEquals(0, table.size());
        unmatch(EsonSpecialValue.TRUE, EsonSpecialValue.FALSE);
        unmatch(EsonSpecialValue.FALSE, EsonSpecialValue.TRUE);
    }

    @Test
    void testWhenNull() {
        SymbolTable table = match(EsonSpecialValue.NULL, EsonSpecialValue.NULL);
        assertEquals(0, table.size());
    }

    @Test
    void testWhenIDEsonID() {
        assertThrows(EsonRuntimeException.class, () -> {
            match(EsonID.from("x"), EsonID.from("x"));
        });
        assertThrows(EsonRuntimeException.class, () -> {
            match(EsonNumber.fromInteger(10), EsonID.from("x"));
        });
    }

    @Test
    void testWhenObjectEsonObject() {
        EsonValue condition
                = EsonObject.from(Entry.from("x", EsonID.from("x")),
                                  Entry.from("y", EsonString.from("string")));
        EsonValue value = EsonObject
                .from(Entry.from("z", EsonSpecialValue.FALSE),
                      Entry.from("y", EsonString.from("string")),
                      Entry.from("x", EsonObject
                              .from(Entry.from("a", EsonSpecialValue.TRUE))));
        SymbolTable table = match(condition, value);
        assertEquals(1, table.size());
        assertEquals(EsonObject.from(Entry.from("a", EsonSpecialValue.TRUE)),
                     table.get("x").get().getValue());
        unmatch(EsonObject.from(Entry.from("a", EsonID.from("x"))), value);
    }

    @Test
    void testWhenArrayEsonArray() {
        EsonValue condition = EsonArray
                .from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(1),
                      EsonID.from("x"), EsonNumber.fromInteger(3));
        EsonValue value = EsonArray
                .from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(1),
                      EsonNumber.fromInteger(2), EsonNumber.fromInteger(3));
        SymbolTable table = match(condition, value);
        assertEquals(1, table.size());
        assertEquals(EsonNumber.fromInteger(2),
                     table.get("x").get().getValue());
        unmatch(EsonArray.from(EsonNumber.fromInteger(0),
                               EsonNumber.fromInteger(1),
                               EsonNumber.fromInteger(3)),
                value);
        unmatch(EsonArray.from(EsonNumber.fromInteger(0),
                               EsonNumber.fromInteger(1), EsonString.from("2"),
                               EsonNumber.fromInteger(3)),
                value);
    }

    @Test
    void testWhenLambdaEsonLambda() {
        assertThrows(EsonNotConditionException.class, () -> {
            EsonValue lambda = EsonLambda.from();
            Match.with(lambda);
        });
    }

    @Test
    void testWhenApplicationEsonApplication() {
        assertThrows(EsonRuntimeException.class, () -> {
            match(EsonID.from("x"),
                  EsonApplication.from(EsonID.from("f"), EsonID.from("x")));
        });
        assertThrows(EsonRuntimeException.class, () -> {
            match(EsonNumber.fromInteger(10),
                  EsonApplication.from(EsonID.from("f"), EsonID.from("x")));
        });
    }

}

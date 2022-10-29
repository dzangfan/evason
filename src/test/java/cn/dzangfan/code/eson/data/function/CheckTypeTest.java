package cn.dzangfan.code.eson.data.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import cn.dzangfan.code.eson.exn.EsonRuntimeException;

class CheckTypeTest {

    @Test
    void testWhenSymbol() {
        EsonValue value = EsonSymbol.from("name");
        assertEquals("name", value.on(CheckType.SYMBOL).getContent());
    }

    @Test
    void testWhenString() {
        EsonValue value = EsonString.from("string");
        assertEquals("string", value.on(CheckType.STRING).getContent());
    }

    @Test
    void testWhenNumber() {
        EsonValue value = EsonNumber.fromInteger(10);
        assertEquals(10, value.on(CheckType.NUMBER).getValue());
    }

    @Test
    void testWhenBoolean() {
        EsonValue value = EsonSpecialValue.TRUE;
        assertEquals(EsonSpecialValue.TRUE, value.on(CheckType.BOOLEAN));
        value = EsonSpecialValue.FALSE;
        assertEquals(EsonSpecialValue.FALSE, value.on(CheckType.BOOLEAN));
    }

    @Test
    void testWhenNull() {
        EsonValue value = EsonSpecialValue.NULL;
        assertEquals(EsonSpecialValue.NULL, value.on(CheckType.NULL));
    }

    @Test
    void testWhenID() {
        EsonValue value = EsonID.from("name");
        assertEquals("name", value.on(CheckType.ID).getName());
    }

    @Test
    void testWhenObject() {
        EsonValue value
                = EsonObject.from(Entry.from("name", EsonString.from("Joe")));
        assertEquals(1, value.on(CheckType.OBJECT).getContent().size());
    }

    @Test
    void testWhenArray() {
        EsonValue value = EsonArray.from(EsonString.from("Joe"));
        assertEquals(1, value.on(CheckType.ARRAY).getContent().size());
    }

    @Test
    void testWhenLambda() {
        EsonValue value = EsonLambda.from(Branch
                .from(EsonNumber.fromInteger(0), EsonNumber.fromDouble(1.)));
        assertEquals(1, value.on(CheckType.LAMBDA).getContent().size());
    }

    @Test
    void testWhenApplication() {
        EsonValue value = EsonApplication.from(EsonString.from("f"),
                                               EsonString.from("or"));
        assertEquals("f", value.on(CheckType.APPLICATION).getOperator()
                .on(CheckType.STRING).getContent());
    }

    @SuppressWarnings("unchecked")
    private void unmatch(@SuppressWarnings("rawtypes") CheckType checkType,
                         EsonValue value) {
        assertThrows(EsonRuntimeException.class, () -> {
            value.on(checkType);
        });
    }

    @Test
    void testUnmatch() {
        EsonString joe = EsonString.from("Joe");
        unmatch(CheckType.NUMBER, joe);
        unmatch(CheckType.SYMBOL, joe);
        unmatch(CheckType.BOOLEAN, joe);
        unmatch(CheckType.NULL, joe);
        unmatch(CheckType.ID, joe);
        unmatch(CheckType.OBJECT, joe);
        unmatch(CheckType.ARRAY, joe);
        unmatch(CheckType.LAMBDA, joe);
        unmatch(CheckType.APPLICATION, joe);
        unmatch(CheckType.STRING, EsonID.from("Joe"));
    }

}

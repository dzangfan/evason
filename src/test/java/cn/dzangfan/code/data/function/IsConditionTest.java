package cn.dzangfan.code.data.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonLambda.Branch;
import cn.dzangfan.code.data.EsonNumber;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;
import cn.dzangfan.code.data.EsonSpecialValue;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonSymbol;
import cn.dzangfan.code.data.EsonValue;

class IsConditionTest {

    private void is(boolean isCondition, EsonValue value) {
        assertEquals(isCondition, value.on(IsCondition.getInstance()));
    }

    @Test
    void testWhenSymbol() {
        is(true, EsonSymbol.from("symbol"));
    }

    @Test
    void testWhenString() {
        is(true, EsonString.from("string"));
    }

    @Test
    void testWhenNumber() {
        is(true, EsonNumber.fromInteger(10));
        is(true, EsonNumber.fromDouble(.10));
    }

    @Test
    void testWhenBoolean() {
        is(true, EsonSpecialValue.TRUE);
        is(true, EsonSpecialValue.FALSE);
    }

    @Test
    void testWhenNull() {
        is(true, EsonSpecialValue.NULL);
    }

    @Test
    void testWhenID() {
        is(true, EsonID.from("id"));
    }

    @Test
    void testWhenObjectEsonObject() {
        is(true, EsonObject.from(Entry.from("x", EsonNumber.fromInteger(0)),
                                 Entry.from("y", EsonNumber.fromInteger(0))));
        is(true, EsonObject.from(Entry.from("x", EsonObject
                .from(Entry.from("a", EsonNumber.fromInteger(0))))));
        is(false, EsonObject
                .from(Entry.from("x",
                                 EsonApplication.from(EsonSpecialValue.NULL,
                                                      EsonSpecialValue.TRUE)),
                      Entry.from("y", EsonNumber.fromInteger(0))));
        is(false, EsonObject
                .from(Entry.from("x", EsonID.from("x")),
                      Entry.from("y", EsonObject.from(Entry.from("a", EsonLambda
                              .from(Branch.from(EsonNumber.fromInteger(0),
                                                EsonNumber.fromInteger(1)))))),
                      Entry.from("z", EsonNumber.fromInteger(0))));
    }

    @Test
    void testWhenArrayEsonArray() {
        is(true,
           EsonArray.from(EsonNumber.fromInteger(0), EsonNumber.fromInteger(1),
                          EsonNumber.fromInteger(2)));
        is(true, EsonArray.from(EsonNumber.fromInteger(0),
                                EsonArray.from(EsonArray.from())));
        is(false, EsonArray.from(EsonNumber.fromInteger(0), EsonObject
                .from(Entry.from("x", EsonID.from("x")),
                      Entry.from("y", EsonObject.from(Entry.from("a", EsonLambda
                              .from(Branch.from(EsonNumber.fromInteger(0),
                                                EsonNumber.fromInteger(1)))))),
                      Entry.from("z", EsonNumber.fromInteger(0)))));
    }

    @Test
    void testWhenLambdaEsonLambda() {
        is(false,
           EsonLambda.from(Branch.from(EsonID.from("x"), EsonID.from("y"))));
    }

    @Test
    void testWhenApplicationEsonApplication() {
        is(false,
           EsonApplication.from(EsonID.from("f"), EsonNumber.fromInteger(0)));
    }

}

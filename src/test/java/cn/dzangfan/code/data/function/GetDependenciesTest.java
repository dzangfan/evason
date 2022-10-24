package cn.dzangfan.code.data.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

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

class GetDependenciesTest {

    private void depends(EsonValue value, String... dep) {
        assertEquals(Set.of(dep), value.on(GetDependencies.getInstance()));
    }

    @Test
    void testWhenSymbol() {
        depends(EsonSymbol.from("x"));
    }

    @Test
    void testWhenString() {
        depends(EsonString.from("x"));
    }

    @Test
    void testWhenNumber() {
        depends(EsonNumber.fromDouble(.10));
    }

    @Test
    void testWhenBoolean() {
        depends(EsonSpecialValue.TRUE);
    }

    @Test
    void testWhenNull() {
        depends(EsonSpecialValue.NULL);
    }

    @Test
    void testWhenID() {
        depends(EsonID.from("x"), "x");
    }

    @Test
    void testWhenObjectEsonObject() {
        depends(EsonObject.from(Entry.from("x", EsonID.from("x")), Entry
                .from("y",
                      EsonObject.from(Entry.from("a", EsonLambda.from(Branch
                              .from(EsonNumber.fromInteger(0),
                                    EsonObject.from(Entry
                                            .from("ext",
                                                  EsonSpecialValue.NULL))))),
                                      Entry.from("b", EsonApplication
                                              .from(EsonID.from("f"),
                                                    EsonNumber
                                                            .fromInteger(0))))),
                                Entry.from("z", EsonNumber.fromInteger(0))),
                "x", "f");
    }

    @Test
    void testWhenArrayEsonArray() {
        depends(EsonArray.from(EsonID.from("x"), EsonArray.from(EsonID
                .from("y")), EsonApplication
                        .from(EsonID.from("f"),
                              EsonLambda.from(Branch.from(EsonID.from("a"),
                                                          EsonID.from("b"))))),
                "x", "y", "f");
    }

    @Test
    void testWhenLambda() {
        depends(EsonLambda
                .from(Branch.from(EsonID.from("x"), EsonID.from("y"))));
    }

    @Test
    void testWhenApplicationEsonApplication() {
        depends(EsonApplication.from(EsonID.from("f"), EsonID.from("g")), "f",
                "g");
    }

}

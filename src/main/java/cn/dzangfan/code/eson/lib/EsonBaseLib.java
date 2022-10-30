package cn.dzangfan.code.eson.lib;

import static cn.dzangfan.code.eson.lib.EsonPrimitiveLambda.fun;

import java.util.function.BiFunction;

import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.CheckType;
import cn.dzangfan.code.eson.data.function.ExceptionCaseFunction;
import cn.dzangfan.code.eson.data.function.GetType;

public class EsonBaseLib extends EsonJavaLib {

    @Override
    protected void init() {
        defCompare("__lessThan", (lhs, rhs) -> lhs < rhs);
        defCompare("__greaterThan", (lhs, rhs) -> lhs > rhs);
        defCompare("__lessEqual", (lhs, rhs) -> lhs <= rhs);
        defCompare("__greaterEqual", (lhs, rhs) -> lhs >= rhs);
        defCompare("__numericallyEqual", (lhs, rhs) -> lhs.equals(rhs));
        defNumBiop("__plus", true, (lhs, rhs) -> lhs + rhs);
        defNumBiop("__minus", true, (lhs, rhs) -> lhs - rhs);
        defNumBiop("__mul", true, (lhs, rhs) -> lhs * rhs);
        defNumBiop("__div", false, (lhs, rhs) -> lhs / rhs);
        defNumBiop("__mod", true, (lhs, rhs) -> lhs % rhs);
    }

    @Override
    public String name() {
        return "base";
    }

    private void defCompare(String name,
                            BiFunction<Double, Double, Boolean> compare) {
        def(name, fun(general -> {
            return general.on(new ExceptionCaseFunction<EsonValue>(
                    GetType.Type.ARRAY) {

                @Override
                public EsonValue whenArray(EsonArray array) {
                    EsonNumber lhs = array.get(0).on(CheckType.NUMBER);
                    EsonNumber rhs = array.get(1).on(CheckType.NUMBER);
                    return EsonSpecialValue.from(compare.apply(lhs.getValue(),
                                                               rhs.getValue()));
                }

            });
        }));
    }

    private void defNumBiop(String name, boolean keepInteger,
                            BiFunction<Double, Double, Double> operation) {
        def(name, fun(general -> {
            return general.on(new ExceptionCaseFunction<EsonValue>(
                    GetType.Type.ARRAY) {

                @Override
                public EsonValue whenArray(EsonArray array) {
                    EsonNumber lhs = array.get(0).on(CheckType.NUMBER);
                    EsonNumber rhs = array.get(1).on(CheckType.NUMBER);
                    Double result
                            = operation.apply(lhs.getValue(), rhs.getValue());
                    if (keepInteger && lhs.isInteger() && rhs.isInteger()) {
                        return EsonNumber.fromInteger((int) Math.floor(result));
                    } else {
                        return EsonNumber.fromDouble(result);
                    }
                }

            });
        }));
    }

}

package cn.dzangfan.code.eson.lib;

import java.util.function.Function;
import java.util.function.Supplier;

import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonValue;

public class EsonPrimitiveLambda extends EsonLambda {

    private Function<Supplier<EsonValue>, EsonValue> function;

    private EsonPrimitiveLambda(
            Function<Supplier<EsonValue>, EsonValue> function) {
        this.function = function;
    }

    @Override
    public EsonValue apply(Supplier<EsonValue> operandSupplier) {
        return function.apply(operandSupplier);
    }

    public static
        EsonPrimitiveLambda fun(Function<EsonValue, EsonValue> function) {
        return new EsonPrimitiveLambda(v -> function.apply(v.get()));
    }

    public static
        EsonPrimitiveLambda
        lazyFun(Function<Supplier<EsonValue>, EsonValue> function) {
        return new EsonPrimitiveLambda(function);
    }

    @Override
    public String prettyPrint() {
        return "<primitive lambda>";
    }

}

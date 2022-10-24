package cn.dzangfan.code.data;

import java.util.function.Function;

public abstract class CaseFunction<T> {

    Function<EsonValue, T> defaultSupplier = (__) -> null;

    public CaseFunction() {
    }

    public CaseFunction(Function<EsonValue, T> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    public T whenSymbol(EsonSymbol symbol) {
        return defaultSupplier.apply(symbol);
    }

    public T whenString(EsonString string) {
        return defaultSupplier.apply(string);
    }

    public T whenNumber(EsonNumber number) {
        return defaultSupplier.apply(number);
    }

    public T whenBoolean(boolean value) {
        return defaultSupplier.apply(EsonSpecialValue.from(value));
    }

    public T whenNull() {
        return defaultSupplier.apply(EsonSpecialValue.NULL);
    }

    public T whenID(EsonID id) {
        return defaultSupplier.apply(id);
    }

    public T whenObject(EsonObject object) {
        return defaultSupplier.apply(object);
    }

    public T whenArray(EsonArray array) {
        return defaultSupplier.apply(array);
    }

    public T whenLambda(EsonLambda lambda) {
        return defaultSupplier.apply(lambda);
    }

    public T whenApplication(EsonApplication application) {
        return defaultSupplier.apply(application);
    }
}

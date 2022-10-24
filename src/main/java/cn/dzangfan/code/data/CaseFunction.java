package cn.dzangfan.code.data;

import java.util.function.Supplier;

public abstract class CaseFunction<T> {

    Supplier<T> defaultSupplier = () -> null;

    public CaseFunction() {
    }

    public CaseFunction(Supplier<T> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    public T whenSymbol(EsonSymbol symbol) {
        return defaultSupplier.get();
    }

    public T whenString(EsonString string) {
        return defaultSupplier.get();
    }

    public T whenNumber(EsonNumber number) {
        return defaultSupplier.get();
    }

    public T whenBoolean(boolean value) {
        return defaultSupplier.get();
    }

    public T whenNull() {
        return defaultSupplier.get();
    }

    public T whenID(EsonID id) {
        return defaultSupplier.get();
    }

    public T whenObject(EsonObject object) {
        return defaultSupplier.get();
    }

    public T whenArray(EsonArray array) {
        return defaultSupplier.get();
    }

    public T whenLambda(EsonLambda lambda) {
        return defaultSupplier.get();
    }

    public T whenApplication(EsonApplication application) {
        return defaultSupplier.get();
    }
}

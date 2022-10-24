package cn.dzangfan.code.data;

public abstract class EsonValue {
    public abstract <T> T on(CaseFunction<T> function);
}

package cn.dzangfan.code.eson.data;

public abstract class EsonValue {
    public abstract <T> T on(CaseFunction<T> function);
}

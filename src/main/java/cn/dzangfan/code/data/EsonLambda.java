package cn.dzangfan.code.data;

public class EsonLambda extends EsonValue {

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenLambda(this);
    }

}

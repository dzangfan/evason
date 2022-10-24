package cn.dzangfan.code.data.function;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;

public class IsCondition extends CaseFunction<Boolean> {

    private static final IsCondition INSTANCE = new IsCondition();

    public static IsCondition getInstance() {
        return INSTANCE;
    }

    private IsCondition() {
        super(() -> true);
    }

    @Override
    public Boolean whenObject(EsonObject object) {
        return object.getContent().stream().map(Entry::getValue)
                .allMatch(value -> value.on(INSTANCE));
    }

    @Override
    public Boolean whenArray(EsonArray array) {
        return array.getContent().stream()
                .allMatch(value -> value.on(INSTANCE));
    }

    @Override
    public Boolean whenLambda(EsonLambda lambda) {
        return false;
    }

    @Override
    public Boolean whenApplication(EsonApplication application) {
        return false;
    }

}

package cn.dzangfan.code.eson.data.function;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;

/**
 * 
 * 
 * Predict whether a {@link cn.dzangfan.code.eson.data.EsonValue} is a
 * <strong>condition</strong>.
 * 
 * <strong>Condition</strong> is a subset of valid ESON forms, which can be used
 * in pattern match. A <strong>condition</strong> should not contain any
 * 
 * <ol>
 * <li>Function application, i.e. {@code (f x)}</li>
 * <li>Lambda expression, i.e. {@code # 0 => 1 #}</li>
 * </ol>
 * 
 * @author Li Dzangfan
 *
 */
public class IsCondition extends CaseFunction<Boolean> {

    private static final IsCondition INSTANCE = new IsCondition();

    public static IsCondition getInstance() {
        return INSTANCE;
    }

    private IsCondition() {
        super((__) -> true);
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

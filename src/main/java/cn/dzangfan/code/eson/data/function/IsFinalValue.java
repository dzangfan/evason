package cn.dzangfan.code.eson.data.function;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonObject;

/**
 * 
 * Determine whether a {@code cn.dzangfan.code.eson.data.EsonValue} is a final value.
 * A {@code EsonValue} is a final value if and only if it is not and does not
 * contain any of following {@code EsonValue}
 * 
 * <ol>
 * <li>{@link cn.dzangfan.code.eson.data.EsonApplication}</li>
 * <li>{@link cn.dzangfan.code.eson.data.EsonID}</li>
 * </ol>
 * 
 * {@link cn.dzangfan.code.eson.data.EsonLambda} is always a final value.
 * 
 * @author Li Dzangfan
 *
 */
public class IsFinalValue extends CaseFunction<Boolean> {

    private IsFinalValue() {
        super((__) -> true);
    }

    private static final IsFinalValue INSTANCE = new IsFinalValue();

    public static IsFinalValue getInstance() {
        return INSTANCE;
    }

    @Override
    public Boolean whenID(EsonID id) {
        return false;
    }

    @Override
    public Boolean whenObject(EsonObject object) {
        return object.getMaybeRest().isEmpty() && object.getContent().stream()
                .map(EsonObject.Entry::getValue)
                .allMatch(value -> value.on(IsFinalValue.getInstance()));
    }

    @Override
    public Boolean whenArray(EsonArray array) {
        return array.getMaybeRest().isEmpty() && array.getContent().stream()
                .allMatch(value -> value.on(IsFinalValue.getInstance()));
    }

    @Override
    public Boolean whenApplication(EsonApplication application) {
        return false;
    }

}

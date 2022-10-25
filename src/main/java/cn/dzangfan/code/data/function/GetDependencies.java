package cn.dzangfan.code.data.function;

import java.util.HashSet;
import java.util.Set;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;

/**
 * 
 * Compute dependencies of a expression. Dependencies mean external variables
 * that the parameter is using. Formally:
 * 
 * <ol>
 * <li>A {@code id} depends { {@code id} }</li>
 * <li>A {@code object} depends union of what all its values are depending</li>
 * <li>A {@code array} depends union of what all its components are
 * depending</li>
 * <li>A {@code application} depends union of what its operator and its operand
 * are depending</li>
 * <li>Any other expressions depend nothing, including {@code lambda}s</li>
 * </ol>
 * 
 * @author Li Dzangfan
 *
 */
public class GetDependencies extends CaseFunction<Set<String>> {

    private static GetDependencies INSTANCE = new GetDependencies();

    public static GetDependencies getInstance() {
        return INSTANCE;
    }

    private GetDependencies() {
        super((__) -> Set.of());
    }

    @Override
    public Set<String> whenID(EsonID id) {
        HashSet<String> set = new HashSet<String>();
        set.add(id.getName());
        return set;
    }

    @Override
    public Set<String> whenObject(EsonObject object) {
        Set<String> union = new HashSet<String>();
        object.getContent().stream().map(Entry::getValue)
                .map(value -> value.on(INSTANCE)).forEach(dep -> {
                    union.addAll(dep);
                });
        object.getMaybeRest().ifPresent(id -> {
            union.add(id.getName());
        });
        return union;
    }

    @Override
    public Set<String> whenArray(EsonArray array) {
        Set<String> union = new HashSet<String>();
        array.getContent().stream().map(value -> value.on(INSTANCE))
                .forEach(dep -> {
                    union.addAll(dep);
                });
        array.getMaybeRest().ifPresent(id -> {
            union.add(id.getName());
        });
        return union;
    }

    @Override
    public Set<String> whenApplication(EsonApplication application) {
        Set<String> union = application.getOperator().on(INSTANCE);
        union.addAll(application.getOperand().on(INSTANCE));
        return union;
    }

}

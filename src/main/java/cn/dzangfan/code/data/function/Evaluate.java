package cn.dzangfan.code.data.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonNumber;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;
import cn.dzangfan.code.data.EsonSpecialValue;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonSymbol;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.exn.EsonRedefinitionException;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.rumtime.Environment;
import cn.dzangfan.code.utils.DependenceTree;

public class Evaluate extends CaseFunction<EsonValue> {

    private Environment environment;

    private Evaluate(Environment environment) {
        super();
        this.environment = environment;
    }

    public static Evaluate in(Environment environment) {
        return new Evaluate(environment);
    }

    @Override
    public EsonValue whenSymbol(EsonSymbol symbol) {
        return symbol;
    }

    @Override
    public EsonValue whenString(EsonString string) {
        return string;
    }

    @Override
    public EsonValue whenNumber(EsonNumber number) {
        return number;
    }

    @Override
    public EsonValue whenBoolean(boolean value) {
        return EsonSpecialValue.from(value);
    }

    @Override
    public EsonValue whenNull() {
        return EsonSpecialValue.NULL;
    }

    @Override
    public EsonValue whenID(EsonID id) {
        return id;
    }

    @Override
    public EsonValue whenObject(EsonObject object) {
        EsonObject orderedObject = orderByDependence(object);
        Environment localEnvironment = environment.extend();
        EsonObject newObject = EsonObject.from();
        for (Entry entry : orderedObject.getContent()) {
            EsonValue value
                    = entry.getValue().on(Evaluate.in(localEnvironment));
            try {
                localEnvironment.define(entry.getKey(), value);
            } catch (EsonRedefinitionException e) {
                EsonRuntimeException re = new EsonRuntimeException();
                re.initCause(e);
                throw re;
            }
            newObject.add(entry.getKey(), value);
        }
        return newObject;
    }

    @Override
    public EsonValue whenArray(EsonArray array) {
        List<EsonValue> evaluatedContent = array.getContent().stream()
                .map(value -> value.on(this)).toList();
        return EsonArray.from(evaluatedContent);
    }

    @Override
    public EsonValue whenLambda(EsonLambda lambda) {
        return lambda;
    }

    @Override
    public EsonValue whenApplication(EsonApplication application) {
        // TODO Auto-generated method stub
        return super.whenApplication(application);
    }

    private EsonObject orderByDependence(EsonObject object) {
        List<DependenceTree<Integer>> pool
                = new ArrayList<DependenceTree<Integer>>();
        for (int i = 0; i < object.getContent().size(); ++i) {
            pool.add(DependenceTree.of(i));
        }
        for (int i = 0; i < object.getContent().size(); ++i) {
            EsonValue value = object.getContent().get(i).getValue();
            Set<String> dependencies = value.on(GetDependencies.getInstance());
            for (int j = 0; j < object.getContent().size(); ++j) {
                String key = object.getContent().get(j).getKey().getName();
                if (dependencies.contains(key)) {
                    pool.get(i).depends(pool.get(j));
                }
            }
        }
        return null;
    }

}

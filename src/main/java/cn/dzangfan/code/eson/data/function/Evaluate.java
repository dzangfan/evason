package cn.dzangfan.code.eson.data.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonUndefinedIDException;
import cn.dzangfan.code.eson.rumtime.Environment;
import cn.dzangfan.code.eson.rumtime.SymbolTable;

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
        try {
            SymbolTable.Entry entry = environment.find(id);
            return entry.getValue();
        } catch (EsonUndefinedIDException e) {
            EsonRuntimeException re = new EsonRuntimeException();
            re.initCause(e);
            throw re;
        }
    }

    @Override
    public EsonValue whenObject(EsonObject object) {
        Environment localEnvironment = environment.extend();
        EsonObject newObject = EsonObject.from();
        for (Entry entry : object.getContent()) {
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
        object.getMaybeRest().ifPresent(id -> {
            id.on(this).on(new ExceptionCaseFunction<List<Entry>>(
                    GetType.Type.OBJECT) {

                @Override
                public List<Entry> whenObject(EsonObject object) {
                    return object.getContent();
                }

            }).forEach(entry -> {
                // Only add what haven't appeared
                if (!newObject.getContent().stream().anyMatch(pEntry -> pEntry
                        .getKey().getName().equals(entry.getKey().getName()))) {
                    newObject.getContent().add(entry);
                }
            });
        });
        return newObject;
    }

    @Override
    public EsonValue whenArray(EsonArray array) {
        List<EsonValue> evaluatedContent = new ArrayList<EsonValue>(array
                .getContent().stream().map(value -> value.on(this)).toList());
        array.getMaybeRest().ifPresent(id -> {
            List<EsonValue> content
                    = id.on(this).on(new ExceptionCaseFunction<List<EsonValue>>(
                            GetType.Type.ARRAY) {

                        @Override
                        public List<EsonValue> whenArray(EsonArray array) {
                            return array.getContent();
                        }

                    });
            evaluatedContent.addAll(content);
        });
        return EsonArray.from(evaluatedContent);
    }

    @Override
    public EsonValue whenLambda(EsonLambda lambda) {
        return lambda.in(environment);
    }

    @Override
    public EsonValue whenApplication(EsonApplication application) {
        EsonValue operator = application.getOperator().on(this);
        Supplier<EsonValue> operandSupplier
                = () -> application.getOperand().on(this);
        return operator.on(Apply.to(operandSupplier));
    }

}

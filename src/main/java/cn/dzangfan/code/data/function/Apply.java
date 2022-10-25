package cn.dzangfan.code.data.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonLambda.Branch;
import cn.dzangfan.code.data.EsonNumber;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;
import cn.dzangfan.code.data.EsonSpecialValue;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonSymbol;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.exn.EsonSimpleException;

public class Apply extends CaseFunction<EsonValue> {

    private Supplier<EsonValue> operandSupplier;

    private Apply(Supplier<EsonValue> operandSupplier) {
        super();
        this.operandSupplier = operandSupplier;
    }

    public static Apply to(Supplier<EsonValue> operandSupplier) {
        return new Apply(operandSupplier);
    }

    @Override
    public EsonValue whenSymbol(EsonSymbol symbol) {
        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.OBJECT) {

                    @Override
                    public EsonValue whenObject(EsonObject object) {
                        boolean isPresent = object.getContent().stream()
                                .anyMatch(entry -> entry.getKey().getName()
                                        .equals(symbol.getContent()));
                        return EsonSpecialValue.from(isPresent);
                    }

                });
    }

    @Override
    public EsonValue whenNumber(EsonNumber number) {
        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.LAMBDA) {

                    @Override
                    public EsonValue whenLambda(EsonLambda lambda) {
                        List<EsonValue> list = new ArrayList<EsonValue>();
                        int times = number.floor();
                        for (int i = 0; i < times; ++i) {
                            EsonNumber num = EsonNumber.fromInteger(i);
                            EsonValue result = lambda.apply(num);
                            list.add(result);
                        }
                        return EsonArray.from(list);
                    }

                });
    }

    @Override
    public EsonValue whenNull() {
        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.STRING) {

                    @Override
                    public EsonValue whenString(EsonString string) {
                        throw EsonRuntimeException
                                .causedBy(new EsonSimpleException(
                                        string.getContent()));
                    }

                });
    }

    @Override
    public EsonValue whenString(EsonString operatorString) {

        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.STRING) {

                    @Override
                    public EsonValue whenString(EsonString operandString) {
                        String content = operatorString.getContent()
                                + operandString.getContent();
                        return EsonString.from(content);
                    }

                });

    }

    @Override
    public EsonValue whenBoolean(boolean value) {
        Branch branch
                = value ? Branch.from(EsonID.from("$_"), operandSupplier.get())
                        : Branch.from(EsonID.from("$x"), EsonID.from("$x"));
        return EsonLambda.from(branch);
    }

    @Override
    public EsonValue whenObject(EsonObject object) {
        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.SYMBOL) {

                    @Override
                    public EsonValue whenSymbol(EsonSymbol symbol) {
                        return object.getContent().stream()
                                .filter(entry -> entry.getKey().getName()
                                        .equals(symbol.getContent()))
                                .map(Entry::getValue).findFirst()
                                .orElse(EsonSpecialValue.NULL);
                    }

                });
    }

    @Override
    public EsonValue whenArray(EsonArray array) {
        return operandSupplier.get()
                .on(new ExceptionCaseFunction<EsonValue>(GetType.Type.NUMBER) {

                    @Override
                    public EsonValue whenNumber(EsonNumber number) {
                        int idx = (int) Math.floor(number.getValue());
                        try {
                            return array.getContent().get(idx);
                        } catch (IndexOutOfBoundsException e) {
                            throw EsonRuntimeException.causedBy(e);
                        }
                    }

                });
    }

    @Override
    public EsonValue whenLambda(EsonLambda lambda) {
        return lambda.apply(operandSupplier.get());
    }

}

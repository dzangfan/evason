package cn.dzangfan.code.eson.data.function;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;

public class GetType extends CaseFunction<GetType.Type> {

    private static final GetType INSTANCE = new GetType();

    public static GetType getInstance() {
        return INSTANCE;
    }

    private GetType() {
    }

    public static enum Type {
        SYMBOL, STRING, NUMBER, BOOLEAN, NULL, ID, OBJECT, ARRAY, LAMBDA,
        APPLICATION
    }

    @Override
    public Type whenSymbol(EsonSymbol symbol) {
        return Type.SYMBOL;
    }

    @Override
    public Type whenString(EsonString string) {
        return Type.STRING;
    }

    @Override
    public Type whenNumber(EsonNumber number) {
        return Type.NUMBER;
    }

    @Override
    public Type whenBoolean(boolean value) {
        return Type.BOOLEAN;
    }

    @Override
    public Type whenNull() {
        return Type.NULL;
    }

    @Override
    public Type whenID(EsonID id) {
        return Type.ID;
    }

    @Override
    public Type whenObject(EsonObject object) {
        return Type.OBJECT;
    }

    @Override
    public Type whenArray(EsonArray array) {
        return Type.ARRAY;
    }

    @Override
    public Type whenLambda(EsonLambda lambda) {
        return Type.LAMBDA;
    }

    @Override
    public Type whenApplication(EsonApplication application) {
        return Type.APPLICATION;
    }

}

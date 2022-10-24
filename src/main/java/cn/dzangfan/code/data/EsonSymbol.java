package cn.dzangfan.code.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EsonSymbol extends EsonValue {

    private String content;

    private static Map<String, EsonSymbol> symbolPool
            = new HashMap<String, EsonSymbol>();

    private EsonSymbol(String content) {
        super();
        this.content = content;
    }

    @Override
    public boolean isCondition() {
        return true;
    }

    public static EsonSymbol from(String content) {
        EsonSymbol symbol = symbolPool.get(content);
        if (Objects.nonNull(symbol)) {
            return symbol;
        }
        EsonSymbol newSymbol = new EsonSymbol(content);
        symbolPool.put(content, newSymbol);
        return newSymbol;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenSymbol(this);
    }

}

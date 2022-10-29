package cn.dzangfan.code.eson.lib;

import java.util.HashMap;
import java.util.Map;

import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonValue;

public abstract class EsonJavaLib extends EsonLib {
    private Map<EsonID, EsonValue> symbolTableMap
            = new HashMap<EsonID, EsonValue>();

    protected void def(String name, EsonValue value) {
        EsonID id = EsonID.from(name);
        symbolTableMap.put(id, value);
    }

    protected abstract void init();

    @Override
    protected Map<EsonID, EsonValue> variables() {
        init();
        return symbolTableMap;
    }

}

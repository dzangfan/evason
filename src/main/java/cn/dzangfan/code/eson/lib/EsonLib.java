package cn.dzangfan.code.eson.lib;

import java.util.Map;

import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonUndefinedIDException;
import cn.dzangfan.code.eson.rumtime.Environment;

public abstract class EsonLib {
    protected Map<EsonID, EsonValue> variables() {
        return Map.of();
    }

    public abstract String name();

    public void inject(Environment environment) {
        Map<EsonID, EsonValue> variables = variables();
        variables.forEach((id, value) -> {
            if (environment.isDefined(id.getName())) {
                EsonID oldId;
                try {
                    oldId = environment.find(id).getId();
                } catch (EsonUndefinedIDException e) {
                    throw EsonRuntimeException.causedBy(e);
                }
                EsonRedefinitionException re
                        = new EsonRedefinitionException(oldId, id);
                throw EsonRuntimeException.causedBy(re);
            }
        });
        variables.forEach((id, value) -> {
            try {
                environment.define(id, value);
            } catch (EsonRedefinitionException e) {
                throw EsonRuntimeException.causedBy(e);
            }
        });
    }
}

package cn.dzangfan.code.eson.lang;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.Evaluate;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.rumtime.Environment;

public class EsonScriptLoader {
    private Map<String, EsonValue> cacheMap;

    private EsonScriptLoader() {
        super();
        this.cacheMap = new HashMap<String, EsonValue>();
    }

    public static EsonScriptLoader getInstance() {
        return new EsonScriptLoader();
    }

    public EsonValue load(String path, Environment environment, boolean force) {
        try {
            Path realPath = Paths.get(path).toRealPath();
            String realPathString = realPath.toString();
            if (!force && cacheMap.containsKey(realPathString)) {
                return cacheMap.get(realPathString);
            }
            CharStream charStream = CharStreams.fromPath(realPath);
            EsonValue value = EsonValueReader.from(charStream).toEsonValue()
                    .on(Evaluate.in(environment));
            cacheMap.put(realPathString, value);
            return value;
        } catch (IOException e) {
            throw EsonRuntimeException.causedBy(e);
        }
    }

    public EsonValue load(String path, Environment environment) {
        return load(path, environment, false);
    }

}

package cn.dzangfan.code.eson.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EsonArray extends EsonValue {

    private List<EsonValue> content;

    private Optional<EsonID> maybeRest;

    private EsonArray(List<EsonValue> content) {
        this(content, Optional.empty());
    }

    private EsonArray(List<EsonValue> content, Optional<EsonID> maybeRest) {
        super();
        this.content = content;
        this.maybeRest = maybeRest;
    }

    public static EsonArray from(Collection<EsonValue> content) {
        return new EsonArray(content.stream().toList());
    }

    public static EsonArray from(EsonValue... content) {
        return new EsonArray(new ArrayList<EsonValue>(Arrays.asList(content)));
    }

    public EsonArray withRest(EsonID id) {
        return new EsonArray(content, Optional.of(id));
    }

    public EsonArray withoutRest() {
        return new EsonArray(content);
    }

    public List<EsonValue> getContent() {
        return content;
    }

    public void setContent(List<EsonValue> content) {
        this.content = content;
    }

    public Optional<EsonID> getMaybeRest() {
        return maybeRest;
    }

    public void setMaybeRest(Optional<EsonID> maybeRest) {
        this.maybeRest = maybeRest;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenArray(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonArray array && array != null) {
            return getContent().equals(array.getContent())
                    && maybeRest.equals(array.maybeRest);
        }
        return false;
    }

}

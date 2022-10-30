package cn.dzangfan.code.eson.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EsonObject extends EsonValue {

    private List<Entry> content;

    private Optional<EsonValue> maybeRest;

    public static class Entry {
        private EsonID key;
        private EsonValue value;

        public EsonValue getValue() {
            return value;
        }

        public EsonID getKey() {
            return key;
        }

        public void setKey(EsonID key) {
            this.key = key;
        }

        public void setValue(EsonValue value) {
            this.value = value;
        }

        private Entry(EsonID key, EsonValue value) {
            super();
            this.key = key;
            this.value = value;
        }

        public static Entry from(String key, EsonValue value) {
            return from(EsonID.from(key), value);
        }

        public static Entry from(EsonID key, EsonValue value) {
            return new Entry(key, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Entry entry && entry != null) {
                return key.equals(entry.key) && value.equals(entry.value);
            }
            return false;
        }

    }

    public List<Entry> getContent() {
        return content;
    }

    public void setContent(List<Entry> content) {
        this.content = content;
    }

    public Optional<EsonValue> getMaybeRest() {
        return maybeRest;
    }

    public void setMaybeRest(Optional<EsonValue> maybeRest) {
        this.maybeRest = maybeRest;
    }

    private EsonObject(List<Entry> content) {
        this(content, Optional.empty());
    }

    private EsonObject(List<Entry> content, Optional<EsonValue> maybeRest) {
        super();
        this.content = content;
        this.maybeRest = maybeRest;
    }

    public static EsonObject from(Collection<Entry> content) {
        return new EsonObject(content.stream().toList());
    }

    public static EsonObject from(Entry... content) {
        return new EsonObject(
                new ArrayList<EsonObject.Entry>(Arrays.asList(content)));
    }

    public EsonObject withRest(EsonValue value) {
        return new EsonObject(content, Optional.of(value));
    }

    public EsonObject withoutRest() {
        return new EsonObject(content);
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenObject(this);
    }

    public EsonValue add(EsonID id, EsonValue value) {
        if (content.stream().anyMatch(entry -> {
            return entry.getKey().getName().equals(id.getName());
        })) {
            String message = String.format("Key %s has appeared", id.getName());
            throw new IllegalArgumentException(message);
        }
        content.add(Entry.from(id, value));
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonObject object && obj != null) {
            return content.equals(object.content)
                    && maybeRest.equals(object.maybeRest);
        }
        return false;
    }

}

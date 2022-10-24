package cn.dzangfan.code.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EsonObject extends EsonValue {

    private List<Entry> content;

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
    }

    public List<Entry> getContent() {
        return content;
    }

    public void setContent(List<Entry> content) {
        this.content = content;
    }

    private EsonObject(List<Entry> content) {
        super();
        this.content = content;
    }

    public static EsonObject from(Collection<Entry> content) {
        return new EsonObject(content.stream().toList());
    }

    public static EsonObject from(Entry... content) {
        return new EsonObject(Arrays.asList(content));
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

}

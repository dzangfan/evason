package cn.dzangfan.code.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DependenceTree<T> {
    private T data;
    private List<DependenceTree<T>> depending;
    private List<DependenceTree<T>> depended;

    public List<DependenceTree<T>> getDepending() {
        return depending;
    }

    public void setDepending(List<DependenceTree<T>> depending) {
        this.depending = depending;
    }

    public List<DependenceTree<T>> getDepended() {
        return depended;
    }

    public void setDepended(List<DependenceTree<T>> depended) {
        this.depended = depended;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private DependenceTree(T data, List<DependenceTree<T>> depending,
            List<DependenceTree<T>> depended) {
        super();
        this.data = data;
        this.depending = depending;
        this.depended = depended;
    }

    public static <T> DependenceTree<T> of(T data) {
        return new DependenceTree<T>(data, new ArrayList<DependenceTree<T>>(),
                new ArrayList<DependenceTree<T>>());
    }

    public void depends(DependenceTree<T> other) {
        if (!depending.contains(other)) {
            depending.add(other);
        }
        if (!other.depended.contains(this)) {
            other.depended.add(this);
        }
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof @SuppressWarnings("rawtypes") DependenceTree other) {
            return other.data.equals(data);
        } else
            return false;
    }

    public static class Visitor<T> {
        private T data;
        private List<Visitor<T>> depending;
        private boolean visited;
        private boolean visiting;
        private Consumer<T> consumer;

        private Visitor(T data, List<Visitor<T>> depending, boolean visited,
                boolean visiting, Consumer<T> consumer) {
            super();
            this.data = data;
            this.depending = depending;
            this.visited = visited;
            this.visiting = visiting;
            this.consumer = consumer;
        }

        private boolean visit() {
            if (visited)
                return true;
            if (visiting == true) {
                return false;
            }
            visiting = true;
            for (Visitor<T> visitor : depending) {
                boolean ok = visitor.visit();
                if (!ok)
                    return false;
            }
            visiting = false;
            consumer.accept(data);
            visited = true;
            return true;
        }

        private static <T> Visitor<T> from(DependenceTree<T> tree,
                                           Consumer<T> consumer) {

            List<Visitor<T>> depending = tree.getDepending().stream()
                    .map(t -> from(t, consumer)).toList();
            return new Visitor<T>(tree.getData(), depending, false, false,
                    consumer);
        }

        public static <T> Optional<List<T>> order(DependenceTree<T> tree) {
            List<T> result = new ArrayList<T>();
            boolean ok = from(tree, t -> result.add(t)).visit();
            return ok ? Optional.of(result) : Optional.empty();
        }
    }
}

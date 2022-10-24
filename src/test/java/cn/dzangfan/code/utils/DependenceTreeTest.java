package cn.dzangfan.code.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cn.dzangfan.code.utils.DependenceTree.Visitor;

class DependenceTreeTest {

    DependenceTree<Integer> t0, t1, t2, t3, t4;

    @BeforeEach
    void setup() {
        t0 = DependenceTree.of(0);
        t1 = DependenceTree.of(1);
        t2 = DependenceTree.of(2);
        t3 = DependenceTree.of(3);
        t4 = DependenceTree.of(4);
        t0.depends(t1);
        t0.depends(t2);
        t0.depends(t4);
        t1.depends(t3);
        t2.depends(t3);
        t3.depends(t4);
    }

    @Test
    void testDepends() {
        assertTrue(t0.getDepending().contains(t1));
        assertTrue(t0.getDepending().contains(t2));
        assertTrue(t0.getDepending().contains(t4));
        assertTrue(t1.getDepending().contains(t3));
        assertTrue(t2.getDepending().contains(t3));
        assertTrue(t3.getDepending().contains(t4));
    }

    @Test
    void testVisitor() {
        Optional<List<Integer>> maybeList = Visitor.order(t0);
        assertTrue(maybeList.isPresent());
        List<Integer> list = maybeList.get();
        System.out.println(list);
        assertTrue(list.indexOf(0) > list.indexOf(1));
        assertTrue(list.indexOf(0) > list.indexOf(2));
        assertTrue(list.indexOf(0) > list.indexOf(4));
        assertTrue(list.indexOf(1) > list.indexOf(3));
        assertTrue(list.indexOf(2) > list.indexOf(3));
        assertTrue(list.indexOf(3) > list.indexOf(4));
        assertEquals(new HashSet<Integer>(list).size(), list.size());
    }

}

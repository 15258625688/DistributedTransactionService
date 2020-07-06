package com.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于ConcurrentHashMap线程安全的特性对ConcurrentHashSet的实现类
 * 
 * @author c00342
 *
 * @param <E>
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ConcurrentHashMap<E, Object> map;

    private final Object VALUE = new Object();

    public ConcurrentHashSet() {
	map = new ConcurrentHashMap<E, Object>();
    }

    @Override
    public int size() {
	return map.size();
    }

    @Override
    public boolean contains(Object o) {
	return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
	return map.keySet().iterator();
    }

    @Override
    public boolean add(E e) {
	return map.put(e, VALUE) == null;
    }

    @Override
    public boolean remove(Object o) {
	return map.remove(o) == VALUE;
    }

    @Override
    public void clear() {
	map.clear();
    }

}

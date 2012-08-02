/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.map.mutable;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.procedure.ObjectIntProcedure;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.map.ConcurrentMutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.procedure.MapEntryToProcedure2;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.internal.IterableIterate;

/**
 * A simple concurrent implementation of MutableMap which uses java.util.concurrent.ConcurrentHashMap for its underlying
 * concurrent Map implementation.
 *
 * @deprecated since 2.0
 */
@Deprecated
public final class ConcurrentMutableHashMap<K, V>
        extends AbstractMutableMap<K, V>
        implements ConcurrentMutableMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String JAVA_SPECIFICATION_VERSION = System.getProperty("java.specification.version");

    private final ConcurrentMap<K, V> delegate;

    private ConcurrentMutableHashMap()
    {
        this(new ConcurrentHashMap<K, V>());
    }

    public ConcurrentMutableHashMap(ConcurrentMap<K, V> delegate)
    {
        this.delegate = delegate;
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap()
    {
        return new ConcurrentMutableHashMap<NK, NV>();
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(int initialCapacity)
    {
        return new ConcurrentMutableHashMap<NK, NV>(new ConcurrentHashMap<NK, NV>(initialCapacity));
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        return new ConcurrentMutableHashMap<NK, NV>(new ConcurrentHashMap<NK, NV>(initialCapacity, loadFactor, concurrencyLevel));
    }

    public static <NK, NV> ConcurrentMutableHashMap<NK, NV> newMap(Map<NK, NV> map)
    {
        return new ConcurrentMutableHashMap<NK, NV>(new ConcurrentHashMap<NK, NV>(map));
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withKeyValue(K key, V value)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withKeyValue(key, value);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withAllKeyValues(keyValues);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withAllKeyValueArguments(keyValues);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withoutKey(K key)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withoutKey(key);
    }

    @Override
    public ConcurrentMutableHashMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        return (ConcurrentMutableHashMap<K, V>) super.withoutAllKeys(keys);
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public MutableMap<K, V> clone()
    {
        return ConcurrentMutableHashMap.newMap(this.delegate);
    }

    @Override
    public <K, V> MutableMap<K, V> newEmpty(int capacity)
    {
        return ConcurrentMutableHashMap.newMap();
    }

    @Override
    public boolean notEmpty()
    {
        return !this.delegate.isEmpty();
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        IterableIterate.forEach(this.delegate.values(), procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.delegate.values(), objectIntProcedure);
    }

    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.values().iterator();
    }

    public V remove(Object key)
    {
        return this.delegate.remove(key);
    }

    public Set<K> keySet()
    {
        return this.delegate.keySet();
    }

    public Collection<V> values()
    {
        return this.delegate.values();
    }

    public Set<Entry<K, V>> entrySet()
    {
        if ("1.5".equals(JAVA_SPECIFICATION_VERSION))
        {
            return new SafeEntrySetAdapter<K, V>(this.delegate.entrySet());
        }
        return this.delegate.entrySet();
    }

    public void clear()
    {
        this.delegate.clear();
    }

    public MutableMap<K, V> newEmpty()
    {
        return ConcurrentMutableHashMap.newMap();
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        IterableIterate.forEach(this.delegate.values(), procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        IterableIterate.forEach(this.delegate.keySet(), procedure);
    }

    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        IterableIterate.forEach(this.delegate.entrySet(), new MapEntryToProcedure2<K, V>(procedure));
    }

    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    public V put(K key, V value)
    {
        return this.delegate.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> map)
    {
        this.delegate.putAll(map);
    }

    public <E> MutableMap<K, V> collectKeysAndValues(Collection<E> collection, Function<? super E, ? extends K> keyFunction, Function<? super E, ? extends V> valueFunction)
    {
        Iterate.addToMap(collection, keyFunction, valueFunction, this.delegate);
        return this;
    }

    public V removeKey(K key)
    {
        return this.delegate.remove(key);
    }

    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            V blockValue = function.value();
            V putResult = this.delegate.putIfAbsent(key, blockValue);
            return putResult == null ? blockValue : putResult;
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            V functionValue = function.valueOf(parameter);
            V putResult = this.delegate.putIfAbsent(key, functionValue);
            return putResult == null ? functionValue : putResult;
        }
        return result;
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return function.value();
        }
        return result;
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return value;
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentWith(K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        V result = this.delegate.get(key);
        if (result == null)
        {
            return function.valueOf(parameter);
        }
        return result;
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        V result = this.delegate.get(key);
        return result == null ? null : function.valueOf(result);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.delegate.values(), procedure, parameter);
    }

    public V putIfAbsent(K key, V value)
    {
        return this.delegate.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value)
    {
        return this.delegate.remove(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue)
    {
        return this.delegate.replace(key, oldValue, newValue);
    }

    public V replace(K key, V value)
    {
        return this.delegate.replace(key, value);
    }

    private static final class SafeEntrySetAdapter<K, V>
            extends AbstractSet<Entry<K, V>>
    {
        private final Set<Entry<K, V>> delegate;

        private SafeEntrySetAdapter(Set<Entry<K, V>> newDelegate)
        {
            this.delegate = newDelegate;
        }

        @Override
        public Iterator<Entry<K, V>> iterator()
        {
            return new Iterator<Entry<K, V>>()
            {
                private final Iterator<Entry<K, V>> entryIterator = SafeEntrySetAdapter.this.delegate.iterator();

                public boolean hasNext()
                {
                    return this.entryIterator.hasNext();
                }

                public Entry<K, V> next()
                {
                    Entry<K, V> superNext = this.entryIterator.next();
                    return ImmutableEntry.of(superNext.getKey(), superNext.getValue());
                }

                public void remove()
                {
                    this.entryIterator.remove();
                }
            };
        }

        @Override
        public int size()
        {
            return this.delegate.size();
        }
    }
}

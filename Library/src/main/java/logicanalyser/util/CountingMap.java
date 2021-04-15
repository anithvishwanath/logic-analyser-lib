package logicanalyser.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class CountingMap<K> {
	private final Map<K, MutableInt> count;
	
	public CountingMap() {
		count = Maps.newHashMap();
	}
	
	public void increment(K key) {
		MutableInt value = count.get(key);
		if (value == null) {
			value = new MutableInt(1);
			count.put(key, value);
		} else {
			value.increment();
		}
	}
	
	public void add(K key, int toAdd) {
		MutableInt value = count.get(key);
		if (value == null) {
			value = new MutableInt(toAdd);
			count.put(key, value);
		} else {
			value.add(toAdd);
		}
	}
	
	public void set(K key, int toSet) {
		MutableInt value = count.get(key);
		if (value == null) {
			value = new MutableInt(toSet);
			count.put(key, value);
		} else {
			value.setValue(toSet);
		}
	}
	
	public void reset(K key) {
		count.remove(key);
	}
	
	public void resetAll() {
		count.clear();
	}
	
	public int get(K key) {
		MutableInt value = count.get(key);
		if (value == null) {
			return 0;
		} else {
			return value.intValue();
		}
	}
	
	public Map<K, Integer> toMap() {
		return Maps.transformValues(count, MutableInt::getValue);
	}
	
	public <V> Map<K, V> toMap(Function<Integer, V> transformer) {
		Map<K, V> dest = Maps.newHashMap();
		for (Entry<K, MutableInt> entry : count.entrySet()) {
			dest.put(entry.getKey(), transformer.apply(entry.getValue().getValue()));
		}
		
		return dest;
	}
	
	public <V> Map<K, V> toMapWithKey(BiFunction<K, Integer, V> transformer) {
		Map<K, V> dest = Maps.newHashMap();
		for (Entry<K, MutableInt> entry : count.entrySet()) {
			dest.put(entry.getKey(), transformer.apply(entry.getKey(), entry.getValue().getValue()));
		}
		
		return dest;
	}
	
	public <E, V> Map<E, V> toMap(Function<K, E> keyTransformer, Function<Integer, V> valueTransformer) {
		Map<E, V> dest = Maps.newHashMap();
		
		for (Entry<K, MutableInt> entry : count.entrySet()) {
			dest.put(
				keyTransformer.apply(entry.getKey()),
				valueTransformer.apply(entry.getValue().getValue())
			);
		}
		
		return dest;
	}
}

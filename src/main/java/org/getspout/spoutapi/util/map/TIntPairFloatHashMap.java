/*
 * This file is part of SpoutcraftPlugin.
 *
 * Copyright (c) 2011 SpoutcraftDev <http://spoutcraft.org//>
 * SpoutcraftPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutcraftPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spoutapi.util.map;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long float hashmap in the backend.
 */
public class TIntPairFloatHashMap {
	private TLongFloatHashMap map;

	public TIntPairFloatHashMap() {
		map = new TLongFloatHashMap(100);
	}

	public TIntPairFloatHashMap(int capacity) {
		map = new TLongFloatHashMap(capacity);
	}

	public float put(int key1, int key2, float value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}

	public float get(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(float val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongFloatIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public float remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TFloatCollection valueCollection() {
		return map.valueCollection();
	}

	public float[] values() {
		return map.values();
	}
}

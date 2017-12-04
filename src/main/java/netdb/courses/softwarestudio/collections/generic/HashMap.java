package netdb.courses.softwarestudio.collections.generic;

/**
 * Array-based, linear probing hash map. The <code>null</code> key and value are
 * allowed.
 */
public class HashMap<K, V> implements Map<K, V> {
	/**
	 * Must be a power of two.
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * Must be a power of two.
	 */
	public static final int MAXIMUM_CAPACITY = 1 << 30;

	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * Class for the entries kept inside this map.
	 */
	//必須改成static，why?
	private static class Entry<K, V> {
		// access modifiers don't matter for a private class
		final K key;
		V value;
		Entry<K, V> next;

		Entry(K k, V v, Entry<K, V> n) {
			key = k;
			value = v;
			next = n;
		}
	}

	/**
	 * Each element represents a bucket of entries. The length of this array is
	 * doubled when size >= threshold.
	 */
	private Entry<K, V>[] table;

	private int size;

	private final float loadFactor;

	/**
	 * The next size to double the table. Equals (capacity * load factor).
	 */
	private int threshold;

	public HashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public HashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public HashMap(int initCapacity, float loadFactor) {
		if (initCapacity < 1 || initCapacity > MAXIMUM_CAPACITY)
			throw new IllegalArgumentException();
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException();

		// find a power of 2 which is >= initialCapacity
		int capacity = 1;
		while (capacity < initCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
		size = 0;
	}

	/**
	 * Obtains an index for the specified key. The <code>null</code> key is
	 * hashed to index 0. The current implementation is simplistic that may
	 * cause funneling. To avoid funneling, see <a
	 * href="http://burtleburtle.net/bob/hash/evahash.html">this post</a>.
	 * 
	 * @param key
	 * @param capacity
	 * @return
	 */
	private static int indexFor(Object key, int capacity) {
		return key == null ? 0 : key.hashCode() >= 0 ? key.hashCode()
				% capacity : key.hashCode() % capacity + capacity;
	}

	private void addEntry(int bucketIndex, K k, V v) {
		Entry<K, V> entry = table[bucketIndex];
		table[bucketIndex] = new Entry(k, v, entry);
		if (size++ >= threshold)
			resize(2 * table.length);
	}

	/**
	 * @param k
	 *            key, may be <code>null</code>
	 * @return
	 */
	private Entry<K, V> getEntry(Object k) {
		int i = indexFor(k, table.length);
		for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
			if (entry.key == k || (k != null && k.equals(entry.key)))
				return entry;
		}
		return null;
	}

	/**
	 * @param k
	 *            key, may be <code>null</code>
	 * @return
	 */
	private Entry<K, V> removeEntry(Object k) {
		int i = indexFor(k, table.length);
		Entry<K, V> prev = table[i];
		Entry<K, V> entry = prev;
		while (entry != null) {
			Entry<K, V> next = entry.next;
			if (entry.key == k || (k != null && k.equals(entry.key))) {
				size--;
				if (prev == entry)
					table[i] = next; // entry is the first in bucket
				else
					prev.next = next;
				return entry;
			}
			prev = entry;
			entry = next;
		}
		return entry;
	}

	/**
	 * @param k
	 *            key, may be <code>null</code>
	 */
	@Override
	public V put(K k, V v) throws OverflowException {
		int i = indexFor(k, table.length);
		for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
			if (entry.key == k || (k != null && k.equals(entry.key))) {
				V oldValue = entry.value;
				entry.value = v;
				return oldValue;
			}
		}
		// no existing value
		addEntry(i, k, v);
		return null;
	}

	/**
	 * @param k
	 *            key, may be <code>null</code>
	 */
	@Override
	public boolean containsKey(Object k) {
		return getEntry(k) != null;
	}

	/**
	 * @param v
	 *            value, may be <code>null</code>
	 */
	@Override
	public boolean containsValue(Object v) {
		for (int i = 0; i < table.length; i++)
			for (Entry<K, V> e = table[i]; e != null; e = e.next)
				if (e.value == v || (v != null && v.equals(e.value)))
					return true;
		return false;
	}

	/**
	 * @param k
	 *            key, may be <code>null</code>
	 */
	@Override
	public V get(Object k) {
		Entry<K, V> entry = getEntry(k);
		return (entry == null ? null : entry.value);
	}

	@Override
	public V remove(Object k) {
		Entry<K, V> entry = removeEntry(k);
		return (entry == null ? null : entry.value);
	}

	@Override
	public int size() {
		return size;
	}

	private void resize(int newCapacity) throws OverflowException {
		if (table.length == MAXIMUM_CAPACITY) {
			throw new OverflowException();
		}

		Entry<K, V>[] newTable = new Entry[newCapacity];
		// rehash all entries into newTable
		for (int j = 0; j < table.length; j++) {
			Entry<K, V> e = table[j];
			while (e != null) {
				Entry<K, V> next = e.next;
				// put e
				int i = indexFor(e.key, newCapacity);
				e.next = newTable[i];
				newTable[i] = e;
				e = next;
			}
		}
		table = newTable; // oldTable will be garbage collected
		threshold = (int) (newCapacity * loadFactor);
	}
	
}

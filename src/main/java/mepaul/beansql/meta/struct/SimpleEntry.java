package mepaul.beansql.meta.struct;

import java.util.Map.Entry;

public class SimpleEntry<K,V> implements Entry<K, V>
{
	public K getKey()
	{
		return key;
	}

	public V getValue()
	{
		return value;
	}
	
	public V setValue(V value)
	{
		V oldValue = this.value ;
		this.value = value;
		return oldValue;
	}
	public SimpleEntry(K key,V value)
	{
		this.key = key;
		this.value = value;
	}
	public String toString(){
		return key+":"+value;
	}
	private  K key;
	private  V value;
}

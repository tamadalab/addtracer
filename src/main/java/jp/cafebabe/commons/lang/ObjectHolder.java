package jp.cafebabe.commons.lang;

/**
 * @author Haruaki TAMADA
 */
public class ObjectHolder<K, V>{
    private K key;
    private V value;

    public ObjectHolder(K key, V value){
        setKey(key);
        setValue(value);
    }

    public void setKey(K key){
        this.key = key;
    }

    public void setValue(V value){
        this.value = value;
    }

    public K getKey(){
        return key;
    }

    public V getValue(){
        return value;
    }
}

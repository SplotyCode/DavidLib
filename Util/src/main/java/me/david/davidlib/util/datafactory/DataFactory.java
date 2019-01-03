package me.david.davidlib.util.datafactory;

import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class DataFactory {

    private Map<String, Object> data = new HashMap<>();

    public DataFactory() {}

    public DataFactory(Map<String, Object> data) {
        this.data = data;
    }

    public <T> T getData(DataKey<T> key) {
        return (T) data.get(key.name);
    }

    /*
     * This will use the name instead of the key name
     * The Key is only for providing the Generic
     */
    public <T> T getData(String name, DataKey<T> key) {
        return (T) data.get(name);
    }

    public <T> void putData(DataKey<T> key, T obj) {
        data.putIfAbsent(key.name, obj);
    }

    public <T> void putData(String name, DataKey<T> key, T obj) {
        data.putIfAbsent(name, obj);
    }

    public Map<String, Object> getMap() {
        return data;
    }

    public void setMap(Map<String, Object> map) {
        data = map;
    }

    public boolean containsData(String name) {
        return data.containsKey(name);
    }

    public boolean containsData(DataKey<?> key) {
        return data.containsKey(key.getName());
    }

    public int getDataSize() {
        return data.size();
    }
}
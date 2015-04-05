package hu.uniobuda.nik.parentalcontrol;

import java.util.Hashtable;

public class BlockerHashTable {

    private static Hashtable<String, Boolean> tempAllowedPackages = new Hashtable<String, Boolean>();

    public static void clear() {
        tempAllowedPackages.clear();
    }

    public static boolean getBoolean(String key) {
        boolean result = tempAllowedPackages.get(key);
        return result;
    }

    public static void setBoolean(String key, Boolean value) {
        tempAllowedPackages.put(key, value);
        // Log.d("setBoolean", key+" " + value);
    }

    public static boolean containsBoolean(String key) {
        boolean result = tempAllowedPackages.containsKey(key);
        return result;
    }

    public static boolean isEmpty() {
        return tempAllowedPackages.isEmpty();
    }

}



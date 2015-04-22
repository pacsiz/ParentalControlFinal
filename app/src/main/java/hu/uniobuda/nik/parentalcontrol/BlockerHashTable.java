package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Hashtable;
import java.util.Map;

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
    }

    public static boolean containsBoolean(String key) {
        boolean result = tempAllowedPackages.containsKey(key);
        return result;
    }

    public static void deleteBoolean(String key)
    {
        tempAllowedPackages.remove(key);
    }

    public static void refresh(Context context)
    {
        tempAllowedPackages.clear();
        SharedPreferences sh = context.getSharedPreferences(context.getString
                (R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        Map<String, ?> map = sh.getAll();
        for (Map.Entry entry : map.entrySet()) {
            tempAllowedPackages.put(entry.getKey().toString(), true);
            Log.d("pname", entry.getKey().toString());
        }
        tempAllowedPackages.put("hu.uniobuda.nik.parentalcontrol", true);
        tempAllowedPackages.put("com.android.settings", true);
        tempAllowedPackages.put("com.android.packageinstaller", true);

    }

    public static boolean isEmpty() {
        return tempAllowedPackages.isEmpty();
    }

}



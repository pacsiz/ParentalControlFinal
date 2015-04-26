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
        return tempAllowedPackages.get(key);
    }

    public static void setBoolean(String key, Boolean value) {
        tempAllowedPackages.put(key, value);
    }

    public static boolean containsBoolean(String key) {
        return tempAllowedPackages.containsKey(key);
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
        //Log.d("BlockerHashTable", "refreshing");
        Map<String, ?> map = sh.getAll();
        for (Map.Entry entry : map.entrySet()) {
            tempAllowedPackages.put(entry.getKey().toString(), true);
            Log.d("BlockerHashTable", "Put in package: "+entry.getKey().toString());
        }
        /*tempAllowedPackages.put("hu.uniobuda.nik.parentalcontrol", true);
        tempAllowedPackages.put("com.android.settings", true);
        tempAllowedPackages.put("com.android.packageinstaller", true);*/

    }

    public static boolean isEmpty() {
        return tempAllowedPackages.isEmpty();
    }

}



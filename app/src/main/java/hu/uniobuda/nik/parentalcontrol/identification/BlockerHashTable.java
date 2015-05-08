package hu.uniobuda.nik.parentalcontrol.identification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Hashtable;
import java.util.Map;

import hu.uniobuda.nik.parentalcontrol.R;

public class BlockerHashTable {

    private static Hashtable<String, Boolean> packagesToBlock = new Hashtable<String, Boolean>();

    public static void clear() {
        packagesToBlock.clear();
    }

    public static boolean getBoolean(String key) {
        return packagesToBlock.get(key);
    }

    public static void setBoolean(String key, Boolean value) {
        packagesToBlock.put(key, value);
    }

    public static boolean containsBoolean(String key) {
        return packagesToBlock.containsKey(key);
    }

    public static void deleteBoolean(String key)
    {
        packagesToBlock.remove(key);
    }

    public static void refresh(Context context)
    {
        packagesToBlock.clear();
        SharedPreferences sh = context.getSharedPreferences(context.getString
                (R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        Map<String, ?> map = sh.getAll();
        for (Map.Entry entry : map.entrySet()) {
            packagesToBlock.put(entry.getKey().toString(), true);
            //Log.d("BlockerHashTable", "Put in package: "+entry.getKey().toString());
        }
        /*packagesToBlock.put("hu.uniobuda.nik.parentalcontrol", true);
        packagesToBlock.put("com.android.settings", true);
        packagesToBlock.put("com.android.packageinstaller", true);*/

    }

    public static boolean isEmpty() {
        return packagesToBlock.isEmpty();
    }

}



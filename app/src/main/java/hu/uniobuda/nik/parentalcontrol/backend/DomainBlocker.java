package hu.uniobuda.nik.parentalcontrol.backend;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import hu.uniobuda.nik.parentalcontrol.R;

public class DomainBlocker {

    public static void blockAllURL(Context context) {
        final SharedPreferences sh = context.getSharedPreferences(context.getString
                (R.string.SHAREDPREFERENCE_URLS), Context.MODE_PRIVATE);
        new Thread() {
            @Override
            public void run() {
                Map<String, ?> map = sh.getAll();
                for (Map.Entry entry : map.entrySet()) {
                    DomainBlocker.blockDomain(entry.getKey().toString());
                }
            }
        }.start();
    }

    public static void unblockAllURL(Context context) {
        final SharedPreferences sh = context.getSharedPreferences(context.getString
                (R.string.SHAREDPREFERENCE_URLS), Context.MODE_PRIVATE);
        new Thread() {
            @Override
            public void run() {
                Map<String, ?> map = sh.getAll();
                for (Map.Entry entry : map.entrySet()) {
                    DomainBlocker.unblockDomain(entry.getKey().toString());
                }
            }
        }.start();
    }

    public static void blockDomain(String domain) {
        for (InetAddress address : resolveDomain(domain)) {
            blockIP(address.getHostAddress());
        }
    }

    public static void unblockDomain(String domain) {
        for (InetAddress address : resolveDomain(domain)) {
            unblockIP(address.getHostAddress());
        }
    }

    public static boolean blockIP(String ip) {
        boolean alreadyBlocked = false;

        for (Map.Entry<Integer, String> entry : enumerateBlocks().entrySet()) {
            if (entry.getValue().equals(ip))
                alreadyBlocked = true;
        }

        if (!alreadyBlocked)
        {
            runCommand(new String[]{"iptables -I INPUT -s " + ip + " -j DROP"});
        }
        //else
            //Log.d("DomaniBlocker",ip + " is already blocked.");

        return alreadyBlocked;
    }

    public static boolean unblockIP(String ip) {
        boolean wasBlocked = false;

        for (Map.Entry<Integer, String> entry : enumerateBlocks().entrySet()) {
            if (entry.getValue().equals(ip)) {
                wasBlocked = true;
                runCommand(new String[]{"iptables -D INPUT " + entry.getKey()});
            }
        }
        return wasBlocked;
    }

    public static Map<Integer, String> enumerateBlocks() {
        String r = runCommand(new String[]{"iptables -L -n"});

        Map<Integer, String> blocked = new HashMap<Integer, String>();

        boolean start = false;
        int id = 0;

        for (String s : r.split("\n")) {
            if (s.startsWith("target")) {
                start = true;
            }

            if (start) {
                if (s.startsWith("DROP")) {
                    blocked.put(id, s.substring(s.indexOf("--") + 2).trim().split(" ")[0]);
                }
                if (s.length() == 0) {
                    break;
                }
                id++;
            }
        }

        return blocked;
    }

    private static InetAddress[] resolveDomain(String domain) {
        InetAddress[] machines = new InetAddress[0];
        try {
            machines = InetAddress.getAllByName(domain);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return machines;
    }

    private static String runCommand(String[] cmds) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                buffer.append(sCurrentLine + "\n");
            }

            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}

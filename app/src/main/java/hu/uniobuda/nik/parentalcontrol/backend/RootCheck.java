package hu.uniobuda.nik.parentalcontrol.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RootCheck {

    public static boolean isDeviceRooted() {
        return (execSu() || new File("/system/app/Superuser.apk").exists());
    }

    private static boolean execSu() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return (in.readLine() != null);
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}

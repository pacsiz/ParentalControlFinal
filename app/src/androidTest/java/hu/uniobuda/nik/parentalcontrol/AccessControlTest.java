package hu.uniobuda.nik.parentalcontrol;

import android.test.AndroidTestCase;

import hu.uniobuda.nik.parentalcontrol.identification.AccessControl;

public class AccessControlTest extends AndroidTestCase {

    public void testChildAccessControl() throws Exception {
       assertEquals(true, AccessControl.childAccessControl("Dani", getContext())); //true esetén nincs blokkolás, false esetén van
       assertEquals(true,AccessControl.childAccessControl("nem létező gyermek", getContext()));
    }
}
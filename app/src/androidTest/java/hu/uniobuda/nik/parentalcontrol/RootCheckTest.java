package hu.uniobuda.nik.parentalcontrol;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import hu.uniobuda.nik.parentalcontrol.backend.RootCheck;

public class RootCheckTest extends AndroidTestCase {

    public void testRootCheck()
    {
        Assert.assertEquals(false, RootCheck.isDeviceRooted());
    }

}
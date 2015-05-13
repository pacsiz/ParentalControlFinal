package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import java.util.ArrayList;
import java.util.List;

import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.TestContext;
import hu.uniobuda.nik.parentalcontrol.identification.Blocker;


public class BlockerTest extends AndroidTestCase {

    private Blocker receiver;
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        receiver = new Blocker();
        context = new TestContext();
    }

    public void testReceiving() {
        Intent unlock = new Intent();
        unlock.setAction(getContext().getString(R.string.BROADCAST_UNLOCK));
        Intent newApp = new Intent();
        newApp.setAction(getContext().getString(R.string.BROADCAST_NEW_APP_STARTED));
        newApp.putExtra(getContext().getString(R.string.EXTRA_PACKAGE_NAME), "com.test.packagename");
        receiver.onReceive(context, unlock);
        receiver.onReceive(context,newApp);
        assertEquals(2, context.getReceivedIntents().size());

        Intent receivedUnlock = context.getReceivedIntents().get(0);
        assertEquals(getContext().getString(R.string.BROADCAST_UNLOCK),receivedUnlock.getAction());

        Intent receivedNewApp = context.getReceivedIntents().get(1);
        assertEquals(getContext().getString(R.string.BROADCAST_NEW_APP_STARTED),receivedNewApp.getAction());
        assertEquals("com.test.packagename", receivedNewApp.getStringExtra(getContext().getString(R.string.EXTRA_PACKAGE_NAME)));
    }


}


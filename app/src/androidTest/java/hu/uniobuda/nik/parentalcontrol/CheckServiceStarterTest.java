package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.TestContext;
import hu.uniobuda.nik.parentalcontrol.identification.Blocker;
import hu.uniobuda.nik.parentalcontrol.service.CheckServiceStarter;

public class CheckServiceStarterTest extends AndroidTestCase {

    private CheckServiceStarter receiver;
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        receiver = new CheckServiceStarter();
        context = new TestContext();
    }

    public void testReceiving() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_BOOT_COMPLETED);

        receiver.onReceive(context, i);
        assertEquals(1, context.getReceivedIntents().size());

        Intent received = context.getReceivedIntents().get(0);
        assertEquals(Intent.ACTION_BOOT_COMPLETED,received.getAction());
    }
}
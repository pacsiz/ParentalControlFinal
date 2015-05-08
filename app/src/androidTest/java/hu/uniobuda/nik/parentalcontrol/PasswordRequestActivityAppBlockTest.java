package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;

import junit.framework.TestCase;

public class PasswordRequestActivityAppBlockTest extends ActivityUnitTestCase<PasswordRequestActivity> {
    Intent i;
    Button ok;
    Button forgotPassword;
    PasswordRequestActivity activity;


    public PasswordRequestActivityAppBlockTest() {
        super(PasswordRequestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        i = new Intent(getInstrumentation()
                .getTargetContext(), PasswordRequestActivity.class);
        i.putExtra("hu.uniobuda.nik.extra.PACKAGE_NAME", "test");
        startActivity(i, null, null);
        activity = getActivity();
        ok = (Button) activity.findViewById(R.id.OK);
    }

    public void testIntent() {
        assertEquals(false, activity.deviceAccessControl);
        assertEquals("test", activity.getIntent().getStringExtra("hu.uniobuda.nik.extra.PACKAGE_NAME"));
    }

    public void testLifeCycle() {

        getInstrumentation().callActivityOnStop(activity);
        assertEquals(0, activity.actionCode);
        getInstrumentation().callActivityOnCreate(activity, null);
        activity.onBackPressed();
        assertEquals(0, activity.actionCode);
        ok.performClick();
        assertEquals(2, activity.actionCode);
    }
}
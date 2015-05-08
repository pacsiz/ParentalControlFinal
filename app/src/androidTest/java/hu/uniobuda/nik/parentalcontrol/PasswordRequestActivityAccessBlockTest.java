package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.TextView;

import junit.framework.TestCase;

public class PasswordRequestActivityAccessBlockTest extends ActivityUnitTestCase<PasswordRequestActivity> {
    Intent i;
    Button ok;
    Button forgotPassword;
    TextView passwordText;
    PasswordRequestActivity activity;

    public PasswordRequestActivityAccessBlockTest() {
        super(PasswordRequestActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        i = new Intent(getInstrumentation()
                .getTargetContext(), PasswordRequestActivity.class);
        startActivity(i, null, null);
        activity = getActivity();
        ok = (Button)activity.findViewById(R.id.OK);
        forgotPassword = (Button)activity.findViewById(R.id.btnForgotPassword);
        passwordText = (TextView)activity.findViewById(R.id.getPwText);
    }

    public void testUIElements()
    {
        assertEquals(activity.getString(R.string.OK),ok.getText());
        assertEquals(activity.getString(R.string.forgotPassword),forgotPassword.getText());
        assertEquals(activity.getString(R.string.getPw),passwordText.getText());
    }

    public void testIntentAccessBlock()
    {
        assertEquals(true,activity.deviceAccessControl);
        assertEquals(null,activity.getIntent().getStringExtra("hu.uniobuda.nik.extra.PACKAGE_NAME"));
    }

    public void testLifeCycleAccessBlock()
    {
        getInstrumentation().callActivityOnStop(activity);
        assertEquals(0, activity.actionCode);
        getInstrumentation().callActivityOnRestart(activity);
        assertEquals(3,activity.actionCode);
        ok.performClick();
        assertEquals(3,activity.actionCode);
    }
}
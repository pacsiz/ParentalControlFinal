package hu.uniobuda.nik.parentalcontrol;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

public class PasswordActivityTest extends ActivityInstrumentationTestCase2<PasswordActivity> {

    Solo solo;

    public PasswordActivityTest() {
        super(PasswordActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        setActivityInitialTouchMode(false);
    }

    public void testPassword()
    {
        solo.clickOnButton(solo.getString(R.string.save));
        assertTrue(solo.waitForText(solo.getString(R.string.noPassword)));
        solo.clickOnButton(solo.getString(R.string.OK));

        solo.enterText((EditText)solo.getView(R.id.newPassword),"aaa");
        solo.clickOnButton(solo.getString(R.string.save));
        assertTrue(solo.waitForText(solo.getString(R.string.shortPassword)));
        solo.clickOnButton(solo.getString(R.string.OK));

        solo.clearEditText((EditText)solo.getView(R.id.newPassword));
        solo.enterText((EditText)solo.getView(R.id.newPassword),"aaaa");
        solo.enterText((EditText)solo.getView(R.id.confirmPassword),"aaa");
        solo.clickOnButton(solo.getString(R.string.save));
        assertTrue(solo.waitForText(solo.getString(R.string.passwordFailMessage)));
        solo.clickOnButton(solo.getString(R.string.OK));

        solo.clearEditText((EditText)solo.getView(R.id.confirmPassword));
        solo.enterText((EditText)solo.getView(R.id.confirmPassword),"aaaa");
    }

    public void testPatternEmail()
    {
        solo.enterText((EditText)solo.getView(R.id.newPassword),"aaaa");
        solo.enterText((EditText)solo.getView(R.id.confirmPassword),"aaaa");
        solo.clearEditText((EditText)solo.getView(R.id.editText_email_address));
        solo.enterText((EditText)solo.getView(R.id.editText_email_address),"aaa");
        solo.clickOnButton(solo.getString(R.string.save));
        assertTrue(solo.waitForText(solo.getString(R.string.emailFailMessage)));
        solo.clickOnButton(solo.getString(R.string.OK));
        solo.clearEditText((EditText)solo.getView(R.id.editText_email_address));
        solo.enterText((EditText)solo.getView(R.id.editText_email_address),"a@a.hu");
    }

    public void testSave()
    {
        solo.enterText((EditText)solo.getView(R.id.newPassword),"aaaa");
        solo.enterText((EditText)solo.getView(R.id.confirmPassword),"aaaa");
        solo.clearEditText((EditText)solo.getView(R.id.editText_email_address));
        solo.enterText((EditText)solo.getView(R.id.editText_email_address),"a@a.hu");
        solo.clickOnButton(solo.getString(R.string.save));
        assertTrue(solo.waitForText(solo.getString(R.string.passwordChanged)));
    }

    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
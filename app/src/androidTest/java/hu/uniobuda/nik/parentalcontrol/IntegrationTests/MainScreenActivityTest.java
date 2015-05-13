package hu.uniobuda.nik.parentalcontrol.IntegrationTests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;

import hu.uniobuda.nik.parentalcontrol.HelpActivity;
import hu.uniobuda.nik.parentalcontrol.MainScreenActivity;
import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.SettingsActivity;

public class MainScreenActivityTest extends ActivityInstrumentationTestCase2<MainScreenActivity> {

    MainScreenActivity activity;
    Solo solo;

    public MainScreenActivityTest() {
        super(MainScreenActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        setActivityInitialTouchMode(false);
        activity = getActivity();
    }

    public void testStartSettings() {
        solo.clickOnButton(solo.getString(R.string.settings));
        solo.assertCurrentActivity("Wrong activity", SettingsActivity.class);
        solo.goBack();
    }

    public void testStartHelp() {
        solo.clickOnButton(solo.getString(R.string.help));
        solo.assertCurrentActivity("Wrong activity", HelpActivity.class);
        solo.goBack();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
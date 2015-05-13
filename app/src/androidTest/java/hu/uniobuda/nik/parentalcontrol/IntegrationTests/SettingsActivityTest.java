package hu.uniobuda.nik.parentalcontrol.IntegrationTests;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.robotium.solo.Solo;

import hu.uniobuda.nik.parentalcontrol.ChildListActivty;
import hu.uniobuda.nik.parentalcontrol.ChildPreferencesActivity;
import hu.uniobuda.nik.parentalcontrol.FilterAppsActivity;
import hu.uniobuda.nik.parentalcontrol.PasswordActivity;
import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.SetNewPersonActivity;
import hu.uniobuda.nik.parentalcontrol.SettingsActivity;
import hu.uniobuda.nik.parentalcontrol.URLActivity;
import hu.uniobuda.nik.parentalcontrol.backend.RootCheck;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {
    Solo solo;
    SettingsActivity activity;
    SharedPreferences sh;

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        setActivityInitialTouchMode(false);
        activity = getActivity();
        sh = activity.getSharedPreferences(activity.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
    }


    public void testPasswordActivity()
    {
        solo.clickOnText(solo.getString(R.string.newPassword));
        solo.assertCurrentActivity("Wrong activity",PasswordActivity.class);
        assertEquals(sh.getString(activity.getString(R.string.SHAREDPREFERENCE_EMAIL),""),solo.getEditText(2).getText().toString());
        solo.goBack();

    }

    public void testFilterAppsActivity()
    {
        solo.clickOnText(solo.getString(R.string.selectApps));
        solo.assertCurrentActivity("Wrong activity",FilterAppsActivity.class);
        solo.waitForView(solo.getView(R.id.appList));
        TextView text = (TextView)solo.getView(R.id.childName);
        assertEquals(solo.getString(R.string.followingPersonSettings)+" "+solo.getString(R.string.all),text.getText());
        solo.goBack();
    }

    public void testChildListActivity()
    {
        solo.clickOnText(solo.getString(R.string.title_childControl));
        solo.assertCurrentActivity("Wrong activity",ChildListActivty.class);
        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong activity", ChildPreferencesActivity.class);
        solo.goBack();
        solo.goBack();
    }

    public void testSetNewPersonActivity()
    {
        solo.clickOnText(solo.getString(R.string.setNewPerson));
        solo.assertCurrentActivity("Wrong activity",SetNewPersonActivity.class);
        solo.goBack();
    }

    public void testThreshold()
    {
        solo.clickOnText(solo.getString(R.string.predictValue_title));
        assertTrue(solo.waitForDialogToOpen());
        solo.clickOnButton(solo.getString(R.string.cancel));
        solo.goBack();
    }

    public void testURLSettings()
    {

        if(solo.isCheckBoxChecked(0))
        {
            solo.clickOnText(solo.getString(R.string.url_custom_title));
            solo.assertCurrentActivity("Wrong activity", URLActivity.class);
            solo.goBack();
        }
        else
        {
            solo.clickOnText(solo.getString(R.string.url_custom_title));
            assertTrue(solo.waitForDialogToOpen());
            solo.clickOnButton(solo.getString(R.string.OK));
        }

        boolean checked = solo.isCheckBoxChecked(0);
        solo.clickOnCheckBox(0);
        if(!RootCheck.isDeviceRooted() && !checked)
        {
            assertTrue(solo.waitForDialogToOpen());
            solo.clickOnButton(solo.getString(R.string.OK));
        }
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
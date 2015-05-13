package hu.uniobuda.nik.parentalcontrol.IntegrationTests;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import hu.uniobuda.nik.parentalcontrol.ChildListActivty;
import hu.uniobuda.nik.parentalcontrol.DeviceAccessSettingsActivity;
import hu.uniobuda.nik.parentalcontrol.FilterAppsActivity;
import hu.uniobuda.nik.parentalcontrol.R;


public class ChildListActivtyIntegrationTest extends ActivityInstrumentationTestCase2<ChildListActivty> {

    Solo solo;
    ListView list;
    String name;

    public ChildListActivtyIntegrationTest() {
        super(ChildListActivty.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        list = (ListView) getActivity().findViewById(R.id.childlistView);
        name = list.getItemAtPosition(0).toString();
    }

    public void testChildFilterApps()
    {
        solo.clickInList(0);
        solo.clickOnText(solo.getString(R.string.selectApps));
        solo.assertCurrentActivity("Wrong activity",FilterAppsActivity.class);
        solo.waitForView(solo.getView(R.id.appList));
        TextView text = (TextView)solo.getView(R.id.childName);
        assertEquals(solo.getString(R.string.followingPersonSettings)+" "+name,text.getText());
        solo.goBack();
    }

    public void testChildAccessControl()
    {
        solo.clickInList(0);
        solo.clickOnText(solo.getString(R.string.title_deviceAccess));
        solo.assertCurrentActivity("Wrong activity",DeviceAccessSettingsActivity.class);
        TextView text = (TextView)solo.getView(R.id.personName);
        assertEquals(name.toLowerCase(),text.getText().toString().toLowerCase());
        solo.goBack();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
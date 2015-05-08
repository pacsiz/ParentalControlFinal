package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

public class ChildListActivtyTest extends ActivityUnitTestCase<ChildListActivty> {

    ChildListActivty activity;
    TextView text;

    public ChildListActivtyTest() {
        super(ChildListActivty.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        Intent i = new Intent(getInstrumentation()
                .getTargetContext(), ChildListActivty.class);
        startActivity(i, null, null);
        activity = getActivity();
        text = (TextView) activity.findViewById(R.id.childListInfo);
    }

    public void testText()
    {
        if (activity.chilListView.getCount() != 0)
        {
            assertEquals("",text.getText());
        }
        else
        {
            assertEquals(activity.getString(R.string.emptyChildList),text.getText());
        }
    }
}
package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;


public class DeletePersonActivityTest extends ActivityUnitTestCase<DeletePersonActivity> {

    DeletePersonActivity activity;
    TextView text;

    public DeletePersonActivityTest() {
        super(DeletePersonActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        Intent i = new Intent(getInstrumentation()
                .getTargetContext(), DeletePersonActivity.class);
        startActivity(i, null, null);
        activity = getActivity();
        text = (TextView) activity.findViewById(R.id.personListInfo);
    }
    public void testText()
    {
        if (activity.personlistView.getCount() != 0)
        {
            assertEquals("",text.getText());
        }
        else
        {
            assertEquals(activity.getString(R.string.emptyPersonList),text.getText());
        }
    }
}
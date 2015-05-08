package hu.uniobuda.nik.parentalcontrol;

import android.content.Intent;
import android.test.mock.MockContext;

import java.util.ArrayList;
import java.util.List;

public class TestContext extends MockContext {
    private List<Intent> receivedIntents = new ArrayList<Intent>();

    @Override
    public void startActivity(Intent intent)//ez felülírható
    {
        receivedIntents.add(intent);
    }

    public List<Intent> getReceivedIntents() {
        return receivedIntents;
    }

}

package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LockScreenActivity extends Activity {
    public WindowManager winManager;
    public RelativeLayout wrapperView;
    Button unlock;
    BroadcastReceiver screenOn;


    @Override
    public void onAttachedToWindow() {
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        this.winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
        this.wrapperView = new RelativeLayout(getBaseContext());
        //unlock = (Button)wrapperView.findViewById(R.id.btnUnlock);
        getWindow().setAttributes(localLayoutParams);
        unlock = (Button)View.inflate(this, R.layout.activity_lock_screen, this.wrapperView).findViewById(R.id.btnUnlock);
        this.winManager.addView(this.wrapperView, localLayoutParams);

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(getString(R.string.BROADCAST_UNLOCK));
                sendBroadcast(i);
            }
        });

        screenOn = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent();
                i.setAction(getString(R.string.BROADCAST_UNLOCK));
                sendBroadcast(i);
            }
        };

        //registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));



    }

    public void onDestroy()
    {
        this.winManager.removeView(this.wrapperView);
        this.wrapperView.removeAllViews();
        unregisterReceiver(screenOn);
        super.onDestroy();
    }

}

package hu.uniobuda.nik.parentalcontrol.identification;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import hu.uniobuda.nik.parentalcontrol.camera.CameraSet;
import hu.uniobuda.nik.parentalcontrol.camera.CameraView;
import hu.uniobuda.nik.parentalcontrol.backend.FaceDetection;
import hu.uniobuda.nik.parentalcontrol.R;

public class CheckPersonActivity extends Activity {
    private static final int ACTION_NOT_SET = 0;
    private static final int ACTION_FINISH = 1;
    private static final int ACTION_SKIP = 2;
    private static final int ACTION_CHECK = 3;
    private static final int ACTION_LOCK = 4;
    private Camera camera = null;
    private CameraView cameraPreview;

    int pResult = -2;
    int fails = 0;
    String packageName;
    private SharedPreferences learnedPersons;
    boolean deviceAccessControl = false;
    int actionCode = ACTION_NOT_SET;
    int predictValue;
    Handler delay = new Handler();
    Runnable r;

    Button skip;

    String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_person);

        skip = (Button)findViewById(R.id.btnSkip);
        packageName = getIntent().getStringExtra(getString(R.string.EXTRA_PACKAGE_NAME));
        deviceAccessControl = packageName == null;

        if(deviceAccessControl && !(((KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode()))
        {
            actionCode = ACTION_LOCK;
        }

        learnedPersons = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        SharedPreferences settings = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        predictValue = settings.getInt(getString(R.string.SHAREDPREFERENCE_PREDICT_VALUE),82);

        camera = CameraSet.initializeCamera(this, (FrameLayout) findViewById(R.id.check_preview));

        new Thread() {
            @Override
            public void run() {
                Loader.load(opencv_nonfree.class);
            }
        }.start();

        r = new Runnable() {
            @Override
            public void run() {
                camera.takePicture(null, null, picture);
            }
        };

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCode = ACTION_SKIP;
                onStop();
            }
        });
    }

    private PictureCallback picture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new DelayedPhoto(data).execute();
        }
    };

    @Override
    protected void onRestart() {
        if(deviceAccessControl) actionCode = ACTION_LOCK;
        super.onRestart();
    }

    @Override
    protected void onStart() {
        delay.postDelayed(r, 3000);
        camera.startPreview();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        onStop();
    }

    @Override
    protected void onStop() {
        Log.d("OnStop", actionCode + "");
        //camera.stopPreview();
        delay.removeCallbacks(r);
        switch(actionCode){
            case ACTION_LOCK:
                AccessControl.lock(CheckPersonActivity.this);
                break;
            case ACTION_FINISH:
                AccessControl.allow(CheckPersonActivity.this, personName, packageName);
                break;
            case ACTION_SKIP:
                Intent i = new Intent(CheckPersonActivity.this, PasswordRequestActivity.class);
                i.putExtra(getString(R.string.EXTRA_PACKAGE_NAME), packageName);
                startActivity(i);
                break;
            case ACTION_CHECK:
                AccessControl.personCheck(CheckPersonActivity.this, personName, packageName);
                break;
            default:
                // ki kell hagyni a finish()-t, mivel ilyen állapotban a SCREEN_ON után fut le, ahol a create() után lefut az onStop() is.
                super.onStop();
                return;
        }
        finish();
        super.onStop();
    }

    private class DelayedPhoto extends AsyncTask<Object, Object, Void> {

        byte[] data;

        public DelayedPhoto(byte[] data) {
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            if (!FaceDetection.numberOfFaces(data, CheckPersonActivity.this)) {
                cancel(true);
                onPostExecute(null);
            }
        }

        @Override
        protected Void doInBackground(Object... params) {
            if (!isCancelled()) {
                Bitmap bitmap = FaceDetection.cropFace(data);
                pResult = FaceDetection.predict(CheckPersonActivity.this, bitmap, predictValue);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.d("CheckPersonActivity", "Predict result: "+pResult);
            //Log.d("CheckPersonActivity", "Package name: "+packageName);
            //Log.d("CheckPersonActivity", "Access control enabled: "+deviceAccessControl);
            if (learnedPersons.contains(Integer.toString(pResult))) {
                personName = learnedPersons.getString(Integer.toString(pResult), "");

                if (personName.startsWith("CHILD-")) {

                    personName = personName.substring(6, 7).toUpperCase() + personName.substring(7);
                    //Log.d(""CheckPersonActivity"", "Child name: "+personName);
                    if (deviceAccessControl) {
                        if (AccessControl.accessControl(personName, CheckPersonActivity.this)) {
                            actionCode = ACTION_FINISH;
                        } else {
                            actionCode = ACTION_LOCK;
                        }
                    } else {
                        actionCode = ACTION_CHECK;
                    }
                } else {
                    actionCode = ACTION_FINISH;
                }
                onStop();
            } else {
                fails += 1;
                if (fails < 3) {
                    Toast.makeText(CheckPersonActivity.this,
                            R.string.recognitionFailMessage, Toast.LENGTH_SHORT).show();
                    camera.startPreview();
                    delay.postDelayed(r, 3000);
                } else {
                    actionCode = ACTION_SKIP;
                    Toast.makeText(CheckPersonActivity.this, R.string.passwordRequest, Toast.LENGTH_SHORT).show();
                    onStop();
                }
            }
        }
    }


}

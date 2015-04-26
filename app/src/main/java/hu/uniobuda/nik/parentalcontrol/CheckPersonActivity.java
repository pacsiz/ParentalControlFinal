package hu.uniobuda.nik.parentalcontrol;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CheckPersonActivity extends Activity {

    private static final int ACTION_FINISH = 1;
    private static final int ACTION_SKIP = 2;
    private static final int ACTION_HOME = 3;
    private static final int ACTION_LOCK = 4;
    private Camera camera = null;
    private CameraView cameraPreview;

    int pResult = -2;
    int fails = 0;
    String packageName;
    private SharedPreferences learnedPersons;
    boolean accessControl;
    int actionCode = 0;
    int predictValue;
    Handler delay = new Handler();
    Runnable r;

    Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_person);

        skip = (Button)findViewById(R.id.btnSkip);
        packageName = getIntent().getStringExtra(getString(R.string.EXTRA_PACKAGE_NAME));
        if (packageName != null) {
            actionCode = ACTION_HOME;
        }
        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
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

        delay.postDelayed(r, 3000);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCode = ACTION_SKIP;
                delay.removeCallbacks(r);
                Intent i = new Intent(CheckPersonActivity.this, PasswordRequestActivity.class);
                i.putExtra(getString(R.string.EXTRA_PACKAGE_NAME), packageName);
                startActivity(i);
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
    public void onBackPressed() {
        if (actionCode != 0)
        {
            AccessControl.block(CheckPersonActivity.this, null);
        }
    }

    @Override
    protected void onPause() {
        camera.release();
        camera = null;
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (actionCode == 0) {
            AccessControl.lock(CheckPersonActivity.this);
        }
        delay.removeCallbacks(r);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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
            //Log.d("CheckPersonActivity", "Access control enabled: "+accessControl);
            if (learnedPersons.contains(Integer.toString(pResult))) {
                String personName = learnedPersons.getString(Integer.toString(pResult), "");

                if (personName.startsWith("CHILD-")) {

                    personName = personName.substring(6, 7).toUpperCase() + personName.substring(7);
                    //Log.d(""CheckPersonActivity"", "Child name: "+personName);
                    if (accessControl) {
                        if (AccessControl.accessControl(personName, CheckPersonActivity.this)) {
                            actionCode = ACTION_FINISH;
                            AccessControl.allow(CheckPersonActivity.this, personName, null);
                            //finish();
                        } else {
                            actionCode = ACTION_LOCK;
                            AccessControl.lock(CheckPersonActivity.this);
                            //finish();
                        }
                        //finish();
                    } else {
                        actionCode = ACTION_HOME;

                        //AccessControl.deny(CheckPersonActivity.this, personName, packageName);
                        AccessControl.personCheck(CheckPersonActivity.this, personName, packageName);
                    }

                    finish();

                } else {
                    actionCode = ACTION_FINISH;

                    AccessControl.allow(CheckPersonActivity.this, personName, packageName);
                    finish();
                }
            } else {
                fails += 1;
                if (fails < 3) {
                    Toast.makeText(CheckPersonActivity.this,
                            R.string.recognitionFailMessage, Toast.LENGTH_SHORT).show();
                    camera.startPreview();
                    delay.postDelayed(r, 3000);
                } else {
                    actionCode = ACTION_FINISH;

                    Toast.makeText(CheckPersonActivity.this,
                            R.string.passwordRequest, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CheckPersonActivity.this, PasswordRequestActivity.class);
                    i.putExtra(getString(R.string.EXTRA_PACKAGE_NAME), packageName);
                    startActivity(i);
                }
            }
        }
    }


}

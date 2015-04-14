package hu.uniobuda.nik.parentalcontrol;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.FrameLayout;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.Arrays;

public class CheckPersonActivity extends Activity {

    private Camera camera = null;
    private static Context context;
    private CameraView cameraPreview;
    CameraInfo info = new CameraInfo();
    int pResult = -2;
    int fails = 0;
    String packageName;
    private SharedPreferences learnedPersons;
    boolean accessControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_person);

        packageName = getIntent().getStringExtra(getString(R.string.EXTRA_PACKAGE_NAME));
        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
        learnedPersons = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        int frontCameraIndex = CameraSet.getFrontCameraIndex();
        FrameLayout preview = (FrameLayout) findViewById(R.id.check_preview);
        camera = Camera.open(frontCameraIndex);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(CameraSet.setCameraRotation(this
                        .getWindowManager().getDefaultDisplay().getRotation(),
                frontCameraIndex));
        camera.setParameters(params);
        cameraPreview = new CameraView(this, camera);
        preview.addView(cameraPreview);

        new Thread() {
            @Override
            public void run() {
                Loader.load(opencv_nonfree.class);
            }
        }.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                camera.takePicture(null, null, mPicture);
            }
        }, 5000);

        // new DelayedPhoto().execute();
    }

    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("onClick", "indítsd a feldolgozást");
            new DelayedPhoto(data).execute();
        }
    };




    @Override
    public void onBackPressed() {
        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
                pResult = FaceDetection.predict(bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("result", Integer.toString(pResult));
            // Log.d("packageName", packageName);
            Log.d("accessControlEnabled", accessControl + "");
            if (learnedPersons.contains(Integer.toString(pResult))) {
                String personName = learnedPersons.getString(Integer.toString(pResult), "");

                if (personName.startsWith("CHILD-")) {

                    personName = personName.substring(6, 7).toUpperCase() + personName.substring(7);
                    Log.d("ChildName", personName);
                    if (accessControl) {
                        if (AccessControl.accessControl(personName, CheckPersonActivity.this)) {
                            AccessControl.allow(CheckPersonActivity.this,personName,null);
                            finish();
                        } else {
                            AccessControl.lock(CheckPersonActivity.this);
                        }
                    } else {
                        AccessControl.deny(CheckPersonActivity.this,personName,packageName);
                    }

                } else {
                    AccessControl.allow(CheckPersonActivity.this,personName,packageName);
                    finish();
                }
            } else {
                fails += 1;
                if (fails <= 3) {
                    Toast.makeText(CheckPersonActivity.this,
                            R.string.recognitionFailMessage, Toast.LENGTH_LONG).show();
                    camera.startPreview();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            camera.takePicture(null, null, mPicture);
                        }
                    }, 3000);
                } else {
                    Toast.makeText(CheckPersonActivity.this,
                            R.string.passwordRequest, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CheckPersonActivity.this, PasswordRequestActivity.class);
                    i.putExtra(getString(R.string.EXTRA_PACKAGE_NAME), packageName);
                    startActivity(i);
                }
            }
        }
    }


}

package hu.uniobuda.nik.parentalcontrol;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class SetNewPersonActivity extends Activity {

    public static final int NUMBER_OF_PHOTOS = 5;
    private Button btnSetNewPerson;
    private EditText personName;
    private Button btnCapture;
    private CheckBox isParent;
    private Camera camera = null;
    private CameraManager cameraNewApi = null;
    private CameraView cameraPreview;
    private int frontCameraIndex;
    private String person;
    private int numberOfPhotos;
    private SharedPreferences learnedPersons;

    int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread() {
            @Override
            public void run() {
                Loader.load(opencv_nonfree.class);
            }
        }.start();

        numberOfPhotos = 0;
        learnedPersons = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_set_new_person);

        SharedPreferences shSettings = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);

        btnSetNewPerson = (Button) findViewById(R.id.btnSetNewPerson);
        personName = (EditText) findViewById(R.id.personName);
        isParent = (CheckBox) findViewById(R.id.isParent);
        hideKeyboard(findViewById(R.id.layout));

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        Map<String, ?> map = learnedPersons.getAll();
        final Map sortedMap = new TreeMap(new ValueComparatorDec(map));
        sortedMap.putAll(map);


        if (sortedMap.size() > 0) {
            Iterator it = sortedMap.entrySet().iterator();
            Map.Entry entry = (Map.Entry) it.next();
            personId = Integer.parseInt((String) entry.getKey());
        }

        if (!shSettings.getBoolean(getString
                (R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false)) {
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(R.string.cameraFailMessage);
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            dialog.show();
        }

        if (!shSettings.contains(getString
                (R.string.SHAREDPREFERENCE_PASSWORD))) {
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(R.string.noPasswordSet);
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            dialog.show();
        }

        btnSetNewPerson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                person = personName.getText().toString().toLowerCase();

                Log.d("név", person);
                if (person.isEmpty()) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.nameFailMessage);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            });
                    dialog.show();
                } else if (sortedMap.containsValue(person)) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.nameExistMessage);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            });
                    dialog.show();
                } else {

                    if (!isParent.isChecked()) {
                        person = "CHILD-" + person;
                    }
                    personId++;
                    setCameraView();
                }
            }
        });

    }

    public void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }

            });
        }
    }

    private void setCameraView() {
        setContentView(R.layout.activity_create_photo);
        camera = CameraSet.initializeCamera(this,(FrameLayout)findViewById(R.id.camera_preview));

        //frontCameraIndex = CameraSet.getFrontCameraIndex();
        //camera = Camera.open(frontCameraIndex);
        /*Camera.Parameters params = camera.getParameters();
        params.setRotation(CameraSet.setCameraRotation(this.getWindowManager().getDefaultDisplay().getRotation(),
                frontCameraIndex));
        camera.setParameters(params);
        cameraPreview = new CameraView(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);*/
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btnCapture.setEnabled(false);
                if (numberOfPhotos < 6) {
                    Log.d("onClick", "csináljfotót");
                    camera.takePicture(null, null, picture);
                } else {
                    finish();
                }
            }
        });
    }

    private PictureCallback picture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("onClick", "indítsd a feldolgozást");

            new createFacePhoto(data).execute();
        }
    };

    private class createFacePhoto extends AsyncTask<Object, Object, Void> {
        private byte[] data;
        int predict;
        ProgressDialog pd = new ProgressDialog(SetNewPersonActivity.this);

        public createFacePhoto(byte[] data) {
            this.data = data;
        }

        @Override
        protected void onPreExecute() {

            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.working));
            pd.show();
            if (!FaceDetection.numberOfFaces(data, SetNewPersonActivity.this)) {
                cancel(true);
                pd.dismiss();
                camera.startPreview();
                btnCapture.setEnabled(true);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            btnCapture.setEnabled(true);

            if (predict > -1) {
                Toast.makeText(SetNewPersonActivity.this, R.string.facePredicted, Toast.LENGTH_SHORT).show();
                camera.startPreview();
            } else {
                numberOfPhotos++;
                if (numberOfPhotos == NUMBER_OF_PHOTOS) {
                    camera.stopPreview();
                    camera.release();
                    new Trainer().execute();
                } else {
                    camera.startPreview();
                }
            }
        }

        @Override
        protected Void doInBackground(Object... params) {
            if (!isCancelled()) {
                Bitmap bitmap = FaceDetection.cropFace(data);
                predict = FaceDetection.predict(SetNewPersonActivity.this, bitmap);
                Log.d("predict",predict+"");
                if (predict == -1)
                {
                    //matVector.put(FaceDetection.matForLBPH(bitmap));
                    // blist.add(FaceDetection.cropFace(data));
                    FaceDetection.saveCroppedFace(SetNewPersonActivity.this, bitmap, person, personId);
                }
            }
            return null;
        }
    }

    private class Trainer extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = new ProgressDialog(SetNewPersonActivity.this);

        @Override
        protected void onPreExecute() {
            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.learning));
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            savePerson();
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {

            FaceDetection.learnJPG(personId, SetNewPersonActivity.this);
            //FaceDetection.learn(blist, personId);
            return null;
        }

        private void savePerson() {
            // TODO Auto-generated method stub
            Editor e = learnedPersons.edit();
            e.putString(Integer.toString(personId), person);
            e.apply();
        }

    }

    @Override
    protected void onPause() {
        onDestroy();
        super.onPause();
    }



    @Override
    protected void onDestroy() {
        FaceDetection.deleteJPGs(SetNewPersonActivity.this);
        super.onDestroy();
    }
}

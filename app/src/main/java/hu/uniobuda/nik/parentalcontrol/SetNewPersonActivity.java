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
    private CameraView cameraPreview;
    private String person;
    private int numberOfPhotos;
    private SharedPreferences learnedPersons;
    Map sortedMap;
    int predictValue;

    int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_person);

        numberOfPhotos = 0;
        SharedPreferences shSettings = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        predictValue = shSettings.getInt(getString(R.string.SHAREDPREFERENCE_PREDICT_VALUE),82);


        btnSetNewPerson = (Button) findViewById(R.id.btnSetNewPerson);
        personName = (EditText) findViewById(R.id.personName);
        isParent = (CheckBox) findViewById(R.id.isParent);
        hideKeyboard(findViewById(R.id.layout));

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);


        if (!shSettings.getBoolean(getString
                (R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false)) {
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(R.string.cameraFailMessage);
            dialog.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            dialog.show();
        } else if (!shSettings.contains(getString
                (R.string.SHAREDPREFERENCE_PASSWORD))) {
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(R.string.noPasswordSet);
            dialog.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            dialog.show();
        } else {
            new Thread() {
                @Override
                public void run() {
                    Loader.load(opencv_nonfree.class);
                }
            }.start();
            learnedPersons = getSharedPreferences(getString
                    (R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
            Map<String, ?> map = learnedPersons.getAll();
            sortedMap = new TreeMap(new ValueComparatorDec(map));
            sortedMap.putAll(map);
            if (sortedMap.size() > 0) {
                Iterator it = sortedMap.entrySet().iterator();
                Map.Entry entry = (Map.Entry) it.next();
                personId = Integer.parseInt((String) entry.getKey());
            }
        }

        btnSetNewPerson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                person = personName.getText().toString().toLowerCase();
                //Log.d("SetNewPersonActivity", "Person name: "+person);
                if (person.isEmpty()) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.nameFailMessage);
                    dialog.setPositiveButton(R.string.OK,
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
                    dialog.setPositiveButton(R.string.OK,
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

    private void setCameraView() {
        setContentView(R.layout.activity_create_photo);
        camera = CameraSet.initializeCamera(this, (FrameLayout) findViewById(R.id.camera_preview));
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btnCapture.setEnabled(false);
                if (numberOfPhotos < 6) {
                    //Log.d("SetNewPersonActivity", "Create next photo");
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
                predict = FaceDetection.predict(SetNewPersonActivity.this, bitmap, predictValue);
                //Log.d("SetNewPersonActivity", "Predict result: "+predict);
                if (predict == -1) {
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
            btnCapture.setEnabled(false);
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
            return null;
        }

        private void savePerson() {
            Editor e = learnedPersons.edit();
            e.putString(Integer.toString(personId), person);
            e.commit();
        }
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


    @Override
    protected void onDestroy() {
        FaceDetection.deleteJPGs(SetNewPersonActivity.this);
        super.onDestroy();
    }
}

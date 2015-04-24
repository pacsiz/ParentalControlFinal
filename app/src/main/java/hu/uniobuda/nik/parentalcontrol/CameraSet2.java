package hu.uniobuda.nik.parentalcontrol;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import java.util.concurrent.Semaphore;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraSet2 {

    CameraDevice cameraD;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            cameraD = cameraDevice;
            mCameraOpenCloseLock.release();
            //cameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            cameraD = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            cameraD = null;
            /*Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }*/
        }
    };


    public static String getFrontCameraId(Context context) throws CameraAccessException {
        String[] camIds;
        String frontCameraid = context.getString(R.string.noFrontCamera);
        CameraManager cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        camIds = cameraManager.getCameraIdList();
        for (String camId : camIds)
        {
            CameraCharacteristics ch = cameraManager.getCameraCharacteristics(camId);
            if(ch.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
            {
                frontCameraid = camId;
                break;
            }
        }
        return frontCameraid;
    }

    public static void openCamera(Context context, String cameraId)
    {
        CameraManager cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);

        //CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {

       // };

    }



}

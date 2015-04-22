package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;

public class CameraSet {


    public static Camera initializeCamera(Activity activity, FrameLayout preview)
    {

        int fronCameraIndex = getFrontCameraIndex();
        Camera camera = Camera.open(fronCameraIndex);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(CameraSet.setCameraRotation(activity.getWindowManager().getDefaultDisplay().getRotation(),
                fronCameraIndex));
        camera.setParameters(params);
        CameraView cameraPreview = new CameraView(activity, camera);
        preview.addView(cameraPreview);
        return camera;
    }

    public static int getFrontCameraIndex() {
        int cameraNumber = Camera.getNumberOfCameras();
        int frontCameraIndex = -1;
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraNumber; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraIndex = i;
                // Log.d("FCI", Integer.toString(frontCameraIndex));
                break;
            }
        }
        return frontCameraIndex;
    }

    public static int setCameraRotation(int windowRotation, int cameraId) {
        int degrees = 0;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        Log.d("window rotation", Integer.toString(windowRotation));

        switch (windowRotation) {

            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Log.d("camera rotation", Integer.toString(info.orientation));
        int rotate = (info.orientation - degrees + 360) % 360;
        Log.d("return", Integer.toString(rotate));
        return rotate;
    }
}

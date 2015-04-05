package hu.uniobuda.nik.parentalcontrol;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_contrib;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Environment;
import android.util.Log;

public class FaceDetection {

    private static final int NUMBER_OF_PHOTOS = 5;
    private final String XML_PATH = Environment.getExternalStorageDirectory().toString() + "/teszt.xml";
    private Context context;
    private Mat face;
    private int result;

    public static Mat toRecognition(byte[] data)
    {
        int numberOfFaces = 0;
        Bitmap bit = getBitmapFromBytes(data);
        Bitmap out = bit.copy(Config.RGB_565, true);
        FaceDetector fD = new FaceDetector(out.getWidth(), out.getHeight(), 2);
        Face[] faces = new Face[2];
        numberOfFaces = fD.findFaces(out, faces);
        //Log.d("doinback", Integer.toString(numberOfFaces));


        if (numberOfFaces == 0) {
            return null;
        } else {
            Log.d("doinback", "else");
            Bitmap b = cropFace(out, faces);
            Log.d("b méret", Integer.toString(b.getByteCount()));
            Bitmap out2 = b.copy(Config.ARGB_8888, true);
            Log.d("out2 méret", Integer.toString(out2.getByteCount()));


            IplImage face = IplImage.create(b.getWidth(),
                    b.getHeight(), IPL_DEPTH_8U, 4);
            IplImage gray = IplImage.create(b.getWidth(), b.getHeight(), IPL_DEPTH_8U, 1);
            out2.copyPixelsToBuffer(face.createBuffer());
            cvCvtColor(face, gray, CV_BGR2GRAY);

            Mat img = new Mat(gray);

            Log.d("Matrix", Boolean.toString(img.isNull()));
            return img;
        }
    }

    public IplImage matForLBPH (Bitmap bitmap, Context context)
    {

        int numberOfFaces = numberOfFaces(bitmap);
        if (numberOfFaces == 1)
        {

            Bitmap temp = cropFace(bitmap, )
        }
    }

    public int predict (byte[] rawData, Context context)
    {
        FaceRecognizer fr = createLBPHFaceRecognizer();
        fr.load(XML_PATH);
        fr.set("threshold", 90);
        Bitmap bitmap = getBitmapFromBytes(rawData);
        Mat mat = matForLBPH(bitmap, context);
        return fr.predict(mat);
    }

    public int numberOfFaces(Bitmap bitmap)
    {
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        Face[] faces = new Face[2];
        return fd.findFaces(bitmap,faces);
    }

    public static boolean saveCroppedFace(byte[] data, String person, int personId) {
        int numberOfFaces = 0;
        Bitmap bit = getBitmapFromBytes(data);
        // Bitmap out = bit.copy(Config.ARGB_8888, true);
        Bitmap out = bit.copy(Config.RGB_565, true);
        FaceDetector fD = new FaceDetector(out.getWidth(), out.getHeight(), 2);
        Face[] faces = new Face[2];
        numberOfFaces = fD.findFaces(out, faces);
        //Log.d("doinback", Integer.toString(numberOfFaces));

        if (numberOfFaces == 0) {
            return false;
        } else {
            Log.d("doinback", "else");
            Bitmap b = cropFace(out, faces);

            try {
                FileOutputStream fos = new FileOutputStream(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/"
                        + personId
                        + "-"
                        + person
                        + "-"
                        + System.currentTimeMillis() + ".jpg");

                b.compress(CompressFormat.JPEG, 90, fos);

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private static Bitmap cropFace(Bitmap bitmap, Face[] faces)
    {
        int faceSize = (int) (faces[0].eyesDistance() * 2);
        PointF centerFace = new PointF();
        faces[0].getMidPoint(centerFace);

        int initX = bitmap.getWidth();
        int initY = bitmap.getHeight();
        int endX = 0;
        int endY = 0;

        int tInitX = (int) (centerFace.x - faceSize / 2);
        int tInitY = (int) (centerFace.y - faceSize / 2);
        tInitX = Math.max(0, tInitX);
        tInitY = Math.max(0, tInitY);

        int tEndX = tInitX + faceSize;
        int tEndY = (int) (tInitY + faceSize * 1.5);
        tEndX = Math.min(tEndX, bitmap.getWidth());
        tEndY = Math.min(tEndY, bitmap.getHeight());

        initX = Math.min(initX, tInitX);
        initY = Math.min(initY, tInitY);
        endX = Math.max(endX, tEndX);
        endY = Math.max(endY, tEndY);

        int sizeX = endX - initX;
        int sizeY = endY - initY;

        if (sizeX + initX > bitmap.getWidth()) {
            sizeX = bitmap.getWidth() - initX;
        }
        if (sizeY + initY > bitmap.getHeight()) {
            sizeY = bitmap.getHeight() - initY;
        }

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, initX, initY,
                sizeX, sizeY);
        Bitmap b = Bitmap
                .createScaledBitmap(croppedBitmap, 100, 150, false);
        return b;
    }

    private static Bitmap getBitmapFromBytes(byte[] imageContent) {
        try {
            return BitmapFactory.decodeByteArray(imageContent, 0,
                    imageContent.length);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

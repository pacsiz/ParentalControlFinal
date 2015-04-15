package hu.uniobuda.nik.parentalcontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
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
import android.widget.Toast;

public class FaceDetection {

    private static final int NUMBER_OF_PHOTOS = 5;
    private static final String XML_PATH = Environment.getExternalStorageDirectory().toString() + "/teszt.xml";

    public static void learnJPG(int personId)
    {
        File root = Environment.getExternalStorageDirectory();

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        MatVector images = new MatVector(imageFiles.length);

        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);

        IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }
        Log.d("KÉPEK", Long.toString(images.size()));
        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
        File f = new File(Environment
                .getExternalStorageDirectory().toString()+"/teszt.xml");
        if(f.exists())
        {
            faceRecognizer.load(XML_PATH);
            faceRecognizer.update(images, labels);
        }
        else
        {
            faceRecognizer.train(images, labels);
        }

        faceRecognizer.save(Environment
                .getExternalStorageDirectory().toString()+"/teszt.xml");

        for (File image : imageFiles)
        {
            image.delete();
        }
    }


    public static void learn(ArrayList<Bitmap> faces, int label)
    {

        //Log.d("tömbméret", Integer.toString(faces.size()));
        Mat labels = new Mat(NUMBER_OF_PHOTOS, 1, CV_32SC1);
        MatVector matVector = new MatVector(NUMBER_OF_PHOTOS);
        for(Bitmap face : faces)
        {
            matVector.put(matForLBPH(face));
        }
        faces.clear();
        IntBuffer intBuff = labels.getIntBuffer();
        //IntBuffer intBuff = IntBuffer.allocate(NUMBER_OF_PHOTOS);
        int index = 0;
        FaceRecognizer fr = createLBPHFaceRecognizer();
        for (int i = 0; i < NUMBER_OF_PHOTOS; i++)
        {
            intBuff.put(i, label);
        }

        //labels.create(NUMBER_OF_PHOTOS,intBuff,CV_32SC1);

        File file = new File(XML_PATH);
        if(file.exists())
        {
            fr.load(XML_PATH);
            fr.update(matVector,labels);
        }
        else
        {
            fr.train(matVector,labels);
            fr.save(XML_PATH);
        }
    }

    public static Mat matForLBPH (Bitmap face)
    {
        //Bitmap bitmap = getBitmapFromBytes(rawData);
        //FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        //Face[] faceArray = new Face[2];
        //fd.findFaces(bitmap, faceArray);
        //Bitmap temp = cropFace(bitmap, faceArray);
        Log.d("temp méret", Integer.toString(face.getByteCount()));
        Bitmap out = face.copy(Config.ARGB_8888, true);
        face.recycle();
        face = null;
        int height = out.getHeight();
        int width = out.getWidth();
        Log.d("out méret", Integer.toString(out.getByteCount()));
        IplImage iplTemp = IplImage.create(width, height, IPL_DEPTH_8U, 4);
        IplImage grayImg = IplImage.create(width,height,IPL_DEPTH_8U,1);
        out.copyPixelsToBuffer(iplTemp.createBuffer());
        out.recycle();
        out = null;
        cvCvtColor(iplTemp,grayImg,CV_BGR2GRAY);
        Mat mat = new Mat(grayImg);
        Log.d("matméret", Boolean.toString(mat.isNull()));
        return mat;
    }

    public static int predict (Bitmap bitmap)
    {
        FaceRecognizer fr = createLBPHFaceRecognizer();
        fr.load(XML_PATH);
        fr.set("threshold", 95);
        Mat mat = matForLBPH(bitmap);
       return fr.predict(mat);

    }

    public static boolean numberOfFaces(byte[] rawData, Context context)
    {
        Bitmap bitmap = getBitmapFromBytes(rawData);
        //Bitmap out = bitmap.copy(Config.RGB_565, true);
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        Face[] faces = new Face[2];
        int numOfFaces = fd.findFaces(bitmap,faces);
        bitmap.recycle();
        bitmap = null;
        if (numOfFaces == 1)
        {
            return true;
        }
        else if (numOfFaces == 0)
        {
            Toast.makeText(context, R.string.faceNotFound, Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            Toast.makeText(context, R.string.moreFaces, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean saveCroppedFace(Bitmap croppedFace, String person, int personId) {
        //int numberOfFaces = 0;
        //Bitmap bit = getBitmapFromBytes(data);
        // Bitmap out = bit.copy(Config.ARGB_8888, true);
        //Bitmap out = bit.copy(Config.RGB_565, true);
        //FaceDetector fD = new FaceDetector(out.getWidth(), out.getHeight(), 2);
        //Face[] faces = new Face[2];
        //numberOfFaces = fD.findFaces(out, faces);
        //Log.d("doinback", Integer.toString(numberOfFaces));

       // if (numberOfFaces == 0) {
         //   return false;
        //} else {
          //  Log.d("doinback", "else");
            //Bitmap b = cropFace(out, faces);

            try {
                FileOutputStream fos = new FileOutputStream(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/"
                        + personId
                        + "-"
                        + person
                        + "-"
                        + System.currentTimeMillis() + ".jpg");

                croppedFace.compress(CompressFormat.JPEG, 90, fos);

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        //}
    }

    public static Bitmap cropFace(byte[] data)
    {
        Bitmap bitmap = getBitmapFromBytes(data);
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        Face[] faces = new Face[2];
        fd.findFaces(bitmap,faces);
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
        bitmap.recycle();
        bitmap = null;
        Bitmap b = Bitmap
                .createScaledBitmap(croppedBitmap, 100, 150, false);
        return b;
    }

    public static Bitmap getBitmapFromBytes(byte[] imageContent) {
        try {
            Bitmap bitmap =  BitmapFactory.decodeByteArray(imageContent, 0,
                    imageContent.length);

            return (bitmap.copy(Config.RGB_565,true));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

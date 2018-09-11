package com.kuldeep.facedetection;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;



/**
 * This class echoes a string called from JavaScript.
 */
public class FaceDetectionCordovaPlugin extends CordovaPlugin {
    String base64="";
    private CallbackContext callback = null;
    Intent intent ;
    public  static final int RequestPermissionCode  = 1;

    //exceute and coolMethod(action)
    //excute 3 parameters action(which function to perform),args input to plugin
    // callback result context

    //java file(contains complete logic of the java program),
    // js(connection between js and java file through cordova or window object),
    // plugin.xml main file or the main file to read by cordova or ionic project install the project

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        callback = callbackContext;
        if(action.equals("ProcessAndDetect")) {
            String message = args.getString(0);
            this.ProcessAndDetect(message,context);
            return true;
        }
        return false;
    }

    private void ProcessAndDetect(String name,Context context) {
        this.cordova.setActivityResultCallback (this);
        intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        this.cordova.getActivity().startActivityForResult(intent, 7);
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7 && resultCode == -1) {
            Bitmap bmp_Copy = (Bitmap) data.getExtras().get("data");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            Bitmap bitmap = bmp_Copy.copy(Bitmap.Config.ARGB_8888,true);
            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);
            Canvas tempCanvas = new Canvas(bitmap);
            tempCanvas.drawBitmap(bitmap, 0, 0, null);
            FaceDetector faceDetector = new
                    FaceDetector.Builder(this.cordova.getActivity()).setTrackingEnabled(false)
                    .build();
            if(!faceDetector.isOperational()){
                new AlertDialog.Builder(this.cordova.getActivity()).setMessage("Could not set up the face detector!").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);


            for(int i=0; i<faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            this.echo(encoded, callback);
        }
    }

}

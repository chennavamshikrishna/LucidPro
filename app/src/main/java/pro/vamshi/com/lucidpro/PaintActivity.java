package pro.vamshi.com.lucidpro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaintActivity extends AppCompatActivity {
    private PaintView paintView;
    Button screenshots,undo;
    ImageView ss;
    File Screenshotfile;
    String currentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        screenshots=findViewById(R.id.screenshot);
        ss=findViewById(R.id.ss);
        undo=findViewById(R.id.clear);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        paintView.normal();
        screenshots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 takeScreenshot(view);
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
            }
        });


    }
    public void takeScreenshot(View view){
        // Get root View of your application
        View rootView = findViewById(R.id.paintView);
        // Enable drawing cache
        rootView.setDrawingCacheEnabled(true);

        // Create image
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        //Bitmap cropimg=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight()-155);
        rootView.setDrawingCacheEnabled(false);
        try {
            Screenshotfile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d("exception",ex.toString());

        }
        try{

            OutputStream stream = null;

            stream = new FileOutputStream(Screenshotfile);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();

            stream.close();

        }catch (IOException e)

        {
            Log.d("exception has",e.toString());

            e.printStackTrace();
        }
        Bitmap takenImage = BitmapFactory.decodeFile(Screenshotfile.getAbsolutePath());


        // Show image in ImageView
        ss.setImageBitmap(takenImage);
        Intent i=new Intent(PaintActivity.this,OCRActivity.class);
        i.putExtra("path",currentPhotoPath);
        startActivity(i);


    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("path",image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

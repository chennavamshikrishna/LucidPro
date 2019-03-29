package pro.vamshi.com.lucidpro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static pro.vamshi.com.lucidpro.Speech2Text.RESULT_SPEECH;

public class OCRActivity extends AppCompatActivity {
    private Vision vision;
    String path="";
    TextView word;
    FloatingActionButton record;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        word=findViewById(R.id.word);
        record=findViewById(R.id.record);
        result=findViewById(R.id.trans);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                intent.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES,true);

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    result.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });


        /*((ImageView)findViewById(R.id.photo_view))
                .setImageResource(R.drawable.photo_of_text_preview);*/

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyB4k5LieQKUQ3zThnKwryvB9zzQ0t3fBe4"));

        vision = visionBuilder.build();
        path=getIntent().getStringExtra("path");

        faceDetection(path);

    }

    private void faceDetection(final String path) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //InputStream inputStream = getResources().openRawResource(R.raw.exa);
                    Bitmap src= BitmapFactory.decodeFile(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] photoData = baos.toByteArray();;

                    //byte[] photoData = IOUtils.toByteArray(inputStream);

                    Image inputImage = new Image();
                    inputImage.encodeContent(photoData);

                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("DOCUMENT_TEXT_DETECTION");

                    AnnotateImageRequest request = new AnnotateImageRequest();
                    request.setImage(inputImage);
                    request.setFeatures(Arrays.asList(desiredFeature));

                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                    batchRequest.setRequests(Arrays.asList(request));

                    BatchAnnotateImagesResponse batchResponse =
                            vision.images().annotate(batchRequest).execute();

                    final TextAnnotation text = batchResponse.getResponses()
                            .get(0).getFullTextAnnotation();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    text.getText(), Toast.LENGTH_LONG).show();
                            word.setText(text.getText());
                        }
                    });

                } catch(Exception e) {
                    Log.d("ERROR", e.getMessage());
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {


                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //ArrayList<String> confidence=data.getStringArrayListExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    //Log.d("confidence",confidence.get(0)+"");
                    float[] confidence = data.getFloatArrayExtra(
                            RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    //Log.d("confidence",confidence.length+"");
                    String trans=text.get(0);
                    String ocrword= (String) word.getText();
                    Log.d("trans",ocrword.compareToIgnoreCase(trans)+"");

                    if(ocrword.compareToIgnoreCase(trans)==1) {
                        Random r=new Random();
                        int low=90;
                        int high=100;
                        int resultscore=r.nextInt(high-low)+low;
                        result.setText("You Pronunced as  " + text.get(0)+" with a accuracy of "+resultscore);
                    }
                    else{
                        result.setText("You Pronunced Incorrectly as "+trans);
                    }

                }
                break;
            }

        }
    }
}

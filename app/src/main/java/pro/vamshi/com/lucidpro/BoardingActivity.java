package pro.vamshi.com.lucidpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


public class BoardingActivity extends AppCompatActivity {
    public Button learn,check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);
        learn= (Button) findViewById(R.id.learn);
        check= (Button) findViewById(R.id.Check);
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        startActivity(new Intent(BoardingActivity.this,CameraActivity.class));



            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        startActivity(new Intent(BoardingActivity.this,PaintActivity.class));




            }
        });

    }

}

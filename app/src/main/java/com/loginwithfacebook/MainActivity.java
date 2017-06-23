package com.loginwithfacebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
   TextView txt_fb,twitter,google;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        idMapping();
        setOnClick();
    }
    private void idMapping() {
        txt_fb =(TextView)findViewById(R.id.txt_fb);
        twitter=(TextView)findViewById(R.id.twitter);
        google=(TextView)findViewById(R.id.google);
    }
    private void setOnClick()
    {

        txt_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  i = new Intent(MainActivity.this,FacebookActivity.class);
                startActivity(i);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  i = new Intent(MainActivity.this,GooglePlusActivity.class);
                startActivity(i);
            }
        });
    }


}

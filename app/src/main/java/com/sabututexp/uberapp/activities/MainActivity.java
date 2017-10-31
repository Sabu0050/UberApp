package com.sabututexp.uberapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sabututexp.uberapp.R;

public class MainActivity extends AppCompatActivity {

    private Button dButton;
    private Button cButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dButton = (Button) findViewById(R.id.driverButton);
        cButton = (Button) findViewById(R.id.customerButton);

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DriverRegistration.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CustomerRegistration.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}

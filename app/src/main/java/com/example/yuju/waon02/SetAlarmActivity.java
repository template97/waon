package com.example.yuju.waon02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAlarmActivity extends Activity {

    Button cancelBtn;
    Button  saveBtn;
    EditText nameText;
    TimePicker timePicker;
    Switch rainSwitch;
    Switch snowSwitch;
    Switch vibrateSwitch;
    ImageView rainImage, snowImage, logoImage;
    int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setalarm);

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        nameText = (EditText)findViewById(R.id.text_name);
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        rainSwitch = (Switch)findViewById(R.id.switch_rain);
        snowSwitch = (Switch)findViewById(R.id.switch_snow);
        vibrateSwitch = (Switch)findViewById(R.id.switch_vibrate);
        rainImage = (ImageView)findViewById(R.id.rainImage);
        snowImage = (ImageView)findViewById(R.id.snowImage);
        logoImage = (ImageView)findViewById(R.id.logoImage);
        rainImage.setImageResource(R.drawable.weather_rainy);
        snowImage.setImageResource(R.drawable.weather_snow);

        logoImage.setImageResource(R.drawable.waon_logo);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("valid", false);

                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "Alarm doesn't saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String label = nameText.getText().toString();
                intent.putExtra("label", label);
                intent.putExtra("hour", timePicker.getHour());
                intent.putExtra("min", timePicker.getMinute());
                intent.putExtra("rain", rainSwitch.isChecked());
                intent.putExtra("snow", snowSwitch.isChecked());
                intent.putExtra("vibrate", vibrateSwitch.isChecked());
                intent.putExtra("valid", true);
                intent.putExtra("index", index);

                setResult(RESULT_OK, intent);
                //Toast.makeText(getApplicationContext(), "Alarm does saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Intent data = getIntent();
        String label = data.getStringExtra("label");
        int hour = data.getIntExtra("hour", 0);
        int min = data.getIntExtra("min", 0);
        boolean r, s,vb;
        r = data.getBooleanExtra("rain", false);
        s = data.getBooleanExtra("snow", false);
        vb = data.getBooleanExtra("vibrate", false);
        index = data.getIntExtra("index", -1);

        if(index != -1) {
            nameText.setText(label);
            timePicker.setHour(hour);
            timePicker.setMinute(min);
        }
        rainSwitch.setChecked(r);
        snowSwitch.setChecked(s);
        vibrateSwitch.setChecked(vb);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("valid", false);

        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}

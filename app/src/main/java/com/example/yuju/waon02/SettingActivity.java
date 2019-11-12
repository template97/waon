package com.example.yuju.waon02;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SettingActivity extends Activity {

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    public static int NOT_USE = 5000;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;



    Spinner spinner, hourSpinner1, hourSpinner2, minSpinner1, minSpinner2;
    Switch GPSSwitch, rainSwitch, snowSwitch;
    ImageView logoImage, snowImage0, rainImage0;
    Button cancelBtn;
    Button  saveBtn;
    TextView signText1, signText2, infoText1, infoText2, infoText0;
    Intent dataIntent;

    int locationNum, snowHour, snowMin, rainHour, rainMin;
    String locations[]={"강원도","경기도","경상남도","경상북도","광주광역시","대구광역시","대전광역시","부산광역시","서울특별시"
            ,"세종특별자치시","울산광역시","인천광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도"};
    String[] hour1 = new String[13];
    String[] hour2 = new String[13];
    String[] min1 = new String[60];
    String[] min2 = new String[60];
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        locationNum = 0;
        logoImage = (ImageView)findViewById(R.id.logoImage2);
        logoImage.setImageResource(R.drawable.waon_logo);
        cancelBtn = (Button) findViewById(R.id.cancelBtn2);
        saveBtn = (Button) findViewById(R.id.saveBtn2);
        rainSwitch = (Switch)findViewById(R.id.rainSwitch);
        snowSwitch = (Switch)findViewById(R.id.snowSwitch);
        snowImage0 = (ImageView)findViewById(R.id.snowImage0);
        snowImage0.setImageResource(R.drawable.weather_snow);
        rainImage0 = (ImageView)findViewById(R.id.rainImage0);
        rainImage0.setImageResource(R.drawable.weather_rainy);
        spinner = (Spinner) findViewById(R.id.locationSpinner);
        signText1 = (TextView)findViewById(R.id.signText1);
        signText2 = (TextView)findViewById(R.id.signText2);
        GPSSwitch = (Switch)findViewById(R.id.switch_GPS);
        hourSpinner1 = (Spinner) findViewById(R.id.hourSpinner1);
        hourSpinner2 = (Spinner) findViewById(R.id.hourSpinner2);
        minSpinner1 = (Spinner) findViewById(R.id.minSpinner1);
        minSpinner2 = (Spinner) findViewById(R.id.minSpinner2);
        infoText0 = (TextView)findViewById(R.id.infoText0);
        infoText1 = (TextView)findViewById(R.id.infoText1);
        infoText2 = (TextView)findViewById(R.id.infoText2);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("valid", false);

                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "Setting doesn't saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("valid", true);
                if(GPSSwitch.isChecked()) {
                    intent.putExtra("location", -1);
                }
                else{
                    intent.putExtra("location", locationNum);
                }
                int r, s;
                if(rainSwitch.isChecked()) {
                    if (signText1.getText().toString() == "-") {
                        r = ((rainHour * 60) + rainMin) * (-1);
                    } else r = ((rainHour * 60) + rainMin);
                    intent.putExtra("rainDetail", r);
                }
                else{
                    intent.putExtra("rainDetail", NOT_USE);
                }
                if(snowSwitch.isChecked()) {
                    if (signText2.getText().toString() == "-") {
                        s = ((snowHour * 60) + snowMin) * (-1);
                    } else s = ((snowHour * 60) + snowMin);
                    intent.putExtra("snowDetail", s);
                }
                else {
                    intent.putExtra("snowDetail", NOT_USE);
                }
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "Setting Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {	//이부분은 스피너에 나타나는 내용
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	//선택시
                locationNum = position;
                setInfoText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {	//미선택시
                locationNum = 0;
                setInfoText();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        GPSSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinner.setEnabled(false);
                }
                else{
                    if (!isPermission) {
                        callPermission();
                    }
                    spinner.setEnabled(true);
                }
                setInfoText();
            }
        });

        hourSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                rainHour = position;
                setInfoText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rainHour = 0;
                setInfoText();
            }
        });
        hourSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                snowHour = position;
                setInfoText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snowHour = 0;
                setInfoText();
            }
        });
        minSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                rainMin = position;
                setInfoText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rainMin = 0;
                setInfoText();
            }
        });
        minSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                snowMin = position;
                setInfoText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snowMin = 0;
                setInfoText();
            }
        });
        for(int i=0; i<13; i++){
            hour1[i] = i + " hour";
            hour2[i] = i + " hour";
        }
        for(int i=0; i<60; i++){
            min1[i] = i + " min";
            min2[i] = i + " min";
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hour1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hour2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, min1);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, min2);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner1.setAdapter(adapter1);
        hourSpinner2.setAdapter(adapter2);
        minSpinner1.setAdapter(adapter3);
        minSpinner2.setAdapter(adapter4);


        signText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signText1.getText().toString() == "+")
                    signText1.setText("-");
                else
                    signText1.setText("+");
                setInfoText();
            }
        });

        signText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signText2.getText().toString() == "+")
                    signText2.setText("-");
                else
                    signText2.setText("+");
                setInfoText();
            }
        });
        rainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hourSpinner1.setEnabled(true);
                    minSpinner1.setEnabled(true);
                    signText1.setEnabled(true);
                }
                else{
                    hourSpinner1.setEnabled(false);
                    minSpinner1.setEnabled(false);
                    signText1.setEnabled(false);
                }
                setInfoText();
            }
        });
        snowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hourSpinner2.setEnabled(true);
                    minSpinner2.setEnabled(true);
                    signText2.setEnabled(true);
                }
                else{
                    hourSpinner2.setEnabled(false);
                    minSpinner2.setEnabled(false);
                    signText2.setEnabled(false);
                }
                setInfoText();
            }
        });

        dataIntent = getIntent();
        if ((locationNum = dataIntent.getIntExtra("location", -1)) == -1) {
            GPSSwitch.setChecked(true);
            spinner.setEnabled(false);
        } else {
            GPSSwitch.setChecked(false);
            spinner.setEnabled(true);
            spinner.setSelection(locationNum);
        }

        int rd = dataIntent.getIntExtra("rainDetail", 0);
        //Toast.makeText(getApplicationContext(), rd + "", Toast.LENGTH_LONG).show();
        int sd = dataIntent.getIntExtra("snowDetail", 0);
        if(rd != NOT_USE) {
            if (rd >= 0)
                signText1.setText("+");
            else {
                rd = rd * -1;
                signText1.setText("-");
            }
            hourSpinner1.setSelection((rd / 60));
            minSpinner1.setSelection((rd % 60));
            rainSwitch.setChecked(true);
        }
        else{
            signText1.setText("+");
            rainSwitch.setChecked(false);
            hourSpinner1.setEnabled(false);
            minSpinner1.setEnabled(false);
            signText1.setEnabled(false);
        }
        if(sd != NOT_USE) {
            if (sd >= 0)
                signText2.setText("+");
            else {
                sd *= -1;
                signText2.setText("-");
            }
            hourSpinner2.setSelection((sd / 60));
            minSpinner2.setSelection((sd % 60));
            snowSwitch.setChecked(true);
        }
        else{
            signText2.setText("+");
            snowSwitch.setChecked(false);
            hourSpinner2.setEnabled(false);
            minSpinner2.setEnabled(false);
            signText2.setEnabled(false);
        }
        setInfoText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    public void setInfoText(){
        String str0, str1, str2;
        if(GPSSwitch.isChecked()) {
            str0 = "Get weather according to GPS location";
        }
        else{
            str0 = "Get weather in " + locations[locationNum];
        }
        int r, s;
        if(rainSwitch.isChecked()) {
            str1 = "If it rains, the alarm will ring\n" + rainHour + " hour(s) " + rainMin + " minute(s) ";
            if (signText1.getText().toString() == "-")
                str1 = str1 + "early";
            else
                str1 = str1 + "later";
        }
        else{
            str1 = "If it rains, the alarm never ring";
        }
        if(snowSwitch.isChecked()) {
            str2 = "If it snows, the alarm will ring\n" + snowHour + " hour(s) " + snowMin + " minute(s) ";
            if (signText2.getText().toString() == "-")
                str2 = str2 + "early";
            else
                str2 = str2 + "later";
        }
        else {
            str2 = "If it snows, the alarm never ring";
        }
        infoText0.setText(str0);
        infoText1.setText(str1);
        infoText2.setText(str2);
    }
    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("valid", false);

        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), "Setting not Saved", Toast.LENGTH_SHORT).show();
        finish();
        super.onBackPressed();
    }
}

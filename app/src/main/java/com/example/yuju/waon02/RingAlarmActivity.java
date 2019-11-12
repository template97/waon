package com.example.yuju.waon02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;

public class RingAlarmActivity extends Activity {

    TextView currentTimeText;
    ImageView curWeather;
    MediaPlayer mPlayer, mPlayer2 = null ;
    Vibrator vibrator;
    Button offBtn;
    boolean vb;


    //For Loaction
    TextView text;		//날씨 뿌려주는 텍스트창
    String text0, sCategory;	//동네
    String sTm;			//발표시각
    String [] sHour, sDay, sTemp, sWdKor, sReh, sWfKor;
    int _location;

    int data = 0;
    boolean bCategory,bTm, bHour, bDay, bTemp, bWdKor, bReh, bWfKor, tCategory, tTm, tItem;
    String weather;
    boolean op = true;
    Handler handler;	//핸들러
    int id;
    TextView curWeatherText;
    LinearLayout back;

    String locationCode[]={"4200000000","4100000000", "4800000000","4700000000","2900000000","2700000000","3000000000","2600000000"
            ,"1100000000","3600000000","3100000000","2800000000","4600000000","4500000000","5000000000","4400000000","4300000000"};
    String locationList[]={"강원도","경기도","경상남도","경상북도","광주광역시","대구광역시","대전광역시","부산광역시","서울특별시"
            ,"세종특별자치시","울산광역시","인천광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringalarm);

        currentTimeText = (TextView) findViewById(R.id.currentTime);
        curWeather = (ImageView)findViewById(R.id.weatherImage00);
        text = (TextView)findViewById(R.id.textView10);
        curWeatherText = (TextView)findViewById(R.id.textView11);
        back = (LinearLayout)findViewById(R.id.back);
        ((ImageView)findViewById(R.id.log)).setImageResource(R.drawable.waon_logo);
        //weather
        bCategory = bTm = bHour = bTemp = bWdKor = bReh = bDay = bWfKor = tCategory = tTm = tItem = false;
        handler = new Handler();
        sHour = new String[40];
        sDay = new String[40];
        sTemp = new String[40];
        sWdKor = new String[40];
        sReh = new String[40];
        sWfKor = new String[40];

        Intent intent = getIntent();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean v = intent.getBooleanExtra("valid", true);
        int weather = intent.getIntExtra("weather", 0);
        String label = intent.getStringExtra("name");
        if (label == null)
            label = "none";
        int hour = intent.getIntExtra("hour", 0);
        int min = intent.getIntExtra("min", 0);
        _location = intent.getIntExtra("location", 0);
        vb = intent.getBooleanExtra("vibrate", false);
        int _rain = intent.getIntExtra("rain", 0);
        int _snow = intent.getIntExtra("snow", 0);
        id = intent.getIntExtra("id", -1);
        String W = refreshCurWeather();
        if(W == null)
            W = refreshCurWeather();
        if(W == null)
            curWeather.setImageResource(R.drawable.waon_logo);
        else {
            switch (W) {
                case "Cloudy": case "Little_Cloudy":
                    curWeather.setImageResource(R.drawable.weather_cloudy);
                    back.setBackgroundColor(0xFFA6EDE1);
                    break;
                case "Sunny":
                    curWeather.setImageResource(R.drawable.weather_sunny);
                    back.setBackgroundColor(0xFFFB9C97);
                    break;
                case "Rainy":
                    curWeather.setImageResource(R.drawable.weather_rainy);
                    back.setBackgroundColor(0xff7f97ff);
                    break;
                case "Snowy":
                    curWeather.setImageResource(R.drawable.weather_snow);
                    back.setBackgroundColor(0xFFEEFBF8);
                    break;
                case "Default":
                    curWeather.setImageResource(R.drawable.waon_logo);
                    back.setBackgroundColor(0xFFF6EBDC);
                    break;
            }
        }
        if (v) {

            //Toast.makeText(getApplicationContext(), W, Toast.LENGTH_SHORT).show();
            if (weather == 1){
                if (W.equals("Rainy")) {
                    String hourText, minText;
                    hour = (hour * 60 + min + _rain) / 60;
                    min = (hour * 60 + min + _rain) % 60;
                    if (hour <= 9)
                        hourText = "0" + hour;
                    else hourText = hour + "";
                    if (min <= 9)
                        minText = "0" + min;
                    else minText = min + "";
                    currentTimeText.setText(hourText + ":" + minText);
                    mPlayer = MediaPlayer.create(RingAlarmActivity.this, R.raw.default_music);
                    mPlayer.setVolume(1, 1);
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    mPlayer2 = MediaPlayer.create(RingAlarmActivity.this, R.raw.rain_sound);
                    mPlayer2.setVolume(1, 1);
                    mPlayer2.start();
                    if (vb) {
                        long[] pattern = {500, 1500};
                        vibrator.vibrate(pattern, 0);
                    }
                }
                else
                    finish();
            } else if (weather == 2 ){
                if( W.equals("Snowy")) {
                    String hourText, minText;
                    hour = (hour * 60 + min + _snow) / 60;
                    min = (hour * 60 + min + _snow) % 60;
                    if (hour <= 9)
                        hourText = "0" + hour;
                    else hourText = hour + "";
                    if (min <= 9)
                        minText = "0" + min;
                    else minText = min + "";
                    currentTimeText.setText(hourText + ":" + minText);
                    mPlayer = MediaPlayer.create(RingAlarmActivity.this, R.raw.default_music);
                    mPlayer.setVolume(1, 1);
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    mPlayer2 = MediaPlayer.create(RingAlarmActivity.this, R.raw.rain_sound);
                    mPlayer2.setVolume(1, 1);
                    mPlayer2.start();
                    if (vb) {
                        long[] pattern = {500, 1500};
                        vibrator.vibrate(pattern, 0);
                    }
                }
                else
                    finish();
            } else if (weather == 0){
                if( !(W.equals("Rainy") || W.equals("Snowy"))) {
                    String hourText, minText;
                    if (hour <= 9)
                        hourText = "0" + hour;
                    else hourText = hour + "";
                    if (min <= 9)
                        minText = "0" + min;
                    else minText = min + "";
                    currentTimeText.setText(hourText + ":" + minText);
                    mPlayer = MediaPlayer.create(RingAlarmActivity.this, R.raw.default_music);
                    mPlayer.setVolume(1, 1);
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    if (vb) {
                        long[] pattern = {500, 1500};
                        vibrator.vibrate(pattern, 0);
                    }
                }
                else finish();
            }

        }
        else {
            finish();
        }

        offBtn = (Button)findViewById(R.id.offBtn);
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vb)
                    vibrator.cancel();
                if(mPlayer != null)
                    mPlayer.stop();
                if(mPlayer2 != null)
                    mPlayer2.stop();
                finish();
            }
        });
    }

    public String refreshCurWeather(){
        text.setText("");	//일단 중복해서 누를경우 대비해서 내용 지워줌
        network_thread thread=new network_thread();		//스레드생성(UI 스레드사용시 system 뻗는다)
        thread.start();	//스레드 시작
        if(curWeatherText.getText().toString() == "No Info")
            Toast.makeText(getApplicationContext(), "Weather Update Failure", Toast.LENGTH_SHORT).show();
        text.setText(locationList[_location]);

        return curWeatherText.toString();
    }
    class network_thread extends Thread{

        public void run(){

            try{


                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp=factory.newPullParser();

                String weatherUrl="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+locationCode[_location];
                URL url=new URL(weatherUrl);
                InputStream is=url.openStream();
                xpp.setInput(is,"UTF-8");

                int eventType=xpp.getEventType();

                while(eventType!=XmlPullParser.END_DOCUMENT){

                    switch(eventType){
                        case XmlPullParser.START_TAG:

                            if(xpp.getName().equals("category")){
                                bCategory=true;

                            } if(xpp.getName().equals("tm")){
                            bTm=true;

                        } if(xpp.getName().equals("hour")){
                            bHour=true;

                        } if(xpp.getName().equals("day")){
                            bDay=true;

                        } if(xpp.getName().equals("temp")){
                            bTemp=true;

                        } if(xpp.getName().equals("wdKor")){
                            bWdKor=true;

                        } if(xpp.getName().equals("reh")){
                            bReh=true;

                        } if(xpp.getName().equals("wfKor")){
                            bWfKor=true;

                        }

                            break;

                        case XmlPullParser.TEXT:
                            if(bCategory){
                                sCategory=xpp.getText();
                                bCategory=false;
                            } if(bTm){
                            sTm=xpp.getText();
                            bTm=false;
                        }  if(bHour){
                            sHour[data]=xpp.getText();
                            bHour=false;
                        }  if(bDay){
                            sDay[data]=xpp.getText();
                            bDay=false;
                        }  if(bTemp){
                            sTemp[data]=xpp.getText();
                            bTemp=false;
                        }  if(bWdKor){
                            sWdKor[data]=xpp.getText();
                            bWdKor=false;
                        }  if(bReh){
                            sReh[data]=xpp.getText();
                            bReh=false;
                        } if(bWfKor){
                            switch (xpp.getText()){
                                case "구름 많음": sWfKor[data]= "Cloudy"; break;
                                case "구름 조금": sWfKor[data]= "Little_Cloudy"; break;
                                case "비": sWfKor[data]= "Rainy"; break;
                                case "맑음": sWfKor[data]= "Sunny"; break;
                                case "눈": sWfKor[data]= "Snowy"; break;
                                default: sWfKor[data]= "Default"; break;
                            }
                            bWfKor=false;
                        }
                            break;

                        case XmlPullParser.END_TAG:

                            if(xpp.getName().equals("item")){
                                tItem=true;
                                view_text();
                            } if(xpp.getName().equals("tm")){
                            tTm=true;
                            view_text();
                        } if(xpp.getName().equals("category")){
                            tCategory=true;
                            view_text();
                        } if(xpp.getName().equals("data")){
                            data++;
                        }
                            break;
                    }
                    eventType=xpp.next();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
            private void view_text(){
            handler.post(new Runnable() {

                @Override
                public void run() {

                    if(tCategory){
                        if(op) text.setText(text.getText()+"Location:"+sCategory+" ");
                        else text0 = text0 + "지역:"+sCategory+" ";
                        tCategory=false;
                    }if(tTm){
                        if(op) text.setText(text.getText()+"StateTime:"+sTm+"\n\n");
                        else text0 = text0 + "발표시각:"+sTm+"\n\n";
                        tTm=false;
                    }if(tItem){
                        weather = sWfKor[0];
                        for(int i=0;i<data;i++){
                            if(sDay[i]!=null){
                                if(Integer.parseInt(sDay[i])==0){
                                    if(op) text.setText(text.getText()+"Today ");
                                    else text0 = text0 +"날짜:"+"오늘"+" ";
                                }else if(Integer.parseInt(sDay[i])==1){
                                    if(op) text.setText(text.getText()+"Tomorrow ");
                                    else text0 = text0 +"날짜:"+"내일"+" ";
                                }else if(Integer.parseInt(sDay[i])==2){
                                    if(op) text.setText(text.getText()+"Day After Tomorrow ");
                                    else text0 = text0 +"날짜:"+"모래"+" ";
                                }
                            }
                            if(op) {
                                text.setText(text.getText() + sHour[i] + ":00\n");
                                text.setText(text.getText() + "Temperature " + sTemp[i] + "degree ");
                                text.setText(text.getText() + "Wind Direction " + sWdKor[i] + " ");
                                text.setText(text.getText() + "Humidity " + sReh[i] + "% ");
                                text.setText(text.getText() + "Weather #" + sWfKor[i] + "#\n\n");
                            }
                            else text0 = text0 + sHour[i]+"시\n" + "현재시간온도:"+sTemp[i]+"도"+" " + "풍향:"+sWdKor[i]+"풍"+" " + "습도:"+sReh[i]+"%"+" " + "날씨:"+sWfKor[i]+"\n\n";
                        }
                        tItem=false;
                        data=0;

                    }
                    String[] st = (text.getText().toString().split("#"));
                    if(st != null && st.length > 1) {
                        weather = st[1];
                    }
                    if(weather == null)
                        curWeather.setImageResource(R.drawable.waon_logo);
                    else {
                        switch (weather) {
                            case "Cloudy":
                                curWeather.setImageResource(R.drawable.weather_cloudy);
                                break;
                            case "Sunny":
                                curWeather.setImageResource(R.drawable.weather_sunny);
                                break;
                            case "Little_Cloudy":
                                curWeather.setImageResource(R.drawable.weather_suncloudy);
                                break;
                            case "Rainy":
                                curWeather.setImageResource(R.drawable.weather_rainy);
                                break;
                            case "Snowy":
                                curWeather.setImageResource(R.drawable.weather_snow);
                                break;
                            case "Default":
                                curWeather.setImageResource(R.drawable.waon_logo);
                                break;
                        }
                    }
                    text.setText(locationList[_location]);
                    if(weather != null)
                        curWeatherText.setText((weather.replace('_', '\n')));

                }

            });
        }
    }

    @Override
    public void onBackPressed() {
        if(vb)
            vibrator.cancel();
        mPlayer.stop();
        if(mPlayer2 != null)
            mPlayer2.stop();
        super.onBackPressed();
    }
}

package com.example.yuju.waon02;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class WeatherActivity extends Activity  {

    Spinner spinner;
    Button getBtn;
    String text0;
    TextView text;
    String sCategory, sTm;
    String [] sHour, sDay, sTemp, sWdKor, sReh, sWfKor;

    int data=0;
    boolean op = true;

    boolean bCategory, bTm, bHour, bDay, bTemp, bWdKor, bReh, bWfKor, tCategory, tTm, tItem;
    String weather;

    Handler handler;
    String code[]={"4200000000","4100000000", "4800000000","4700000000","2900000000","2700000000","3000000000","2600000000"
            ,"1100000000","3600000000","3100000000","2800000000","4600000000","4500000000","5000000000","4400000000","4300000000"};
    String list[]={"강원도","경기도","경상남도","경상북도","광주광역시","대구광역시","대전광역시","부산광역시","서울특별시"
            ,"세종특별자치시","울산광역시","인천광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도"};

    String loc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ((ImageView)findViewById(R.id.logoImage3)).setImageResource(R.drawable.waon_logo);
        handler=new Handler();

        bCategory=bTm=bHour=bTemp=bWdKor=bReh=bDay=bWfKor=tCategory=tTm=tItem=false;

        sHour=new String[40];
        sDay=new String[40];
        sTemp=new String[40];
        sWdKor=new String[40];
        sReh=new String[40];
        sWfKor=new String[40];

        spinner = (Spinner) findViewById(R.id.spinner0);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                loc=code[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loc=code[0];

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



        text=(TextView) findViewById(R.id.weatherText);
        getBtn=(Button) findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new OnClickListener() {	//버튼을 눌러보자

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                text.setText("");
                getWeather();
            }
        });
    }

    public String getWeather(){
        weather = null;
        network_thread thread=new network_thread();
        thread.start();
        return weather;
    }

    class network_thread extends Thread{
        public void run(){

            try{

                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp=factory.newPullParser();

                String weatherUrl="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+loc;
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
                                case "흐림": case "구름 많음": case "구름 조금": sWfKor[data]= "Cloudy"; break;
                                case "비": case "눈/비": sWfKor[data]= "Rainy"; break;
                                case "맑음": sWfKor[data]= "Sunny"; break;
                                default: sWfKor[data]= xpp.getText(); break;
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

            handler.post(new Runnable() {	//기본 핸들러니깐 handler.post하면됨

                @Override
                public void run() {

                    if(tCategory){
                        if(op) text.setText(text.getText());//+"Location:"+sCategory+" ");
                        else text0 = text0 + "지역:"+sCategory+" ";
                        tCategory=false;
                    }if(tTm){
                        if(op) text.setText(text.getText());//+"StateTime:"+sTm+"\n\n");
                        else text0 = text0 + "발표시각:"+sTm+"\n\n";
                        tTm=false;
                    }if(tItem){
                        weather = sWfKor[0];
                        for(int i=0;i<data;i++){
                            if(sDay[i]!=null){
                                if(Integer.parseInt(sDay[i])==0){
                                    if(op) text.setText(text.getText()+"[Today");
                                    else text0 = text0 +"날짜:"+"오늘"+" ";
                                }else if(Integer.parseInt(sDay[i])==1){
                                    if(op) text.setText(text.getText()+"[Tomorrow");
                                    else text0 = text0 +"날짜:"+"내일"+" ";
                                }else if(Integer.parseInt(sDay[i])==2){
                                    if(op) text.setText(text.getText()+"[Day After Tomorrow");
                                    else text0 = text0 +"날짜:"+"모래"+" ";
                                }
                            }
                            if(op) {
                                text.setText(text.getText() + " "+ sHour[i] + ":00] ");
                                text.setText(text.getText() + sTemp[i] + "℃ ");
                                text.setText(text.getText() + sWdKor[i] + "풍 ");
                                text.setText(text.getText() + "Humidity " + sReh[i] + "% ");
                                text.setText(text.getText() + "Weather #" + sWfKor[i] + "#\n\n");
                            }
                            else text0 = text0 + sHour[i]+"시\n" + "현재시간온도:"+sTemp[i]+"도"+" " + "풍향:"+sWdKor[i]+"풍"+" " + "습도:"+sReh[i]+"%"+" " + "날씨:"+sWfKor[i]+"\n\n";
                        }
                        tItem=false;
                        data=0;

                    }


                }
            });
        }
    }
}




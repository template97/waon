package com.example.yuju.waon02;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public final static int REQUEST_CODE_SET_ALARM = 1001;
    public final static int REQUEST_CODE_SETTING = 1002;
    public final static int REQUEST_CODE_UPDATE_ALARM = 1003;
    public static int NOT_USE = 5000;

    //User Saved Data
    int _location, _rain, _snow;
    boolean _useGPS, _useRain, _useSnow;
    String _locationName;
    ArrayList<Alarm> alarmListItem = new ArrayList<Alarm>();
    ArrayList<Alarm> alarmListItemTemp = new ArrayList<Alarm>();
    public static int ALARM_NUMBER = 0;

    public static AlarmManager alarmManager = null;
    static PendingIntent mAlarmIntent= null;

    //Views
    ListView _alarmList;
    ImageView settingImage, curWeather;
    AlarmListAdapter _alarmListAdapter;
    TextView fortext, curWeatherText;
    LinearLayout weatherLayout;

    //For Loaction
    TextView text;		//날씨 뿌려주는 텍스트창
    String text0, sCategory;	//동네
    String sTm;			//발표시각
    String [] sHour, sDay, sTemp, sWdKor, sReh, sWfKor;

    int data = 0;
    boolean bCategory,bTm, bHour, bDay, bTemp, bWdKor, bReh, bWfKor, tCategory, tTm, tItem;
    String weather;
    boolean op = true;
    Handler handler;	//핸들러

    String locationCode[]={"4200000000","4100000000", "4800000000","4700000000","2900000000","2700000000","3000000000","2600000000"
            ,"1100000000","3600000000","3100000000","2800000000","4600000000","4500000000","5000000000","4400000000","4300000000"};
    String locationList[]={"강원도","경기도","경상남도","경상북도","광주광역시","대구광역시","대전광역시","부산광역시","서울특별시"
            ,"세종특별자치시","울산광역시","인천광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도"};

    protected LocationManager locationManager;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    //Database
    DBOpenHelper helper;
    SQLiteDatabase db;
    public final static int DATA_VERSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.btn_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SET_ALARM);
            }
        });
        _location = 0;
        _rain = 0;
        _snow = 0;
        _useGPS = false;
        _useRain = false;
        _useSnow = false;

        _alarmList = (ListView) findViewById(R.id.alarmList);
        settingImage = (ImageView) findViewById(R.id.setting);
        fortext = (TextView) findViewById(R.id.fortest);
        curWeather = (ImageView)findViewById(R.id.curWeather);
        curWeatherText = (TextView)findViewById(R.id.curWeatherText);
        weatherLayout = (LinearLayout)findViewById(R.id.weatherLayout);

        callPermission();

        //Database
        helper = new DBOpenHelper(this, DATA_VERSION);
        try {
            readFromFile();
        }
        catch (Throwable e)
        { }

        _alarmListAdapter = new AlarmListAdapter(this, R.layout.alarm_list, alarmListItem);
        _alarmList.setAdapter(_alarmListAdapter);
        _alarmListAdapter.notifyDataSetChanged();

        int permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
        }else{
            Toast.makeText(getApplicationContext(), "인터넷 권한 없음", Toast.LENGTH_SHORT).show();
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
                Toast.makeText(getApplicationContext(), "권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS},1);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, 1);
            }
        }

        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                if(_useGPS) {
                    intent.putExtra("location", -1);
                }
                else{
                    intent.putExtra("location", _location);
                }
                if(_useRain)
                    intent.putExtra("rainDetail", _rain);
                else
                    intent.putExtra("rainDetail", NOT_USE);
                if(_useSnow)
                    intent.putExtra("snowDetail", _snow);
                else
                    intent.putExtra("snowDetail", NOT_USE);
                startActivityForResult(intent,REQUEST_CODE_SETTING);
            }
        });
        //weather
        bCategory=bTm=bHour=bTemp=bWdKor=bReh=bDay=bWfKor=tCategory=tTm=tItem=false;
        handler=new Handler();	//스레드&핸들러처리
        sHour=new String[40];	//예보시간(사실 15개밖에 안들어오지만 넉넉하게 20개로 잡아놓음)
        sDay=new String[40];	//날짜
        sTemp=new String[40];	//현재온도
        sWdKor=new String[40];	//풍향
        sReh=new String[40];	//습도
        sWfKor=new String[40];	//날씨
        text = (TextView)findViewById(R.id.fortest);

        weatherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });
        refreshCurWeather();

        curWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshCurWeather();
            }
        });
    }

    public void refreshCurWeather(){
        text.setText("");	//일단 중복해서 누를경우 대비해서 내용 지워줌
        network_thread thread=new network_thread();		//스레드생성(UI 스레드사용시 system 뻗는다)
        thread.start();	//스레드 시작
        if(curWeatherText.getText().toString() == "No Info")
            Toast.makeText(getApplicationContext(), "Weather Update Failure", Toast.LENGTH_SHORT).show();
        text.setText(locationList[_location]);
    }

    public void getLocationFromGPS(){
        GPSManager gps = new GPSManager(this);
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            _locationName = cityName;
            boolean check = false;
            for(int i=0; i<locationList.length; i++){
                if(locationList[i].equals(_locationName)){
                    _location = i;
                    check = true;
                }
            }
            if(!check){
                _location = 8;//seoul
                Toast.makeText(getApplicationContext(), _locationName + " is not included in location DB\nLocation is set to Seoul(default value)", Toast.LENGTH_SHORT).show();
            }

        } else {
            gps.showSettingsAlert();
        }
    }
    public class Alarm {
        int hour, min, id;
        String name, label;
        boolean forSnow, forRain, isWeekly, isVibration, _valid;
        int ringtone, volume;
        Calendar calendar = Calendar.getInstance();

        public Alarm(String name, String label, int hour, int min, boolean forRain, boolean forSnow, boolean isVibration, boolean _valid) {
            this.name = name;
            this.hour = hour;
            this.min = min;
            this.label = label;
            this.forRain = forRain;
            this.forSnow = forSnow;
            this.isVibration = isVibration;
            this.isWeekly = false;
            this.ringtone = 1;
            this.volume = 1;
            this._valid = _valid;
            this.calendar.set(Calendar.HOUR_OF_DAY, this.hour);
            this.calendar.set(Calendar.MINUTE, this.min);
            this.calendar.set(Calendar.SECOND, 0);

            ALARM_NUMBER += 1;
            this.id = ALARM_NUMBER;
        }
        public Alarm(String label, int hour, int min, boolean forRain, boolean forSnow, boolean isVibration, boolean _valid) {
            this.hour = hour;
            this.min = min;
            this.label = label;
            this.forRain = forRain;
            this.forSnow = forSnow;
            this.isVibration = isVibration;
            this.isWeekly = false;
            this.ringtone = 1;
            this.volume = 1;
            this._valid = _valid;
            this.calendar.set(Calendar.HOUR_OF_DAY, this.hour);
            this.calendar.set(Calendar.MINUTE, this.min);
            this.calendar.set(Calendar.SECOND, 0);

            ALARM_NUMBER += 1;
            this.name = "alarm" + ALARM_NUMBER;
            this.id = ALARM_NUMBER;
        }

        public void setCalendar() {
            this.calendar.set(Calendar.HOUR_OF_DAY, this.hour);
            this.calendar.set(Calendar.MINUTE, this.min);
            this.calendar.set(Calendar.SECOND, 0);
        }
    }

    class AlarmListAdapter extends BaseAdapter {
        ArrayList<Alarm> data;
        LayoutInflater inflater;
        int layout;
        Context context;

        public void setAlarmManager(Alarm _alarm){

            if (_alarm._valid) {
                if(_alarm.forRain && _useRain){
                    Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
                    alarmIntent.putExtra("label", _alarm.label);
                    alarmIntent.putExtra("hour", _alarm.hour);
                    alarmIntent.putExtra("min", _alarm.min);
                    alarmIntent.putExtra("vibrate", _alarm.isVibration);
                    alarmIntent.putExtra("valid", _alarm._valid);
                    alarmIntent.putExtra("location", _location);
                    alarmIntent.putExtra("rain", _rain);
                    alarmIntent.putExtra("snow", _snow);
                    alarmIntent.putExtra("id", _alarm.id);
                    long curTime = System.currentTimeMillis();
                    long setTime = _alarm.calendar.getTimeInMillis();
                    long period = 1000 * 60 * 60 * 24;
                    setTime = setTime + (_rain * 1000 * 60);
                    while (curTime > setTime) {
                        setTime += period;
                    }

                    alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmIntent.putExtra("weather", 1);
                    mAlarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (_alarm.id*10 + 1), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC, setTime, period, mAlarmIntent);
                }
                if(_alarm.forSnow && _useSnow){
                    Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
                    alarmIntent.putExtra("label", _alarm.label);
                    alarmIntent.putExtra("hour", _alarm.hour);
                    alarmIntent.putExtra("min", _alarm.min);
                    alarmIntent.putExtra("vibrate", _alarm.isVibration);
                    alarmIntent.putExtra("valid", _alarm._valid);
                    alarmIntent.putExtra("location", _location);
                    alarmIntent.putExtra("rain", _rain);
                    alarmIntent.putExtra("snow", _snow);
                    alarmIntent.putExtra("id", _alarm.id);
                    long curTime = System.currentTimeMillis();
                    long setTime = _alarm.calendar.getTimeInMillis();
                    long period = 1000 * 60 * 60 * 24;
                    setTime = setTime + (_snow * 1000 * 60);
                    while (curTime > setTime) {
                        setTime += period;
                    }

                    alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmIntent.putExtra("weather", 2);
                    mAlarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (_alarm.id*10 + 2), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC, setTime, period, mAlarmIntent);
                }
                Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
                alarmIntent.putExtra("label", _alarm.label);
                alarmIntent.putExtra("hour", _alarm.hour);
                alarmIntent.putExtra("min", _alarm.min);
                alarmIntent.putExtra("vibrate", _alarm.isVibration);
                alarmIntent.putExtra("valid", _alarm._valid);
                alarmIntent.putExtra("location", _location);
                alarmIntent.putExtra("rain", _rain);
                alarmIntent.putExtra("snow", _snow);
                alarmIntent.putExtra("id", _alarm.id);
                long curTime = System.currentTimeMillis();
                long setTime = _alarm.calendar.getTimeInMillis();
                long period = 1000 * 60 * 60 * 24;

                while (curTime > setTime) {
                    setTime += period;
                }

                alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmIntent.putExtra("weather", 0);
                mAlarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), _alarm.id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC, setTime, period, mAlarmIntent);
            }
        }

        public void cancelAlarm(Alarm _alarm){
            alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            if(_alarm.forRain){
                mAlarmIntent = PendingIntent.getBroadcast(context, (_alarm.id*10 + 1), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                if(alarmManager != null)
                    alarmManager.cancel(mAlarmIntent);
                if(mAlarmIntent != null)
                    mAlarmIntent.cancel();
                alarmManager = null;
                mAlarmIntent = null;
            }
            if(_alarm.forSnow) {
                mAlarmIntent = PendingIntent.getBroadcast(context, (_alarm.id * 10 + 2), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                if(alarmManager != null)
                    alarmManager.cancel(mAlarmIntent);
                if(mAlarmIntent != null)
                    mAlarmIntent.cancel();
                alarmManager = null;
                mAlarmIntent = null;
            }
            mAlarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), _alarm.id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            if(alarmManager != null)
                alarmManager.cancel(mAlarmIntent);
            if(mAlarmIntent != null)
                mAlarmIntent.cancel();
            alarmManager = null;
            mAlarmIntent = null;
        }
        @Override
        public void notifyDataSetChanged() {
            for (int i = 0; i < alarmListItemTemp.size(); i++) {
                Alarm _alarm = alarmListItemTemp.get(i);
                cancelAlarm(_alarm);
            }

            Collections.sort(data, new Comparator<Alarm>(){
                @Override
                public int compare(Alarm o1, Alarm o2) {
                    int t1 = o1.hour * 60 + o1.min;
                    int t2 = o2.hour * 60 + o2.min;
                    if(t1 > t2)
                        return 1;
                    else if(t1 < t2)
                        return -1;
                    else
                        return 0;
                }
            });

            for (int i = 0; i < data.size(); i++) {
                //알람 울리기
                Alarm _alarm = alarmListItem.get(i);
                _alarm.setCalendar();
                setAlarmManager(_alarm);
            }
            super.notifyDataSetChanged();
        }

        public AlarmListAdapter(Context context, int layout, ArrayList<Alarm> data) {
            this.context = context;
            this.data = data;
            this.layout = layout;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(alarmManager == null)
                alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);
            }
            final Alarm alarm = data.get(position);
            ImageView snow = (ImageView) convertView.findViewById(R.id.alarm_snow);
            ImageView rain = (ImageView) convertView.findViewById(R.id.alarm_rain);
            TextView time = (TextView) convertView.findViewById(R.id.alarm_time);
            TextView label = (TextView) convertView.findViewById(R.id.alarm_name);
            Switch _switch = (Switch) convertView.findViewById(R.id._switch);
            LinearLayout totalView = (LinearLayout)convertView.findViewById(R.id.totalView);

            _switch.setChecked(alarm._valid);
            _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        alarm._valid = true;
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat s1 = new SimpleDateFormat("HH");
                        SimpleDateFormat s2 = new SimpleDateFormat("mm");
                        int h = Integer.parseInt(s1.format(date));
                        int m = Integer.parseInt(s2.format(date));
                        m = alarm.min - m;
                        if(m < 0) {
                            h = alarm.hour - h - 1;
                            m += 60;
                        }
                        else {
                            h = alarm.hour - h;
                        }
                        if(h < 0)
                            h += 24;
                        Toast.makeText(context, "Alarm will ring " + h + " hour(s) " + m + " minute(s) after", Toast.LENGTH_LONG).show();
                    }
                    else{
                        alarm._valid = false;
                        Toast.makeText(context, "The alarm won't ring.", Toast.LENGTH_SHORT).show();
                    }
                    _alarmListAdapter.notifyDataSetChanged();
                    saveUPDATE(alarm);
                }
            });

            if (alarm.forSnow)
                snow.setImageResource(R.drawable.weather_snow);
            else
                snow.setImageResource(R.drawable.weather_nosnow);


            if (alarm.forRain)
                rain.setImageResource(R.drawable.weather_rainy);
            else
                rain.setImageResource(R.drawable.weather_norainy);

            View.OnClickListener snowListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = data.get(position);
                    ImageView snow = (ImageView) v;
                    if (alarm.forSnow) {
                        snow.setImageResource(R.drawable.weather_nosnow);
                        alarm.forSnow = false;
                        Toast.makeText(context, "The alarm won't change even if it snows.", Toast.LENGTH_SHORT).show();
                    } else {
                        snow.setImageResource(R.drawable.weather_snow);
                        alarm.forSnow = true;
                        Toast.makeText(context, "The alarm will change if it snows.", Toast.LENGTH_SHORT).show();
                    }
                    _alarmListAdapter.notifyDataSetChanged();
                    saveUPDATE(alarm);

                }
            };
            View.OnClickListener rainListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = data.get(position);
                    ImageView rain = (ImageView) v;
                    if (alarm.forRain) {
                        rain.setImageResource(R.drawable.weather_norainy);
                        alarm.forRain = false;
                        Toast.makeText(context, "The alarm won't change even if it rains.", Toast.LENGTH_SHORT).show();
                    } else {
                        rain.setImageResource(R.drawable.weather_rainy);
                        alarm.forRain = true;
                        Toast.makeText(context, "The alarm will change if it rains.", Toast.LENGTH_SHORT).show();
                    }
                    _alarmListAdapter.notifyDataSetChanged();
                    saveUPDATE(alarm);

                }
            };
            snow.setOnClickListener(snowListener);
            rain.setOnClickListener(rainListener);
            String hourText, minText;
            if (alarm.hour <= 9)
                hourText = "0" + alarm.hour;
            else hourText = alarm.hour + "";
            if (alarm.min <= 9)
                minText = "0" + alarm.min;
            else minText = alarm.min + "";

            time.setText(hourText + ":" + minText);
            if(alarm.label == null || alarm.label == "null")
                label.setText("");
            else
                label.setText(alarm.label);

            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = data.get(position);
                    Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                    intent.putExtra("label", alarm.label);
                    intent.putExtra("hour", alarm.hour);
                    intent.putExtra("min", alarm.min);
                    intent.putExtra("rain", alarm.forRain);
                    intent.putExtra("snow", alarm.forSnow);
                    intent.putExtra("valid", alarm._valid);
                    intent.putExtra("vibrate", alarm.isVibration);
                    intent.putExtra("index", position);
                    //intent.putExtra("index", )
                    startActivityForResult(intent, REQUEST_CODE_UPDATE_ALARM);
                }
            });

            View.OnLongClickListener longClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete the Alarm");
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to delete the alarm?");
                    //builder.setMessage(helpMessage[help]);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmListItemTemp = alarmListItem;
                            Alarm _a = alarmListItem.remove(position);
                            _alarmListAdapter.notifyDataSetChanged();
                            saveDELETE(_a);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                    return true;
                }
            };
            //totalView.setOnLongClickListener(longClick);
            time.setOnLongClickListener(longClick);
            snow.setOnLongClickListener(longClick);
            rain.setOnLongClickListener(longClick);
            label.setOnLongClickListener(longClick);

            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(MainActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_SET_ALARM) {
            boolean v = data.getBooleanExtra("valid", false);
            if (v) {
                String label = data.getStringExtra("label");
                //if (label == null)
                //    label = "none";
                int hour = data.getIntExtra("hour", 0);
                int min = data.getIntExtra("min", 0);
                boolean r, s, vb;
                r = data.getBooleanExtra("rain", false);
                s = data.getBooleanExtra("snow", false);
                vb = data.getBooleanExtra("vibrate", false);
                Alarm newAlarm = new Alarm(label, hour, min, r, s, vb, v);
                alarmListItem.add(newAlarm);
                _alarmListAdapter.notifyDataSetChanged();
                saveINSERT(newAlarm);
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat s1 = new SimpleDateFormat("HH");
                SimpleDateFormat s2 = new SimpleDateFormat("mm");
                int h = Integer.parseInt(s1.format(date));
                int m = Integer.parseInt(s2.format(date));
                m = newAlarm.min - m;
                if(m < 0) {
                    h = newAlarm.hour - h - 1;
                    m += 60;
                }
                else {
                    h = newAlarm.hour - h;
                }
                if(h < 0)
                    h += 24;
                Toast.makeText(getApplicationContext(), "Alarm will ring " + h + " hour(s) " + m + " minute(s) after", Toast.LENGTH_LONG).show();
            }

            //saveINSERT(name, hour, min, rain, snow, valid);************************
        }
        if (requestCode == REQUEST_CODE_UPDATE_ALARM) {

            boolean v = data.getBooleanExtra("valid", false);
            if (v) {
                int index = data.getIntExtra("index", -1);
                if(index == -1)
                    return;
                Alarm updateAlarm = alarmListItem.get(index);
                saveDELETE(updateAlarm);
                updateAlarm.label = data.getStringExtra("label");
                updateAlarm.hour = data.getIntExtra("hour", 0);
                updateAlarm.min = data.getIntExtra("min", 0);
                updateAlarm.forRain = data.getBooleanExtra("rain", false);
                updateAlarm.forSnow = data.getBooleanExtra("snow", false);
                updateAlarm.isVibration = data.getBooleanExtra("vibrate", false);
                updateAlarm._valid = true;
                alarmListItemTemp = alarmListItem;
                alarmListItem.remove(index);
                alarmListItem.add(0, updateAlarm);
                _alarmListAdapter.notifyDataSetChanged();
                saveINSERT(updateAlarm);

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat s1 = new SimpleDateFormat("HH");
                SimpleDateFormat s2 = new SimpleDateFormat("mm");
                int h = Integer.parseInt(s1.format(date));
                int m = Integer.parseInt(s2.format(date));
                m = updateAlarm.min - m;
                if(m < 0) {
                    h = updateAlarm.hour - h - 1;
                    m += 60;
                }
                else {
                    h = updateAlarm.hour - h;
                }
                if(h < 0)
                    h += 24;
                Toast.makeText(getApplicationContext(), "Alarm will ring " + h +" hour(s) " + m + " minute(s) after", Toast.LENGTH_LONG).show();
            }

            //saveINSERT(name, hour, min, rain, snow, valid);************************
        }
        if( requestCode == REQUEST_CODE_SETTING){
            boolean v = data.getBooleanExtra("valid", false);
            if(v){
                _location = data.getIntExtra("location", 0);
                if(_location == -1){
                    _location = 0;
                    _useGPS = true;

                    getLocationFromGPS();
                    //Toast.makeText(getApplicationContext(), getLocation(), Toast.LENGTH_SHORT).show();
                }
                else
                    _useGPS = false;

                _snow = data.getIntExtra("snowDetail", 0);
                if(_snow == NOT_USE)
                    _useSnow = false;
                else
                    _useSnow = true;
                _rain = data.getIntExtra("rainDetail", 0);
                if(_rain == NOT_USE)
                    _useRain = false;
                else
                    _useRain = true;
                saveSetting();
                refreshCurWeather();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                                case "흐림": case "구름 많음": case "구름 조금": sWfKor[data]= "Cloudy"; break;
                                case "비": case "눈/비": sWfKor[data]= "Rainy"; break;
                                case "맑음": sWfKor[data]= "Sunny"; break;
                                case "눈": sWfKor[data]= "Snowy"; break;
                                default: sWfKor[data]= "default"; break;
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

    // 권한 요청
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

        }
    }

    //========================== 데이터 베이스 ============================//
    public void saveSetting()
    {
        db = helper.getWritableDatabase();

        if(_useGPS)
            db.execSQL("update SETTING set LOCATION = -1;");
        else
            db.execSQL("update SETTING set LOCATION = " + _location + ";");

        db.execSQL("update SETTING set RAIN = " + _rain + ";");
        db.execSQL("update SETTING set SNOW = " + _snow + ";");

        db.close();
        helper.close();
    }

    public void saveINSERT(Alarm _alarm){
        db = helper.getWritableDatabase();

        int r,s,vb,v;
        if(_alarm.forRain)
            r = 1;
        else r = 0;
        if(_alarm.forSnow)
            s = 1;
        else s = 0;
        if(_alarm.isVibration)
            vb = 1;
        else vb = 0;
        if(_alarm._valid)
            v = 1;
        else v = 0;

        db.execSQL("insert into ALARM values (null, "
                + "'" + _alarm.name + "', "
                + "'" + _alarm.label + "', "
                + _alarm.hour + ", "
                + _alarm.min + ", "
                + r + ", "
                + s + ", "
                + vb + ", "
                + v + ");");

        db.close();
        helper.close();
    }

    public void saveDELETE(Alarm _alarm){
        db = helper.getWritableDatabase();

        db.execSQL("delete from ALARM where NAME = '" + _alarm.name + "';");

        db.close();
        helper.close();
    }

    public void saveUPDATE(Alarm _alarm){
        db = helper.getWritableDatabase();


        int r,s,vb,v;
        if(_alarm.forRain)
            r = 1;
        else r = 0;
        if(_alarm.forSnow)
            s = 1;
        else s = 0;
        if(_alarm.isVibration)
            vb = 1;
        else vb = 0;
        if(_alarm._valid)
            v = 1;
        else v = 0;

        db.execSQL("update ALARM set LABEL = '" + _alarm.label + "' where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set HOUR = " + _alarm.hour + " where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set MIN = " + _alarm.min + " where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set RAIN = " + r + " where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set SNOW = " + s + " where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set VIBRATE = " + vb + " where name = '" + _alarm.name + "';");
        db.execSQL("update ALARM set VALID = " + v + " where name = '" + _alarm.name + "';");

        db.close();
        helper.close();
    }

    public void readFromFile()
    {
        try
        {
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from SETTING", null);

            cursor.moveToFirst();

            if((_location = cursor.getInt(1)) == -1){
                _useGPS = true;
                getLocationFromGPS();
                //gps
            }
            else{
                _useGPS = false;
                //장소
            }

            _rain = cursor.getInt(2);
            _snow = cursor.getInt(3);
            if(_snow == NOT_USE)
                _useSnow = false;
            else
                _useSnow = true;
            if(_rain == NOT_USE)
                _useRain = false;
            else
                _useRain = true;
            cursor = null;
            cursor = db.rawQuery("select * from ALARM", null);

            cursor.moveToFirst();

            while(!cursor.isAfterLast())
            {
                String name = cursor.getString(1);
                String label = cursor.getString(2);
                int hour = cursor.getInt(3);
                int min = cursor.getInt(4);
                boolean forRain, forSnow, isVibration, _valid;
                if(cursor.getInt(5) == 1)
                    forRain = true;
                else
                    forRain = false;
                if(cursor.getInt(6) == 1)
                    forSnow = true;
                else
                    forSnow = false;
                if(cursor.getInt(7) == 1)
                    isVibration = true;
                else
                    isVibration = false;
                if(cursor.getInt(8) == 1)
                    _valid = true;
                else
                    _valid = false;
                alarmListItem.add(new Alarm(name, label, hour, min, forRain, forSnow, isVibration, _valid));
                cursor.moveToNext();
            }

            cursor.close();
            db.close();
            helper.close();
            alarmListItemTemp = alarmListItem;
        }
        catch(Throwable e)
        {
            Toast.makeText(getApplicationContext(), "저장된 데이터를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
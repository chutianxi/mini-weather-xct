package company.chutianxi.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import company.chutianxi.entity.TodayWeather;
import company.chutianxi.util.NetUtil;
import company.chutianxi.util.PinYin;

/**
 * Created by Administrator on 2017\9\25 0025.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateBtn;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ImageView mCitySelect;
    private ProgressBar progressUpdate;
    private String pmImgStr = "0_50";//初始化weatherImg图片的字符串
    private String typeImg;//天气类型的字符串地址
    private int pmvalue;//pm2.5的值，将pmvalue的处理放到了UpdateWeather方法里
    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    Toast.makeText(MainActivity.this,"更新成功！！！",Toast.LENGTH_SHORT).show();
                    mUpdateBtn.setVisibility(View.VISIBLE);
                    progressUpdate.setVisibility(View.INVISIBLE);
                    //Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        mUpdateBtn = findViewById(R.id.title_update_button);
        mCitySelect = findViewById(R.id.title_city_manager);
        progressUpdate = findViewById(R.id.title_update_progess);
        progressUpdate.setVisibility(View.INVISIBLE);
        progressUpdate.setProgress(0);
        mCitySelect.setOnClickListener(this);
        mUpdateBtn.setOnClickListener(this);
        initView();
    }
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        SharedPreferences pref = getSharedPreferences("config",MODE_PRIVATE);
        String responseStr =  pref.getString("responseStr","");
        if (responseStr=="")
        {
            city_name_Tv.setText("N/A");
            cityTv.setText("N/A");
            timeTv.setText("N/A");
            humidityTv.setText("N/A");
            pmDataTv.setText("N/A");
            pmQualityTv.setText("N/A");
            weekTv.setText("N/A");
            temperatureTv.setText("N/A");
            climateTv.setText("N/A");
            windTv.setText("N/A");
        }else{
            TodayWeather todayweather = parseXML(responseStr);
            updateTodayWeather(todayweather);
        }
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.title_city_manager)
        {
            Intent intent = new Intent(this,SelectCity.class);
            //this.startActivity(intent);
            this.startActivityForResult(intent,1);//requestCode为1
        }
        if(view.getId() == R.id.title_update_button)
        {
            mUpdateBtn.setVisibility(View.INVISIBLE);
            progressUpdate.setVisibility(View.VISIBLE);
            SharedPreferences pref = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = pref.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE)
            {
                Toast.makeText(this,"网络正常",Toast.LENGTH_LONG).show();
                queryWeatherCode(cityCode);
                //mUpdateBtn.setVisibility(View.VISIBLE);
                //progressUpdate.setVisibility(View.INVISIBLE);
            }else
            {
                Toast.makeText(this,"么有网络,请打开网络",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            String citycode = data.getStringExtra("citycode");
            //Log.d("name", "onActivityResult: "+name);
            //cityTv.setText(name);//cityTV仍然存在，因为main活动尚未出站，还持有他的引用
            SharedPreferences pref = getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("main_city_code",citycode);
            editor.commit();
            Toast.makeText(this,"已选择城市,请点击更新按钮",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *this is for query the website
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        boolean status;
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection(
                    );
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                        InputStream in = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            response.append(str);
                            Log.d("myWeather", str);
                        }
                        String responseStr = response.toString();
                        Log.d("myWeather", responseStr);
                        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);//每次更新将xml存入pref中
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("responseStr", responseStr);
                        editor.commit();
                        todayWeather = parseXML(responseStr);
                        Log.d("todayweather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
    void updateTodayWeather(TodayWeather todayWeather){
        if(todayWeather.getPm25()==null)
            pmvalue = 100;
        else
            pmvalue = Integer.parseInt(todayWeather.getPm25());
        if(pmvalue>50&&pmvalue<201)
        {
            int startV = (pmvalue-1)/50*50+1;
            int endV = ((pmvalue-1)/50+1)*50;
            pmImgStr = startV+"_"+endV;
        }else if(pmvalue>=201&&pmvalue<301)
        {
            pmImgStr = "201_300";
        }else{
            pmImgStr = "greater_300";
        }
        typeImg = "biz_plugin_weather_"+ PinYin.converterToSpell(todayWeather.getType());
        Class aclass = R.drawable.class;
        int typeId = -1;
        int pmId = -1;
        try {
            Field field = aclass.getField(typeImg);
            //为什么要用到Integer
            Object value = field.get(new Integer(0));
            typeId = (int)value;
            Field pmfield = aclass.getField(pmImgStr);
            Object pmvalue = pmfield.get(new Integer(0));
            pmId = (int)pmvalue;
        } catch (Exception e) {
            if(typeId==-1)
                typeId = R.drawable.biz_plugin_weather_qing;
            if(pmId==-1)
                pmId = R.drawable.biz_plugin_weather_0_50;
        }finally {
            Drawable drawable = getResources().getDrawable(typeId);
            weatherImg.setImageDrawable(drawable);
            drawable = getResources().getDrawable(pmId);
            pmImg.setImageDrawable(drawable);
        }


        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        //Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
}

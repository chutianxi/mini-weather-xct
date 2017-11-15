package company.chutianxi.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import company.chutianxi.adapter.CityAdapter;
import company.chutianxi.app.MyApplication;
import company.chutianxi.entity.City;

/**
 * Created by Administrator on 2017\10\18 0018.
 */

public class SelectCity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageView mBackBtn;
    ListView listview;
    List<City> cityList;
    List<String> citynamelist;
    CityAdapter cityadapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_city);
        mBackBtn = findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        listview = findViewById(R.id.citylist);
        cityList = MyApplication.getInstance().getCityList();
        citynamelist = new ArrayList<String>();
        for(int i=0;i<cityList.size();i++)
            citynamelist.add(cityList.get(i).getCity()) ;
        cityadapter = new CityAdapter(this,cityList);
        listview.setAdapter(cityadapter);
        listview.setOnItemClickListener(this);
        //listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,citynamelist));
    }

    @Override
    public void onClick(View view) {
    if(view.getId() == R.id.title_back)
    {
        //Intent intent = new Intent(this,MainActivity.class);
        //intent.putExtra("citycode","101160101");
        //setResult(RESULT_OK,intent);
        finish();
    }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        City city = (City) adapterView.getItemAtPosition(i);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("citycode",city.getNumber());
        setResult(RESULT_OK,intent);
        finish();
    }
}

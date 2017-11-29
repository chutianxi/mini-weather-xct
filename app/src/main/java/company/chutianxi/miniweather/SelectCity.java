package company.chutianxi.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import company.chutianxi.adapter.CityAdapter;
import company.chutianxi.app.MyApplication;
import company.chutianxi.entity.City;
import company.chutianxi.view.ClearEditText;


/**
 * Created by Administrator on 2017\10\18 0018.
 */

public class SelectCity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageView mBackBtn;
    ListView listview;
    ClearEditText search_city;
    List<City> cityList;
    List<City> filterList;
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
        search_city = findViewById(R.id.search_city);
        cityList = MyApplication.getInstance().getCityList();
        filterList = new ArrayList<City>();
        citynamelist = new ArrayList<String>();
        for(int i=0;i<cityList.size();i++)
            citynamelist.add(cityList.get(i).getCity()) ;
        cityadapter = new CityAdapter(this,cityList);
        listview.setAdapter(cityadapter);
        listview.setOnItemClickListener(this);
        search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(filterList.size()==0)
                    search_city.setShakeAnimation();
                filterData(charSequence.toString());
                //cityadapter = new CityAdapter(SelectCity.this,filterList);
                cityadapter.setCitys(filterList);
                listview.setAdapter(cityadapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,citynamelist));
    }

    /**
     * 根据输入框中的值，过滤并更新listview
     * @param s
     */
    private void filterData(String s) {
    if(s==""||s==null)
    {
    for(City city:cityList)
        filterList.add(city);
    }else
        {
            filterList.clear();
            for(City city:cityList)
            {
                if(city.getCity().indexOf(s.toString())!=-1)
                    filterList.add(city);
            }
        }
    //Collections.sort(filterlist,azcomparator）;
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

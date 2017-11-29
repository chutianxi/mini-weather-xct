package company.chutianxi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import company.chutianxi.entity.City;
import company.chutianxi.miniweather.R;

import static company.chutianxi.miniweather.R.id.city;
import static company.chutianxi.miniweather.R.id.citylist;

/**
 * Created by Administrator on 2017\11\8 0008.
 */

public class CityAdapter extends BaseAdapter {
    List<City> citys;
    Context context;
    public CityAdapter(Context context,List<City> citys) {
        this.citys = citys;
        this.context = context;
    }
    public List<City> getCitys() {
        return citys;
    }

    public void setCitys(List<City> citys) {
        this.citys = citys;
    }

    @Override
    public int getCount() {
        return citys.size();
    }

    @Override
    public Object getItem(int i) {
        return citys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View ConvertView, ViewGroup viewGroup) {
        View view;
        if(ConvertView != null)
            view = ConvertView;
        else {
            view = View.inflate(context, R.layout.city_list, null);
        }
        TextView textview = view.findViewById(R.id.cityname);
        textview.setText(citys.get(i).getCity());
        return view;
    }
}

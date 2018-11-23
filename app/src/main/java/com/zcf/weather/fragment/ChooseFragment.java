package com.zcf.weather.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.zcf.weather.R;
import com.zcf.weather.Util.HttpUtil;
import com.zcf.weather.Util.Utility;
import com.zcf.weather.db.City;
import com.zcf.weather.db.County;
import com.zcf.weather.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titletext;
    private Button backbt;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist =new ArrayList<>();
    private int currentLevel;
    private Province sprovince;
    private City scity;
    private County scounty;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private int spcode,sccode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_city,container,false);
        titletext = view.findViewById(R.id.title_text1);
        backbt = view.findViewById(R.id.back_bt);
        listView = view.findViewById(R.id.list_item);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    sprovince = provinceList.get(position);
                        queryCity();
                }
                else if(currentLevel==LEVEL_CITY){
                    scity= cityList.get(position);
                    queryCounty();
                    //Log.d("ttt--",scity.getId()+"  "+scity.getCityName());
                }
            }
        });
        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }
                else if(currentLevel==LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }
    private void queryProvince(){
        titletext.setText("中国");
        backbt.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            datalist.clear();
            for (Province province :provinceList){
                datalist.add(province.getpName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }
        else {
            String adress ="http://guolin.tech/api/china";
            queryFromServer(adress,"province");
        }

    }
    private void queryCity(){
        titletext.setText(sprovince.getpName());
        backbt.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("pid = ?",
                String.valueOf(sprovince.getId())).find(City.class);
//        cityList = DataSupport.where("id = 1"
//                ).find(City.class);
        if(cityList.size()>0){
            datalist.clear();
            for (City city :cityList){
                datalist.add(city.getCityName());
                //Log.d("ttt--1",city.getCityCode()+"");
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }
        else {
            spcode = sprovince.getId();
            String adress = "http://guolin.tech/api/china/"+spcode;
            queryFromServer(adress,"city");
        }

    }
    private void queryCounty(){
        titletext.setText(scity.getCityName());
        backbt.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(scity.getCityCode())).find(County.class);
        if(countyList.size()>0){
            datalist.clear();
            for (County county:countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            sccode = scity.getCityCode();
            String adress = "http://guolin.tech/api/china/"+spcode+"/"+sccode;
            queryFromServer(adress,"county");
        }

    }

    private void queryFromServer(String adress , final String ifo){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(adress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseT = response.body().string();
                boolean result =false;
                if("province".equals(ifo)){
                    result = Utility.handleProvinceResponse(responseT);
                }
                else if (ifo.equals("city")){
                    result = Utility.handleCityResponse(responseT,sprovince.getId());

                }
                else if(ifo.equals("county")){
                    result = Utility.handleCountyResponse(responseT,scity.getCityCode());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(ifo)){
                                queryProvince();
                            }
                            else if (ifo.equals("city")){
                                queryCity();

                            }
                            else if(ifo.equals("county")){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog ==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}

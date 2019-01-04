package com.automata.calculatorkenya;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Fragment {

    private List<Model> mList = new ArrayList<>();
    RecyclerAdapter mAdapter;
    View v;
    private AdView mBannerAd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       v=inflater.inflate(R.layout.activity_main,container,false);

        mBannerAd = v.findViewById(R.id.banner_AdView);
        mBannerAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mBannerAd.setVisibility(View.VISIBLE);
            }
        });
        showBannerAd();

        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        mAdapter = new RecyclerAdapter(getActivity(), mList);
        populateRecycler();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return  v;
    }


    private void showBannerAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mBannerAd.loadAd(adRequest);

    }

    private void populateRecycler() {

        int[] covers = new int[]{
                R.drawable.income_tax,
                R.drawable.mpesa,
                R.drawable.airtel_money,
                R.drawable.mshwari,
                R.drawable.helb,
                R.drawable.kplc,
                R.drawable.kplc_prepaid,
                R.drawable.water,
                R.drawable.tsc,
                R.drawable.civil,
                R.drawable.motor,
                R.drawable.currency,
                R.drawable.bmw,
                R.drawable.uber,
                R.drawable.kcbmpesa


        };
        Model m = new Model("Income tax-PAYE", covers[0]);
        mList.add(m);
        m = new Model("M-PESA Cost", covers[1]);
        mList.add(m);
        m = new Model("Airtel Money Cost", covers[2]);
        mList.add(m);
        m = new Model("M-Shwari Loan", covers[3]);
        mList.add(m);
        m = new Model("HELB Loan", covers[4]);
        mList.add(m);
        m = new Model("KPLC Postpaid", covers[5]);
        mList.add(m);
        m = new Model("KPLC Prepaid Tokens", covers[6]);
        mList.add(m);
        m = new Model("Nairobi Water Bill", covers[7]);
        mList.add(m);
        m = new Model("TSC Salary", covers[8]);
        mList.add(m);
        m = new Model("Civil Servants Salary", covers[9]);
        mList.add(m);
        m = new Model("Motor Insurance", covers[10]);
        mList.add(m);
        m = new Model("Currency Converter", covers[11]);
        mList.add(m);
        m=new Model("Motor Vehicle Import Duty",covers[12]);
        mList.add(m);
        m=new Model("Uber Fare Estimator",covers[13]);
        mList.add(m);
        m=new Model("KCB Mpesa Loan",covers[14]);
        mList.add(m);



        mAdapter.notifyDataSetChanged();
    }
}

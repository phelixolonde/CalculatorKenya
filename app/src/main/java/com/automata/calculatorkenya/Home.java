package com.automata.calculatorkenya;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Fragment fragment = null;
    NavigationView navigationView;
    View contentView;
    private InterstitialAd mInterstitialAd;



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mInterstitialAd = createNewIntAd();
        loadIntAdd();

        drawer = findViewById(R.id.drawer_layout);
        contentView = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.addDrawerListener(toggle);

        toggle.syncState();


        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new MainActivity());
        tx.commit();


    }

    private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(Home.this);
        // set the adUnitId (defined in values/strings.xml)
        intAd.setAdUnitId(getString(R.string.interstitial));
        intAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        showIntAdd();
                    }
                }, 6000);

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
            }
        });
        return intAd;
    }

    private void loadIntAdd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showIntAdd() {

// Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_share) {
            String shareBody = "Calculator Kenya is a financial companion. It's performs your calculations in speeds. Download here https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Calculator Kenya");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, ("Share Using")));
        }

        return super.onOptionsItemSelected(item);
    }


    // Handle navigation view item clicks

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int itemId) {


        switch (itemId) {
            case R.id.nav_home:
                fragment = new MainActivity();

                break;

            case R.id.nav_bmi:
                fragment = new BMIFragment();
                getSupportActionBar().setTitle("BMI Calculator");
                break;

            case R.id.nav_paybill:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Home.this, "Unable to find play store", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_share:
                String shareBody = "Calculator Kenya is a financial companion. It's performs your calculations in speeds.Download here https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Calculate Kenya");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, ("Share Using")));
                break;

            case R.id.nav_feedback:
                getSupportActionBar().setTitle("Feedback");
                fragment = new FeedbackFragment();
                break;

            case R.id.nav_about:
                getSupportActionBar().setTitle("About app");
                fragment = new AboutFragment();
                break;

            default:
                fragment = new MainActivity();

                break;

        }
        drawer.closeDrawer(GravityCompat.START);


        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }


}

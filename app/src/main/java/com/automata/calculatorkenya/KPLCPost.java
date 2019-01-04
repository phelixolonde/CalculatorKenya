package com.automata.calculatorkenya;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class KPLCPost extends AppCompatActivity {
    EditText txtAmount, txtDate, txtHeater;
    Button btnCalculate;
    TableLayout tblResult;
    ScrollView scroll;
    AVLoadingIndicatorView avi;
    CardView cardView;
    Spinner spMonths, spTarrifs;

    //private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kplcpost);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

       /* mInterstitialAd = createNewIntAd();
        loadIntAdd();*/

        spMonths = findViewById(R.id.spMonths);
        spTarrifs = findViewById(R.id.spTariffs);

        scroll = findViewById(R.id.scroll);
        tblResult = findViewById(R.id.tblResult);
        txtAmount = findViewById(R.id.txtConsumption);
        txtDate = findViewById(R.id.txtDemand);
        txtHeater = findViewById(R.id.txtHeater);

        btnCalculate = findViewById(R.id.btnCalculate);
        avi = findViewById(R.id.avi);
        cardView = findViewById(R.id.card_view);

        txtAmount.addTextChangedListener(new NumberTextWatcherForThousand(txtAmount));


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tblResult.removeAllViews();
                hideKeyboard(KPLCPost.this);

                if (isNetworkConnected()) {
                    if (txtAmount.getText().toString().equals("")) {
                        txtAmount.setError("Enter a valid value");
                    } else {
                        txtAmount.setError(null);
                        int salary = Integer.parseInt(txtAmount.getText().toString().replace(",", ""));
                        getData(salary, Integer.parseInt(txtDate.getText().toString()), spMonths.getSelectedItemPosition() + 1, spTarrifs.getSelectedItemPosition() + 1, Integer.parseInt(txtHeater.getText().toString()));
                    }
                } else {
                    Toast.makeText(KPLCPost.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

   /* private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(KPLCPost.this);
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
                }, 30000);

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
*/
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(int salary, int demand, int month, int tarrif, int heater) {
        String url = "https://calculator.co.ke/math.php?units=" + salary + "&tariff=" + tarrif + "&heater=" + heater + "&demand=" + demand + "&postmonth=" + month + "&postyear=11&rand=58487669";

        Log.i("KPLCPost_URL", url);
        avi.show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        avi.hide();
                        cardView.setVisibility(View.VISIBLE);

                        Document doc = Jsoup.parse(response);
                        Elements tables = doc.select("table");
                        for (Element table : tables) {
                            Elements trs = table.select("tr");
                            String[][] trtd = new String[trs.size()][];

                            for (int i = 0; i < trs.size(); i++) {
                                TableRow row = new TableRow(KPLCPost.this);
                                if (i % 2 == 0) {
                                    row.setBackgroundColor(getResources().getColor(R.color.gray));
                                }

                                Elements tds = trs.get(i).select("td");
                                trtd[i] = new String[tds.size()];


                                for (int j = 0; j < tds.size(); j++) {
                                    trtd[i][j] = tds.get(j).text();

                                    final TextView flight = new TextView(KPLCPost.this);
                                    flight.setText(trtd[i][j]);
                                    flight.setPadding(10, 20, 10, 20);
                                    row.addView(flight);
                                    if (j % 2 == 1) {
                                        flight.setTypeface(null, Typeface.BOLD);
                                    } else {
                                        flight.setMaxWidth(400);
                                    }


                                }
                                tblResult.addView(row);
                            }

                        }

                        scroll.post(new Runnable() {
                            @Override
                            public void run() {

                                scroll.smoothScrollTo(0, tblResult.getBottom());
                            }
                        });


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.hide();
                        Log.e("KPLCPost_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(KPLCPost.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(KPLCPost.this, "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(KPLCPost.this, "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(KPLCPost.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(KPLCPost.this, "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(KPLCPost.this, "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "KPLCPost_REQUEST");
    }
}

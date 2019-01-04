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

public class KPLCPre extends AppCompatActivity {
    EditText txtP1, txtP2, txtP3, txtP4;
    Button btnCalculate;
    TableLayout tblResult;
    ScrollView scroll;
    AVLoadingIndicatorView avi;
    CardView cardView;
    Spinner spMonths;
    //private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kplcpre);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        /*mInterstitialAd = createNewIntAd();
        loadIntAdd();
*/
        spMonths = findViewById(R.id.spMonths);

        scroll = findViewById(R.id.scroll);
        tblResult = findViewById(R.id.tblResult);
        txtP1 = findViewById(R.id.txtPayment1);
        txtP2 = findViewById(R.id.txtPayment2);
        txtP3 = findViewById(R.id.txtPayment3);
        txtP4 = findViewById(R.id.txtPayment4);

        btnCalculate = findViewById(R.id.btnCalculate);
        avi = findViewById(R.id.avi);
        cardView = findViewById(R.id.card_view);

        txtP1.addTextChangedListener(new NumberTextWatcherForThousand(txtP1));
        txtP2.addTextChangedListener(new NumberTextWatcherForThousand(txtP2));
        txtP3.addTextChangedListener(new NumberTextWatcherForThousand(txtP3));
        txtP4.addTextChangedListener(new NumberTextWatcherForThousand(txtP4));

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    int p1 = Integer.parseInt(txtP1.getText().toString().replace(",", ""));
                    int p2 = Integer.parseInt(txtP2.getText().toString().replace(",", ""));
                    int p3 = Integer.parseInt(txtP3.getText().toString().replace(",", ""));
                    int p4 = Integer.parseInt(txtP4.getText().toString().replace(",", ""));
                    if (isNetworkConnected()) {
                        getData(p1, p2, p3, p4, spMonths.getSelectedItemPosition() + 1);
                    } else {
                        Toast.makeText(KPLCPre.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    txtP1.setError("Enter a valid value");
                }


                tblResult.removeAllViews();
                hideKeyboard(KPLCPre.this);

            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    /*private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(KPLCPre.this);
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
    }*/


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
        String url = "https://calculator.co.ke/math.php?payment_1=" + salary + "&payment_2=" + demand + "&payment_3=" + month + "&payment_4=" + tarrif + "&premonth=" + heater + "&&kplc_rate=new&preyear=11&rand=84832028";

        Log.i("KPLCPre_URL", url);
        avi.show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        avi.hide();
                        cardView.setVisibility(View.VISIBLE);

                        Document doc = Jsoup.parse(response);
                        Elements tables = doc.select("table");
                        int k = 1;
                        for (Element table : tables) {
                            Elements trs = table.select("tr");
                            String[][] trtd = new String[trs.size()][];


                            TableRow header_row = new TableRow(KPLCPre.this);
                            TextView txtHeader = new TextView(KPLCPre.this);
                            txtHeader.setText("Payment " + k);
                            header_row.addView(txtHeader);
                            txtHeader.setTypeface(null, Typeface.BOLD);
                            tblResult.addView(header_row);
                            k++;

                            // Log.i("CALC_i","i is");

                            for (int i = 0; i < trs.size(); i++) {
                                TableRow row = new TableRow(KPLCPre.this);


                                if (i % 2 == 0) {
                                    row.setBackgroundColor(getResources().getColor(R.color.gray));
                                }

                                Elements tds = trs.get(i).select("td");
                                trtd[i] = new String[tds.size()];


                                for (int j = 0; j < tds.size(); j++) {
                                    trtd[i][j] = tds.get(j).text();


                                    final TextView flight = new TextView(KPLCPre.this);
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
                        Log.e("KPLCPre_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(KPLCPre.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(KPLCPre.this, "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(KPLCPre.this, "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(KPLCPre.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(KPLCPre.this, "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(KPLCPre.this, "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "KPLCPre_REQUEST");
    }
}

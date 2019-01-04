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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Converter extends AppCompatActivity {
    EditText txtAmount;
    Button btnCalculate;
    TableLayout tblResult;
    ScrollView scroll;
    AVLoadingIndicatorView avi;
    CardView cardView;
    Spinner spFrom, spTo;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        mInterstitialAd = createNewIntAd();
        loadIntAdd();

        spFrom = findViewById(R.id.spFrom);
        spTo = findViewById(R.id.spTo);
        spFrom.setSelected(false);
        spFrom.setSelection(0, true);
        /*spTo.setSelected(false);
        spTo.setSelection(0,true);*/
        spTo.setSelection(21);

        spFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!spFrom.getSelectedItem().toString().equalsIgnoreCase("KENYAN SHILLING")) {

                    spTo.setSelection(21);
                    spTo.setEnabled(false);
                } else {

                    spTo.setSelection(0, true);
                    spTo.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!spTo.getSelectedItem().toString().equalsIgnoreCase("KENYAN SHILLING")) {
                    spFrom.setSelection(21);
                    spFrom.setEnabled(false);

                } else {

                    spFrom.setSelection(0, true);
                    spFrom.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        scroll = findViewById(R.id.scroll);
        tblResult = findViewById(R.id.tblResult);
        txtAmount = findViewById(R.id.txtAmount);


        btnCalculate = findViewById(R.id.btnCalculate);
        avi = findViewById(R.id.avi);
        cardView = findViewById(R.id.card_view);

        txtAmount.addTextChangedListener(new NumberTextWatcherForThousand(txtAmount));


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tblResult.removeAllViews();
                hideKeyboard(Converter.this);

                if (isNetworkConnected()) {
                    if (txtAmount.getText().toString().equals("")) {
                        txtAmount.setError("Enter a valid amount");
                    } else {
                        double amount = Double.parseDouble(txtAmount.getText().toString().replace(",", ""));
                        getData(amount, getSelectedCurrency(spFrom), getSelectedCurrency(spTo));
                    }
                } else {
                    Toast.makeText(Converter.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(Converter.this);
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

    public String getSelectedCurrency(Spinner spinner) {
        if (spinner.getSelectedItem().equals("KENYAN SHILLING")) {
            return "KES";
        }
        return spinner.getSelectedItem().toString();
    }

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

    private void getData(double amount, String x, String y) {
        String url = "https://calculator.co.ke/math.php?currency_amount=" + amount + "&currency_x=" + x + "&currency_y=" + y + "&rand=32215493";

        Log.i("Converter_URL", url);
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
                                TableRow row = new TableRow(Converter.this);

                                Elements tds = trs.get(i).select("td");
                                trtd[i] = new String[tds.size()];


                                for (int j = 0; j < tds.size(); j++) {
                                    trtd[i][j] = tds.get(j).text();

                                    final TextView flight = new TextView(Converter.this);
                                    flight.setText(trtd[i][j]);
                                    flight.setPadding(10, 40, 10, 20);

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
                        Log.e("Converter_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(Converter.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(Converter.this, "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(Converter.this, "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(Converter.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(Converter.this, "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Converter.this, "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "Converter_REQUEST");
    }
}

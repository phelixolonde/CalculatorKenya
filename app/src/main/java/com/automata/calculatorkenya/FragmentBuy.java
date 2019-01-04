package com.automata.calculatorkenya;


import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by HANSEN on 12/07/2018.
 */

public class FragmentBuy extends Fragment {
    View v;
    Button btnCalculate;
    Spinner spYear, spMonth, spBody, spMakes, spModels, spEngine;
    AVLoadingIndicatorView avi;
    EditText txtCIF, txtInsurance;
    HashMap<String, String> map_model = new HashMap<>();
    HashMap<String, String> capacity_map = new HashMap<>();
    CardView cardView;
    TableLayout tblResult;
    ScrollView scroll;
    TextView txtTotal;

    public FragmentBuy() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new, container, false);

        spBody = v.findViewById(R.id.spBody);
        spYear = v.findViewById(R.id.spYears);
        spMonth = v.findViewById(R.id.spMonths);
        spMakes = v.findViewById(R.id.spMakes);
        spModels = v.findViewById(R.id.spModels);
        spEngine = v.findViewById(R.id.spEngine);
        txtCIF = v.findViewById(R.id.txtCIF);
        txtInsurance = v.findViewById(R.id.txtInsurance);
        cardView = v.findViewById(R.id.card_view);
        tblResult = v.findViewById(R.id.tblResult);
        scroll = v.findViewById(R.id.scroll);
        txtTotal = v.findViewById(R.id.txtTotal);

        btnCalculate = v.findViewById(R.id.btnCalculate);
        avi = v.findViewById(R.id.avi);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        ArrayList<String> years = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            int y = year - i;
            years.add(String.valueOf(y));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.insert(String.valueOf(year), 0);
        spYear.setAdapter(adapter);
        //spYear.setSelection(1);
        spYear.setSelected(true);


        spBody.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spBody.getSelectedItemPosition() != 0) {

                    getVehiclesMakes(spBody.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spMakes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spMakes.getSelectedItemPosition() != 0) {

                    getVehiclesModels(spMakes.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spModels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spModels.getSelectedItemPosition() != 0) {

                    getVehicleCapacity(map_model.get(spModels.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tblResult.removeAllViews();
                cardView.setVisibility(View.GONE);
                if (isNetworkConnected()) {
                    try {

                        calculateRate(new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                Document doc = Jsoup.parse(result);
                                Element input = doc.select("input[name=car_rate]").last();
                                String val = input.attr("value");

                                try {
                                    calculateTotal(Double.parseDouble(txtInsurance.getText().toString()), capacity_map.get(spEngine.getSelectedItem().toString()), val, spMonth.getSelectedItem().toString(), spYear.getSelectedItem().toString());
                                }catch (Exception e){
                                    if (spBody.getSelectedItemPosition()==0){
                                        Toast.makeText(getContext(), "Please select body type, make and model", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                }
                            });
                        }
                catch(Exception e){
                            Toast.makeText(getContext(), "Sorry, you chose an invalid option", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "You have no internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        return v;
        }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    private void calculateRate(final VolleyCallback callback) {

        String url = "https://calculator.co.ke/math.php?car_month=" + spMonth.getSelectedItem().toString() + "&car_year=" + spYear.getSelectedItem().toString() + "&rand=72650241";
        Log.i("RATES_URL", url);

        avi.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                        Log.i("RATES_RESPONSE", response);
                        avi.hide();
                        cardView.setVisibility(View.VISIBLE);


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.hide();
                        Log.e("RATES_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext(), "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "MOTOR_RATES_REQUEST");

    }

    private void calculateTotal(double insure, String capacity, String rate, String month, String year) {
        String url = "https://calculator.co.ke/math.php?motor_value=0&insure_rate=" + insure + "&car_capacity=" + capacity + "&car_rate=" + rate + "&car_status=new&car_cost=0&motor_month=" + month + "&motor_year=" + year + "&rand=85205924";

        Log.i("BUY_URL", url);
        avi.show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("BUY_RESPONSE", response);

                        avi.hide();
                        cardView.setVisibility(View.VISIBLE);

                        Document doc = Jsoup.parse(response);
                        Elements tables = doc.select("table");
                        for (Element table : tables) {
                            Elements trs = table.select("tr");
                            String[][] trtd = new String[trs.size()][];

                            for (int i = 0; i < trs.size(); i++) {
                                TableRow row = new TableRow(getActivity());
                                if (i % 2 == 0) {
                                    row.setBackgroundColor(getResources().getColor(R.color.gray));
                                }

                                Elements tds = trs.get(i).select("td");
                                trtd[i] = new String[tds.size()];


                                for (int j = 0; j < tds.size(); j++) {
                                    trtd[i][j] = tds.get(j).text();

                                    final TextView flight = new TextView(getActivity());
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
                        Log.e("BUY_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext(), "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "BUY_REQUEST");
    }

    private void getVehicleCapacity(String model) {
        String url = "https://calculator.co.ke/math.php?car_model=" + model + "&rand=6586480";
        Log.i("MODELS_URL", url);

        avi.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MODELS_RESPONSE", response);
                        avi.hide();

                        ArrayList<String> models = new ArrayList<>();

                        Document doc = Jsoup.parse(response);
                        Elements options = doc.getElementsByAttributeValue("name", "car_capacity").get(0).children();
                        for (Element option : options) {
                            models.add(option.text());

                            capacity_map.put(option.text(), option.val());
                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, models);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spEngine.setAdapter(adapter);


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.hide();
                        Log.e("MOTOR_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext(), "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "MOTOR_MAKE_REQUEST");
    }

    private void getVehiclesModels(int i) {
        String url = "https://calculator.co.ke/math.php?car_make=" + i + "&rand=6586480";
        Log.i("MODELS_URL", url);

        avi.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MODELS_RESPONSE", response);
                        avi.hide();


                        ArrayList<String> models = new ArrayList<>();

                        Document doc = Jsoup.parse(response);
                        Elements options = doc.getElementsByAttributeValue("name", "car_model").get(0).children();
                        for (Element option : options) {
                            models.add(option.text());


                            map_model.put(option.text(), option.val());

                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, models);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spModels.setAdapter(adapter);


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.hide();
                        Log.e("MOTOR_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext(), "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "MOTOR_MAKE_REQUEST");
    }

    private void getVehiclesMakes(int s) {
        String url = "https://calculator.co.ke/math.php?car_body=" + s + "&rand=6586480";
        Log.i("MAKES_URL", url);

        avi.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MAKES_RESPONSE", response);
                        avi.hide();

                        ArrayList<String> models = new ArrayList<>();

                        Document doc = Jsoup.parse(response);
                        Elements options = doc.getElementsByAttributeValue("name", "car_make").get(0).children();
                        for (Element option : options) {
                            models.add(option.text());
                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, models);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spMakes.setAdapter(adapter);


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.hide();
                        Log.e("MOTOR_VOLLEY_ERROR", "error " + error);
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(), "Server unreachable.Please try again later", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext(), "Server error.Please try again later", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getContext(), "An error occurred on our side.Please contact us with error code 5", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "An error occurred.Please contact us", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
        CalculatorKenya.getInstance().addToRequestQueue(postRequest, "MOTOR_MAKE_REQUEST");
    }

    public interface VolleyCallback {
        void onSuccess(String result);
    }
}

package com.rx.rxpanel.SM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rx.rxpanel.MainActivity;
import com.rx.rxpanel.Model.RxDataList;
import com.rx.rxpanel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class SmFragment extends Fragment {
    private Spinner rsmSpinner, dsmSpinner, psoSpinner, productSpinner, teamSpinner, regionSpinner;
    private ArrayList<String> rsmList, dsmList, psoList, productList, teamList, regionList, sampleRxDataList;
    private String name,brand;
    private EditText editText, editText2;
    SharedPreferences pref;
    private Button button;
    private ProgressDialog progressDialog;
    File directory, sd, file;
    WritableWorkbook workbook;
    private ArrayList<RxDataList> RxList;
    private EditText et_brand;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sm, container, false);
        rsmSpinner = view.findViewById(R.id.rsm_spinner);
        dsmSpinner = view.findViewById(R.id.dsm_spinner);
        psoSpinner = view.findViewById(R.id.pso_spinner);
        productSpinner = view.findViewById(R.id.product_spinner);
        teamSpinner = view.findViewById(R.id.team_spinner);
        regionSpinner = view.findViewById(R.id.region_spinner);
        editText = view.findViewById(R.id.from_date);
        editText2 = view.findViewById(R.id.to_date);
        button = view.findViewById(R.id.bt_download);
        et_brand = view.findViewById(R.id.et_brand);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(" Processing It Will Take 5-10 min...");
        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        String name = pref.getString("key_name", "");

//brand


        //all arrayList

        rsmList = new ArrayList<String>();
        dsmList = new ArrayList<String>();
        psoList = new ArrayList<String>();
        teamList = new ArrayList<String>();
        productList = new ArrayList<String>();
        regionList = new ArrayList<String>();
        sampleRxDataList = new ArrayList<String>();
        RxList = new ArrayList<RxDataList>();

        rsmList.add(0, "Select");
        dsmList.add(0, "Select");
        psoList.add(0, "Select");
        productList.add(0, "Select");
        teamList.add(0, "Select");
        regionList.add(0, "Select");

        CallSmffc callSmffc = new CallSmffc(getActivity());
        callSmffc.execute("http://renata-vision.xyz/rx_new/get_smffc.php?id=" + name);
//only this url will change as per user
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(dayOfMonth);
                        editText.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(dayOfMonth);
                        editText2.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });


        rsmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("rsm", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        dsmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("dsm", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        psoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("pso", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("product", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("team", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("region", String.valueOf(adapterView.getItemAtPosition(i)));
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                brand = et_brand.getText().toString();
                String smffc = pref.getString("smffc", "");
                String rsm = pref.getString("rsm", "");
                String dsm = pref.getString("dsm", "");
                String pso = pref.getString("pso", "");
                String team = pref.getString("team", "");
                String region = pref.getString("region", "");
                String product = pref.getString("product", "");
                String f_date = editText.getText().toString();
                String t_date = editText2.getText().toString();



                if (!rsm.equals("Select") && !dsm.equals("Select") && !pso.equals("Select") && !team.equals("Select") && !region.equals("Select") && !product.equals("Select")) {
                    //thats means Everything is selected
                    Log.d("notice", "Everything is Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&rsm="+rsm;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();

                }

                if (rsm.equals("Select") && dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select") && !brand.isEmpty()) {
                    //thats means brand is selected
                    Log.d("notice", "Everything is Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&brand="+brand;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();

                }


                if (!rsm.equals("Select") && dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select")) {
                    //only rsm  selected

                    RxList.clear();
                    Log.d("notice", "Only Rsm Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&rsm="+rsm;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();
                }


                if (rsm.equals("Select") && !dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select")) {


                    //only dsm

                    Log.d("notice", "Only dsm Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&dsm="+dsm;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();


                }


                if (rsm.equals("Select") && dsm.equals("Select") && !pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select")) {

                    //only pso

                    Log.d("notice", "Only pso Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&pso="+pso;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);

                    RxList.clear();
                }

                if (rsm.equals("Select") && dsm.equals("Select") && pso.equals("Select") && !team.equals("Select") && region.equals("Select") && product.equals("Select")) {
                    //only team

                    Log.d("notice", "Only team Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&team="+team;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);

                    RxList.clear();
                }
                if (rsm.equals("Select") && dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && !region.equals("Select") && product.equals("Select")) {
                    //only Region

                    Log.d("notice", "Only Region Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&region="+region;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();
                }
                if (rsm.equals("Select") && dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && !product.equals("Select")) {
                    //only Product


                    Log.d("notice", "Only Region Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&product="+product;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();

                }


                if (!rsm.equals("Select") && !dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select")) {

                    //only rsm  & dsm selected

                    Log.d("notice", "Only rsm dsm Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&dsm="+dsm;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();

                }


                if (rsm.equals("Select") && !dsm.equals("Select") && pso.equals("Select") && team.equals("Select") && region.equals("Select") && product.equals("Select")) {

                    //only dsm rsm pso   selected
                    Log.d("notice", "Only rsm dsm pso Selected");
                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date="+f_date+"&t_date="+t_date+"&pso="+pso;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();
                } else {
                    //thats means only Date Selected nothing Else
                    Log.d("notice", "Only Date Selected");

                    String url = "http://renata-vision.xyz/rx_new/sm_rx.php?smffc="+smffc+"&f_date=" + f_date + "&t_date=" + t_date;
                    CallRxData callRxData = new CallRxData(getActivity());
                    callRxData.execute(url);
                    RxList.clear();
                }


            }
        });

        return view;
    }


    public class CallSmffc extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("id", s);

//now call rsm dsm ,pso as per s value

//rsm call
            CallRSmList callRSmList = new CallRSmList(getActivity());
            callRSmList.execute("http://renata-vision.xyz/rx_new/get_rsmlist.php?id=" + s);

//dsm call

            CallDSmList callDSmList = new CallDSmList(getActivity());
            callDSmList.execute("http://renata-vision.xyz/rx_new/get_dsmlist.php?id=" + s);

            //Pso Call

            CallPsoList callPsoList = new CallPsoList(getActivity());
            callPsoList.execute("http://renata-vision.xyz/rx_new/get_psolist.php?id=" + s);


            //get Product
            //http://renata-vision.xyz/rx_new/get_product.php

            CallProductList callProductList = new CallProductList(getActivity());
            callProductList.execute("http://renata-vision.xyz/rx_new/get_product.php");

            //get all team

            //http://renata-vision.xyz/rx_new/get_team.php
            CallTeamList callTeamList = new CallTeamList(getActivity());
            callTeamList.execute("http://renata-vision.xyz/rx_new/get_team.php");

            //get all Region

            CallRegionList callRegionList = new CallRegionList(getActivity());
            callRegionList.execute("http://renata-vision.xyz/rx_new/get_region.php");
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallSmffc(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();
                JSONObject jsonObject = new JSONObject(mainfile);
                String ffc = jsonObject.getString("id");


                return ffc;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallRSmList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, rsmList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            rsmSpinner.setAdapter(spinnerArrayAdapter);
            rsmSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallRSmList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    rsmList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallDSmList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dsmList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            dsmSpinner.setAdapter(spinnerArrayAdapter);
            dsmSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallDSmList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    dsmList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallPsoList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            @SuppressLint({"NewApi", "LocalSuppress"}) ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, psoList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            psoSpinner.setAdapter(spinnerArrayAdapter);
            psoSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallPsoList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    psoList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallProductList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, productList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            productSpinner.setAdapter(spinnerArrayAdapter);
            productSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallProductList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    productList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallTeamList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, teamList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            teamSpinner.setAdapter(spinnerArrayAdapter);
            teamSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallTeamList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    teamList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallRegionList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, regionList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            regionSpinner.setAdapter(spinnerArrayAdapter);
            regionSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallRegionList(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String name = child.getString("name");

                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    regionList.add(name);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public class CallRxData extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("size", String.valueOf(RxList.size()));
            createExcelSheet();
            progressDialog.dismiss();
//we will call the excel File From Here After getting all the Data
        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallRxData(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String link = strings[0];

            try {
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = " ";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);


                }

                mainfile = stringBuffer.toString();


                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    String id = child.getString("id");
                    String rx_no = child.getString("rx_no");
                    String doctor_name = child.getString("doctor_name");
                    String doctor_code = child.getString("doctor_code");
                    String sm = child.getString("sm");
                    String sm_ffc = child.getString("sm_ffc");
                    String rsm = child.getString("rsm");
                    String rsm_ffc = child.getString("rsm_ffc");
                    String dsm = child.getString("dsm");
                    String dsm_ffc = child.getString("dsm_ffc");
                    String pso = child.getString("pso");
                    String pso_ffc = child.getString("pso_ffc");
                    String region = child.getString("region");
                    String head_quarter = child.getString("head_quarter");
                    String depot = child.getString("depot");
                    String product_1 = child.getString("product_1");
                    String product_2 = child.getString("product_2");
                    String product_3 = child.getString("product_3");
                    String product_4 = child.getString("product_4");
                    String product_5 = child.getString("product_5");
                    String team = child.getString("team");
                    Log.d("name", id);
                    // arrayList.add(new JsonDataList(name));
                    RxList.add(new RxDataList(id, rx_no, doctor_name, doctor_code, sm, sm_ffc, rsm, rsm_ffc, dsm, dsm_ffc, pso, pso_ffc, region, head_quarter, depot, product_1, product_2, product_3, product_4, product_5, team));
                    sampleRxDataList.add(id);
                    i++;
                }


                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }


    public void createExcelSheet() {
        if (isStoragePermissionGranted()) {
            String folder_main = "Vision Top Management";
            String csvFile = "rx_data.xls";
            File f = new File(Environment.getExternalStorageDirectory(), folder_main);
            // directory = new File(sd.getAbsolutePath());
            if (!f.exists()) {
                f.mkdirs();
            }
            file = new File(f, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
                createFirstSheet();
                //closing cursor
                workbook.write();
                workbook.close();
                //clear the list
                Toast.makeText(getActivity(), "File Creating Please Check After 5 min !", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(getActivity(), "Permission Denied !", Toast.LENGTH_SHORT).show();
        }
    }

    public void createFirstSheet() {
        try {

            //Excel sheet name. 0 (number)represents first sheet
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            // column and row title
            sheet.addCell(new Label(0, 0, "Id"));
            sheet.addCell(new Label(1, 0, "rx_no"));
            sheet.addCell(new Label(2, 0, "doctor_name"));
            sheet.addCell(new Label(3, 0, "doctor_code"));
            sheet.addCell(new Label(4, 0, "sm"));
            sheet.addCell(new Label(5, 0, "sm_ffc"));
            sheet.addCell(new Label(6, 0, "rsm"));
            sheet.addCell(new Label(7, 0, "rsm_ffc"));
            sheet.addCell(new Label(8, 0, "dsm"));
            sheet.addCell(new Label(9, 0, "dsm_ffc"));
            sheet.addCell(new Label(10, 0, "pso"));
            sheet.addCell(new Label(12, 0, "pso_ffc"));
            sheet.addCell(new Label(11, 0, "region"));
            sheet.addCell(new Label(13, 0, "Head_Quarter"));
            sheet.addCell(new Label(14, 0, "Depo"));
            sheet.addCell(new Label(15, 0, "product_1"));
            sheet.addCell(new Label(16, 0, "product_2"));
            sheet.addCell(new Label(17, 0, "product_3"));
            sheet.addCell(new Label(18, 0, "product_4"));
            sheet.addCell(new Label(19, 0, "product_5"));
            sheet.addCell(new Label(20, 0, "team"));


            for (int i = 0; i < RxList.size(); i++) {
                sheet.addCell(new Label(0, i + 1, RxList.get(i).getId()));
                sheet.addCell(new Label(1, i + 1, RxList.get(i).getRx_no()));
                sheet.addCell(new Label(2, i + 1, RxList.get(i).getDoctor_name()));
                sheet.addCell(new Label(3, i + 1, RxList.get(i).getDoctor_code()));
                sheet.addCell(new Label(4, i + 1, RxList.get(i).getSm()));
                sheet.addCell(new Label(5, i + 1, RxList.get(i).getSm_ffc()));
                sheet.addCell(new Label(6, i + 1, RxList.get(i).getRsm()));
                sheet.addCell(new Label(7, i + 1, RxList.get(i).getRsm_ffc()));
                sheet.addCell(new Label(8, i + 1, RxList.get(i).getDsm()));
                sheet.addCell(new Label(9, i + 1, RxList.get(i).getDsm_ffc()));
                sheet.addCell(new Label(10, i + 1, RxList.get(i).getPso()));
                sheet.addCell(new Label(11, i + 1, RxList.get(i).getPso_ffc()));
                sheet.addCell(new Label(12, i + 1, RxList.get(i).getRegion()));
                sheet.addCell(new Label(13, i + 1, RxList.get(i).getHead_quarter()));
                sheet.addCell(new Label(14, i + 1, RxList.get(i).getDepot()));
                sheet.addCell(new Label(15, i + 1, RxList.get(i).getProduct_1()));
                sheet.addCell(new Label(16, i + 1, RxList.get(i).getProduct_2()));
                sheet.addCell(new Label(17, i + 1, RxList.get(i).getProduct_3()));
                sheet.addCell(new Label(18, i + 1, RxList.get(i).getProduct_4()));
                sheet.addCell(new Label(19, i + 1, RxList.get(i).getProduct_5()));
                sheet.addCell(new Label(20, i + 1, RxList.get(i).getTeam()));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

}

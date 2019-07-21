package com.rx.rxpanel.SM;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Region;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rx.rxpanel.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class SmAddRsmFragment extends Fragment {
    private Spinner spinner, regionSpinner,rsmSpinner;
    private ArrayList<String> regionList;
    SharedPreferences pref;
    private EditText et_smname, et_userid, et_password, et_name, et_email, et_ffc, et_pin;
    private Button button;
    private ProgressDialog progressDialog;
    private String uname, uemail, upass, usmname, uffc, upin, ureg, uid, udes, smffc;
    private SmHomeFragment smHomeFragment;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sm_add_rsm, container, false);
        spinner = view.findViewById(R.id.sp_designation);
        regionSpinner = view.findViewById(R.id.region_spinner);
        //edit text ---------------------------
        et_smname = view.findViewById(R.id.sm_name);
        et_userid = view.findViewById(R.id.et_userid);
        et_password = view.findViewById(R.id.et_password);
        et_name = view.findViewById(R.id.et_name);
        et_email = view.findViewById(R.id.et_email);
        et_ffc = view.findViewById(R.id.et_ffc);
        et_pin = view.findViewById(R.id.et_pin);
        rsmSpinner = view.findViewById(R.id.rsm_spinner);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Submitting ....");
        smHomeFragment = new SmHomeFragment();
        //edit text close -------------------------
        regionList = new ArrayList<String>();
        regionList.add(0, "Select");
        button = view.findViewById(R.id.submit);
        SetSpinner();

        CallRegionList callRegionList = new CallRegionList(getActivity());
        callRegionList.execute("http://renata-vision.xyz/rx_new/get_region.php");
        CallRsmUid callRsmUid = new CallRsmUid(getActivity());
        callRsmUid.execute("http://renata-vision.xyz/rx_new/generate_rsm_uid.php");
        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("designation", String.valueOf(adapterView.getItemAtPosition(i)));
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


        String name = pref.getString("smname", "");
        et_smname.setText(name);
        et_password.setText("RS-1234");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                //  uname,uemail,upass,usmname,uffc,upin,ureg,uid,udes;

                uname = et_name.getText().toString();
                uemail = et_email.getText().toString();
                upass = et_password.getText().toString();
                usmname = et_smname.getText().toString();
                uffc = et_ffc.getText().toString();
                uid = et_userid.getText().toString();
                upin = et_pin.getText().toString();
                ureg = pref.getString("region", "");
                udes = pref.getString("designation", "");
                smffc = pref.getString("smffc", "");
                Log.d("check", smffc);

                //now we will call the post value to insert into table

                SubmitRsm submitRsm = new SubmitRsm(getActivity());
                submitRsm.execute("http://renata-vision.xyz/rx_new/sm_add_rsm.php?name="+uname+"&email="+uemail+"&pass="+upass+"&sm="+usmname+"&smffc="+smffc+"&ffc="+uffc+"&uid="+uid+"&pin="+upin+"&ureg="+ureg+"&role="+udes);


            }
        });
        return view;
    }

    private void SetSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.rsm_spinner));
        spinner.setAdapter(spinnerAdapter);
    }


    public class CallRegionList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            @SuppressLint({"NewApi", "LocalSuppress"}) ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, regionList);
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

    public class CallRsmUid extends AsyncTask<String, String, String> {
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

        public CallRsmUid(Context context) {
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
                String id = jsonObject.getString("id");
                et_userid.setText(id);
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

    public class SubmitRsm extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public SubmitRsm(Context context) {
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
                String id = jsonObject.getString("id");
                if (id.equals("saved")) {
                    progressDialog.dismiss();
                    SetFragment(smHomeFragment);

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Information Saved ! ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Problem ! ", Toast.LENGTH_SHORT).show();
                        }
                    });

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




    private void SetFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("my_fragment").commit();


    }

}

package com.rx.rxpanel.RSM.PSO;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rx.rxpanel.DSM.DsmHomeFragment;
import com.rx.rxpanel.DSM.PSO.DsmEditPsoFragment;
import com.rx.rxpanel.R;
import com.rx.rxpanel.RSM.RsmHomeFragment;

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


public class RsmEditPsoFragment extends Fragment {
    private Spinner spinner, regionSpinner;
    private Button button;
    private ArrayList<String> ffcList;
    SharedPreferences pref;
    private ProgressDialog progressDialog;
    private RsmHomeFragment smHomeFragment;
    private LinearLayout after, before;
    private EditText et_name, et_email, et_designation, sm_name, et_password, et_pin, et_region, et_mobile;
    private Button submit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_rsm_edit_pso, container, false);



        regionSpinner = view.findViewById(R.id.region_spinner);
        button = view.findViewById(R.id.bt_delete);
        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        ffcList = new ArrayList<String>();
        ffcList.add(0, "Select");
        smHomeFragment = new RsmHomeFragment();
        CallDsmFfcList callDsmFfcList = new CallDsmFfcList(getActivity());
        callDsmFfcList.execute("http://renata-vision.xyz/rx_new/get_all_pso_ffc.php");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ......");
        after = view.findViewById(R.id.after_layout);
        before = view.findViewById(R.id.before_layout);
        et_name = view.findViewById(R.id.et_name);
        et_email = view.findViewById(R.id.et_email);
        et_designation = view.findViewById(R.id.et_designation);
        et_password = view.findViewById(R.id.et_password);
        et_pin = view.findViewById(R.id.et_pin);
        et_region = view.findViewById(R.id.et_region);
        et_mobile = view.findViewById(R.id.et_mobile);
        sm_name = view.findViewById(R.id.sm_name);
        submit = view.findViewById(R.id.save_info);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String ffc = pref.getString("rsmffc", ""); //now its pso
                String uname= et_name.getText().toString();
                String udesignation= et_designation.getText().toString();
                String uemail= et_email.getText().toString();
                String umobile= et_mobile.getText().toString();
                String upassword= et_password.getText().toString();
                String upin= et_pin.getText().toString();
                String uregion= et_region.getText().toString();
                String usmname= sm_name.getText().toString();
                UpdateRsm updateRsm = new UpdateRsm(getActivity());
                updateRsm.execute("http://renata-vision.xyz/rx_new/edit_pso_profile.php?id="+ffc+"&name="+uname+"&email="+uemail+"&role="+udesignation+"&smname="+usmname+"&pass="+upassword+"&pin="+upin+"&region="+uregion+"&mobile="+umobile);



            }
        });







        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                SharedPreferences.Editor editor = pref.edit();
                editor.putString("rsmffc", String.valueOf(adapterView.getItemAtPosition(i)));
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

                String ffc = pref.getString("rsmffc", "");
                //call for edit show the new layout and remove this two button
                after.setVisibility(View.GONE);
                before.setVisibility(View.VISIBLE);
                //get data and set into edit text
                GetRsmViaFfc getRsmViaFfc = new GetRsmViaFfc(getActivity());
                getRsmViaFfc.execute("http://renata-vision.xyz/rx_new/get_pso_via_ffc.php?id=" + ffc);
            }
        });













        return view;
    }
    public class CallDsmFfcList extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            @SuppressLint({"NewApi", "LocalSuppress"}) ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, ffcList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            regionSpinner.setAdapter(spinnerArrayAdapter);
            regionSpinner.setSelection(0);
            spinnerArrayAdapter.notifyDataSetChanged();

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public CallDsmFfcList(Context context) {
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
                    String name = child.getString("ffc");
                    Log.d("name", name);
                    // arrayList.add(new JsonDataList(name));
                    ffcList.add(name);
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

    public class GetRsmViaFfc extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public GetRsmViaFfc(Context context) {
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
                    final String name = child.getString("name");
                    final String email = child.getString("email");
                    final String mobile = child.getString("mobile_no");
                    final String role = child.getString("role");
                    final String sm = child.getString("sm");
                    final String pass = child.getString("password");
                    final String pin = child.getString("pin_code");
                    final String region = child.getString("region");

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Now You Can Edit this Information ! ", Toast.LENGTH_SHORT).show();
                            et_name.setText(name);
                            et_designation.setText(role);
                            et_email.setText(email);
                            et_mobile.setText(mobile);
                            et_password.setText(pass);
                            et_pin.setText(pin);
                            et_region.setText(region);
                            sm_name.setText(sm);
                            progressDialog.dismiss();

                        }
                    });

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

    public class UpdateRsm extends AsyncTask<String, String, String> {


        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public UpdateRsm(Context context) {
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

                String value = jsonObject.getString("id");
                if (value.equals("updated")) {
                    progressDialog.dismiss();
                    SetFragment(smHomeFragment);

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Information Updated ! ", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    progressDialog.dismiss();
                    SetFragment(smHomeFragment);

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Try Again ! ! ", Toast.LENGTH_SHORT).show();
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
        @SuppressLint({"NewApi", "LocalSuppress"}) FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("my_fragment").commit();


    }

}

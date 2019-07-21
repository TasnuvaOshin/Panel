package com.rx.rxpanel.DSM.PSO;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.rx.rxpanel.DSM.DsmHomeFragment;
import com.rx.rxpanel.R;
import com.rx.rxpanel.RSM.PSO.RsmRemovePsoFragment;
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

public class DsmRemovePsoFragment extends Fragment {
    private Spinner spinner, regionSpinner;
    private Button button;
    private ArrayList<String> ffcList;
    SharedPreferences pref;
    private ProgressDialog progressDialog;
    private DsmHomeFragment smHomeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_dsm_remove_pso, container, false);



        regionSpinner = view.findViewById(R.id.region_spinner);
        button = view.findViewById(R.id.bt_delete);
        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        ffcList = new ArrayList<String>();
        ffcList.add(0, "Select");
        smHomeFragment = new DsmHomeFragment();
        CallDsmFfcList callDsmFfcList = new CallDsmFfcList(getActivity());
        callDsmFfcList.execute("http://renata-vision.xyz/rx_new/get_all_pso_ffc.php");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ......");
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
                //call delete

//http://renata-vision.xyz/rx_new/remove_rsm.php?id=111

                RemoveRsm removeRsm = new RemoveRsm(getActivity());
                removeRsm.execute("http://renata-vision.xyz/rx_new/remove_pso.php?id="+ ffc);
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

    public class RemoveRsm extends AsyncTask<String, String, String> {


        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public RemoveRsm(Context context) {
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
                if (value.equals("removed")) {
                    progressDialog.dismiss();
                    SetFragment(smHomeFragment);

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Information Deleted ! ", Toast.LENGTH_SHORT).show();
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

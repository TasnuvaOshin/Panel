package com.rx.rxpanel.PSO;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rx.rxpanel.R;
import com.rx.rxpanel.SM.SmProfileFragment;

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


public class PsoProfileFragment extends Fragment {
    SharedPreferences pref;
    private TextView userName, userId,userPassword,userDesignation,userEmail,userPincode,userFfc,userMobileno;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
  View view  = inflater.inflate(R.layout.fragment_pso_profile, container, false);

        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        String name = pref.getString("key_name", "");


        userName = view.findViewById(R.id.tv_username);
        userEmail = view.findViewById(R.id.tv_useremail);
        userDesignation = view.findViewById(R.id.tv_designation);
        userFfc = view.findViewById(R.id.tv_userffc);
        userPassword  = view.findViewById(R.id.tv_userpassword);
        userMobileno = view.findViewById(R.id.tv_usermobilenumber);
        userPincode = view.findViewById(R.id.tv_userepincode);
        userId = view.findViewById(R.id.tv_userid);
        userId.setText(name);



        CallSmffc callSmffc = new CallSmffc(getActivity());
        callSmffc.execute("http://renata-vision.xyz/rx_new/get_psoprofile.php?id="+name);





        return view;
    }


    public class CallSmffc extends AsyncTask<String, String, String> {

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
                JSONArray parent = new JSONArray(mainfile);
                int i = 0;
                while (i <= parent.length()) {

                    JSONObject child = parent.getJSONObject(i);

                    name = child.getString("name");
                    userName.setText(name);

                    String email = child.getString("email");
                    String mobile_no = child.getString("mobile_no");
                    String role = child.getString("role");
                    String password = child.getString("password");
                    String pin = child.getString("pin_code");
                    String ffc = child.getString("ffc");
                    userDesignation.setText(role);
                    userPincode.setText(pin);
                    userPassword.setText(password);
                    userMobileno.setText(mobile_no);
                    userFfc.setText(ffc);
                    userEmail.setText(email);

                    Log.d("test", name);
                    // arrayList.add(new JsonDataList(name));

                    i++;
                }






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
}

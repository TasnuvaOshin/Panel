package com.rx.rxpanel.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rx.rxpanel.DSM.DsmFragment;
import com.rx.rxpanel.DSM.DsmHomeFragment;
import com.rx.rxpanel.MainActivity;
import com.rx.rxpanel.PSO.PsoFragment;
import com.rx.rxpanel.PSO.PsoHomeFragment;
import com.rx.rxpanel.R;
import com.rx.rxpanel.RSM.RsmFragment;
import com.rx.rxpanel.RSM.RsmHomeFragment;
import com.rx.rxpanel.SM.SmFragment;
import com.rx.rxpanel.SM.SmHomeFragment;

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

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {


    private AutoCompleteTextView et_userId;
    private EditText et_password;
    private Button button;
    private String userId, userPass;
    private SmFragment smFragment;
    private RsmFragment rsmFragment;
    private DsmFragment dsmFragment;
    private PsoFragment psoFragment;
    private SmHomeFragment smHomeFragment;
    private DsmHomeFragment dsmHomeFragment;
    public ProgressDialog progressDialog;
    private RsmHomeFragment rsmHomeFragment;
    private PsoHomeFragment psoHomeFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        et_userId = view.findViewById(R.id.edit_user_id);
        et_password = view.findViewById(R.id.edit_pass);
        button = view.findViewById(R.id.btn_sign_in);
        smFragment = new SmFragment();
        rsmFragment = new RsmFragment();
        dsmFragment = new DsmFragment();
        psoFragment = new PsoFragment();
        smHomeFragment = new SmHomeFragment();
        rsmHomeFragment = new RsmHomeFragment();
        dsmHomeFragment = new DsmHomeFragment();
        psoHomeFragment = new PsoHomeFragment();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                userId = et_userId.getText().toString();
                userPass = et_password.getText().toString();
                if (TextUtils.isEmpty(userId)) {

                    Toast.makeText(getActivity(), "Please Enter your User Id Correctly", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                if (TextUtils.isEmpty(userPass)) {

                    Toast.makeText(getActivity(), "Please Enter your Password  Correctly", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Log.d("user", userId + " ---this is the log---  " + userPass);
                    AuthCall authCall = new AuthCall(getActivity());
                    authCall.execute("http://renata-vision.xyz/rx_new/auth.php?id=" + userId + "&pass=" + userPass);


                }
            }
        });


        return view;
    }


    public class AuthCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public AuthCall(Context context) {
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

                try {
                    JSONObject jsonObject = new JSONObject(mainfile);
                    String role = String.valueOf(jsonObject.get("role"));
                    Log.d("role", role);
                    if (role.equals("SM")) {
                        SetFragment(smHomeFragment);
                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("key_name", et_userId.getText().toString());
                        editor.apply();
                        progressDialog.dismiss();
                    }
                    if (role.equals("RSM")) {
                        SetFragment(rsmHomeFragment);
                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("key_name", et_userId.getText().toString());
                        editor.apply();
                        progressDialog.dismiss();

                    }
                    if (role.equals("DSM")) {
                        SetFragment(dsmHomeFragment);
                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("key_name", et_userId.getText().toString());
                        editor.apply();
                        progressDialog.dismiss();
                    }
                    if (role.equals("PSO")) {
                        SetFragment(psoHomeFragment);
                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("key_name", et_userId.getText().toString());
                        editor.apply();
                        progressDialog.dismiss();

                    }
                    if (role.equals("invalid")) {
                        progressDialog.dismiss();
                        ShowErrorMsg();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mainfile;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    private void ShowErrorMsg() {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), "UserId or Password is not Correct", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void SetFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("my_fragment").commit();


    }


}

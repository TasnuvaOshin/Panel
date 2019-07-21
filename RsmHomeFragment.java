package com.rx.rxpanel.RSM;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.rx.rxpanel.Authentication.LoginFragment;
import com.rx.rxpanel.R;
import com.rx.rxpanel.RSM.DSM.RsmAddDsmFragment;
import com.rx.rxpanel.RSM.DSM.RsmEditDsmFragment;
import com.rx.rxpanel.RSM.DSM.RsmRemoveDsmFragment;
import com.rx.rxpanel.RSM.PSO.RsmAddPsoFragment;
import com.rx.rxpanel.RSM.PSO.RsmEditPsoFragment;
import com.rx.rxpanel.RSM.PSO.RsmRemovePsoFragment;
import com.rx.rxpanel.SM.SmFragment;
import com.rx.rxpanel.SM.SmHomeFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


public class RsmHomeFragment extends Fragment {
    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView lmd, lsd, mtd;
    private LoginFragment loginFragment;
    private RsmFragment smFragment;
    SharedPreferences pref;
    private ImageButton imageButton;
    private TextView tv_name;
    private ImageView iv_img;
    private String img;
    private RsmProfileFragment rsmProfileFragment;
    private RsmAddDsmFragment rsmAddDsmFragment;
    private RsmRemoveDsmFragment rsmRemoveDsmFragment;
    private RsmEditDsmFragment rsmEditDsmFragment;
    private RsmAddPsoFragment rsmAddPsoFragment;
    private RsmRemovePsoFragment rsmRemovePsoFragment;
    private RsmEditPsoFragment rsmEditPsoFragment;
    private RsmHomeFragment rsmHomeFragment;
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rsm_home, container, false);


        drawerLayout = view.findViewById(R.id.drawerlayout);
        navigationView = view.findViewById(R.id.navigationview);
        tv_name = view.findViewById(R.id.tv_name);
        iv_img = view.findViewById(R.id.iv_img);
        loginFragment = new LoginFragment();
        smFragment = new RsmFragment();
        rsmProfileFragment = new RsmProfileFragment();
        rsmAddDsmFragment = new RsmAddDsmFragment();
        rsmRemoveDsmFragment = new RsmRemoveDsmFragment();
        rsmEditDsmFragment = new RsmEditDsmFragment();
        rsmAddPsoFragment = new RsmAddPsoFragment();
        rsmRemovePsoFragment = new RsmRemovePsoFragment();
        rsmEditPsoFragment = new RsmEditPsoFragment();
        rsmHomeFragment = new RsmHomeFragment();
        SetUpSideDrawer();
        SetupMenu();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("No Internet Connection !");
        lmd = view.findViewById(R.id.lmd);
        lsd = view.findViewById(R.id.lsd);
        mtd = view.findViewById(R.id.mtd);
        imageButton = view.findViewById(R.id.bt_menu);
        progressDialog.show();
        pref = Objects.requireNonNull(getActivity()).getApplicationContext().getSharedPreferences("MyPref", 0);
        String name = pref.getString("key_name", "");
        if (isNetworkStatusAvialable(getActivity().getApplicationContext())) {

            CallSmffc callSmffc = new CallSmffc(getActivity());
            callSmffc.execute("http://renata-vision.xyz/rx_new/get_rsmffc.php?id=" + name);
            progressDialog.dismiss();
        } else {


            progressDialog.show();
        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(Gravity.START);
                else drawerLayout.closeDrawer(Gravity.END);
            }

        });
        hideItem();

        return view;
    }

    private void hideItem() {

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.rsm).setVisible(false);
    }

    private void SetupMenu() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {


                    case R.id.logout:
                        SetFragment(loginFragment);
                        break;

                    case R.id.rx_report:
                        SetFragment(smFragment);
                        break;

                    case R.id.rx_summary_report:
                        SetFragment(smFragment);
                        break;

                    case R.id.profile:
                        SetFragment(rsmProfileFragment);
                        break;


                    case R.id.add_dsm:
                        SetFragment(rsmAddDsmFragment);
                        break;

                    case R.id.remove_dsm:
                        SetFragment(rsmRemoveDsmFragment);
                           break;

                    case R.id.edit_dsm:
                        SetFragment(rsmEditDsmFragment);
                        break;

                    case R.id.add_pso:
                        SetFragment(rsmAddPsoFragment);
                        break;

                    case R.id.remove_pso:
                        SetFragment(rsmRemovePsoFragment);
                        break;

                    case R.id.edit_pso:
                        SetFragment(rsmEditPsoFragment);
                        break;

                    case R.id.home:
                        SetFragment(rsmHomeFragment);
                        break;
                }

                return false;
            }
        });
    }


    private void SetUpSideDrawer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mToolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(mToolbar);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //check internet connection
    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null) {
                return netInfos.isConnected();
            }
        }
        return false;
    }

    private void SetFragment(Fragment fragment) {
        @SuppressLint({"NewApi", "LocalSuppress"}) FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("my_fragment").commit();


    }


    public class CallSmffc extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("id", s);
            LastMonthRx lastMonthRx = new LastMonthRx(getActivity());
            lastMonthRx.execute("http://renata-vision.xyz/rx_new/lsd_rsm.php?id=" + s);
            LastDayRx lastDayRx = new LastDayRx(getActivity());
            lastDayRx.execute("http://renata-vision.xyz/rx_new/ldrsm.php?id=" + s);
            MtdRx mtdRx = new MtdRx(getActivity());
            mtdRx.execute("http://renata-vision.xyz/rx_new/mtd_rsm.php?id=" + s);
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
                String name = jsonObject.getString("name");
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("rsmname",name);
                editor.putString("rsmffc",ffc);
                editor.apply();
                img = jsonObject.getString("img");
                tv_name.setText(name);
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load("http://renata-vision.xyz/profile_photo/" + img)
                                .into(iv_img);
                    }
                });


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


    public class LastMonthRx extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public LastMonthRx(Context context) {
            this.context = context;
        }

        @SuppressLint("SetTextI18n")
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


                //  mainfile = stringBuffer.toString();
                Log.d("lmd", mainfile);

                JSONObject jsonObject = new JSONObject(mainfile);
                String count = jsonObject.getString("id");
                lmd.setText("Last Month Rx Submitted : " + count);
                // JSONArray parent = new JSONArray(mainfile);

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

    public class LastDayRx extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public LastDayRx(Context context) {
            this.context = context;
        }

        @SuppressLint("SetTextI18n")
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


                mainfile = stringBuffer.toString();
                Log.d("lmd", mainfile);

                JSONObject jsonObject = new JSONObject(mainfile);
                String count = jsonObject.getString("id");
                lsd.setText("Last Day Rx Submitted : " + count);
                // JSONArray parent = new JSONArray(mainfile);

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

    public class MtdRx extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection = null;
        String mainfile;
        BufferedReader bufferedReader = null;
        public Context context;

        public MtdRx(Context context) {
            this.context = context;
        }

        @SuppressLint("SetTextI18n")
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


                mainfile = stringBuffer.toString();
                Log.d("lmd", mainfile);

                JSONObject jsonObject = new JSONObject(mainfile);
                String count = jsonObject.getString("id");
                mtd.setText("Mtd Rx Submitted : " + count);
                // JSONArray parent = new JSONArray(mainfile);

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

}

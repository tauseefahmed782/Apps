package com.cscodetech.pocketporter.fregment;

import static com.cscodetech.pocketporter.utility.SessionManager.currency;
import static com.cscodetech.pocketporter.utility.SessionManager.language;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscodetech.pocketporter.R;
import com.cscodetech.pocketporter.activity.HelpDetailsActivity;
import com.cscodetech.pocketporter.activity.HomeActivity;
import com.cscodetech.pocketporter.activity.IntroActivity;
import com.cscodetech.pocketporter.model.Pages;
import com.cscodetech.pocketporter.model.Refercode;
import com.cscodetech.pocketporter.model.User;
import com.cscodetech.pocketporter.retrofit.APIClient;
import com.cscodetech.pocketporter.retrofit.GetResult;
import com.cscodetech.pocketporter.utility.CustPrograssbar;
import com.cscodetech.pocketporter.utility.SessionManager;
import com.google.android.datatransport.BuildConfig;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class AccountFragment extends Fragment implements GetResult.MyListener {
    SessionManager sessionManager;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_edit)
    TextView txtEdit;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.txt_redercode)
    TextView txtRedercode;
    User user;
    @BindView(R.id.txt_share)
    TextView txtShare;
    @BindView(R.id.txt_call)
    TextView txtCall;
    @BindView(R.id.txt_wallet)
    TextView txtWallet;
    @BindView(R.id.txt_singuprefer)
    TextView txtSinguprefer;
    @BindView(R.id.recycler_menu)
    RecyclerView recyclerMenu;
    CustPrograssbar custPrograssbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 201);
        }
        sessionManager = new SessionManager(getActivity());
        custPrograssbar = new CustPrograssbar();
        user = sessionManager.getUserDetails();
        txtName.setText("" + user.getFname());
        txtPhone.setText("" + user.getMobile());
        txtWallet.setText(sessionManager.getStringData(currency)+ user.getWallet());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMenu.setLayoutManager(mLayoutManager2);
        recyclerMenu.setItemAnimator(new DefaultItemAnimator());
        getData();
        return view;
    }

    private void getData() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(jsonObject.toString(),MediaType.parse(getString(R.string.application_json)));
        Call<JsonObject> call = APIClient.getInterface().getdata(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
    }

    private void editProfile(JSONObject jsonObject) {
        custPrograssbar.prograssCreate(getActivity());

        RequestBody bodyRequest = RequestBody.create(jsonObject.toString(),MediaType.parse(getString(R.string.application_json)));
        Call<JsonObject> call = APIClient.getInterface().walletReport(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
    }

    public void bottonConfirm() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.custome_edit, null);
        mBottomSheetDialog.setContentView(sheetView);
        EditText edName = sheetView.findViewById(R.id.ed_name);
        EditText edEmail = sheetView.findViewById(R.id.ed_email);
        TextView edmobile = sheetView.findViewById(R.id.ed_mobile);
        EditText edPassword = sheetView.findViewById(R.id.ed_password);
        edName.setText("" + user.getFname());
        edEmail.setText("" + user.getEmail());
        edmobile.setText("" + user.getMobile());
        edPassword.setText("" + user.getPassword());
        TextView txtChoose = sheetView.findViewById(R.id.btn_send);
        txtChoose.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edName.getText().toString())
                    && !TextUtils.isEmpty(edEmail.getText().toString())
                    && !TextUtils.isEmpty(edmobile.getText().toString())
                    && !TextUtils.isEmpty(edPassword.getText().toString())) {
                mBottomSheetDialog.cancel();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", user.getId());
                    jsonObject.put("name", edName.getText().toString());
                    jsonObject.put("email", edEmail.getText().toString());
                    jsonObject.put("password", edPassword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editProfile(jsonObject);
            }
        });
        mBottomSheetDialog.show();
    }

    @OnClick({R.id.txt_edit, R.id.txt_launge, R.id.txt_call, R.id.txt_share, R.id.txt_wallet, R.id.txt_logout})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.txt_edit:
                bottonConfirm();
                break;
            case R.id.txt_launge:
                bottonLanguageList();
                break;
            case R.id.txt_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + refercode.getMobile()));
                startActivity(intent);
                break;
            case R.id.txt_share:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String shareMessage = "Hey! Now use our app to share with your family or friends. User will get wallet amount on your 1st successful trip. Enter my referral code *" + refercode.getCode() + "* & Enjoy your trip !!!";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    Log.e("error", Objects.requireNonNull(e.getMessage()));
                }
                break;
            case R.id.txt_wallet:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new WalletFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.txt_logout:
                sessionManager.logoutUser();
                startActivity(new Intent(getActivity(), IntroActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void bottonLanguageList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.custome_launguage, null);
        LinearLayout lvlenglish = sheetView.findViewById(R.id.lvl_english);
        LinearLayout lvlGujrati = sheetView.findViewById(R.id.lvl_gujrati);
        LinearLayout lvlarb = sheetView.findViewById(R.id.lvl_arb);
        LinearLayout lvlhind = sheetView.findViewById(R.id.lvl_hind);
        LinearLayout lvlIndonesiya = sheetView.findViewById(R.id.lvl_indonesiya);
        LinearLayout lvlBangali = sheetView.findViewById(R.id.lvl_bangali);
        LinearLayout lvlAfrikan = sheetView.findViewById(R.id.lvl_afrikan);

        lvlIndonesiya.setOnClickListener(v -> {

            sessionManager.setStringData(language, "in");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });

        lvlBangali.setOnClickListener(v -> {

            sessionManager.setStringData(language, "bn");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });

        lvlAfrikan.setOnClickListener(v -> {

            sessionManager.setStringData(language, "af");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });

        lvlenglish.setOnClickListener(v -> {

            sessionManager.setStringData(language, "en");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });

        lvlGujrati.setOnClickListener(v -> {

            sessionManager.setStringData(language, "es");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();


        });

        lvlarb.setOnClickListener(v -> {

            sessionManager.setStringData(language, "ar");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();


        });

        lvlhind.setOnClickListener(v -> {

            sessionManager.setStringData(language, "hi");
            startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();


        });


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }


    Refercode refercode;

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                refercode = gson.fromJson(result.toString(), Refercode.class);
                if (refercode != null) {
                    String sourceString = getString(R.string.youget) +" "+ sessionManager.getStringData(currency) + refercode.getRefercredit() + getString(R.string.when_your_frined) + sessionManager.getStringData(currency) + refercode.getSignupcredit() + "</b> when he or she sign up on app";
                    txtSinguprefer.setText(Html.fromHtml(sourceString));
                    txtEmail.setText(getString(R.string.mainlusat)+" " + refercode.getEmail());
                    txtRedercode.setText("" + refercode.getCode());
                    recyclerMenu.setAdapter(new MyFaqAdepter(refercode.getPagelist()));
                }
            }
        } catch (Exception e) {
            Log.e("Error", "-" + e.getMessage());
        }
    }

    public class MyFaqAdepter extends RecyclerView.Adapter<MyFaqAdepter.ViewHolder> {
        private List<Pages> orderData;

        public MyFaqAdepter(List<Pages> orderData) {
            this.orderData = orderData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.halp_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Log.e("position", "" + position);
            Pages order = orderData.get(position);
            holder.txtTital.setText("" + order.getTitle());

            holder.lvlClick.setOnClickListener(v -> startActivity(new Intent(getActivity(), HelpDetailsActivity.class).putExtra("title", order.getTitle()).putExtra("desc", order.getDescription())));
        }

        @Override
        public int getItemCount() {
            return orderData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_tital)
            TextView txtTital;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
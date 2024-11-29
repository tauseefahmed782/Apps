package com.pocketporter.partner.frgment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.adapter.LogisticAdapter;
import com.pocketporter.partner.model.Logistic;
import com.pocketporter.partner.model.LogisticHistory;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.ui.LogisticDetailsActivity;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ParcelFragment extends Fragment implements LogisticAdapter.RecyclerTouchListener, GetResult.MyListener {

    @BindView(R.id.txt_neworder)
    TextView txtNeworder;
    @BindView(R.id.txt_ongoing)
    TextView txtOngoing;
    @BindView(R.id.txt_complet)
    TextView txtComplet;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.recycleview_trip)
    RecyclerView recycleviewLogistic;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_logistic, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        recycleviewLogistic.setLayoutManager(mLayoutManager2);
        recycleviewLogistic.setItemAnimator(new DefaultItemAnimator());
        recycleviewLogistic.setAdapter(new LogisticAdapter(getActivity(), new ArrayList<>(), this));
        getOrders("New");
        return view;

    }

    private void getOrders(String status) {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", user.getId());
            jsonObject.put("dzone", user.getDzone());
            jsonObject.put("vid", user.getVehiid());
            jsonObject.put("status", status);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(jsonObject.toString(),MediaType.parse(getString(R.string.application_json)));
        Call<JsonObject> call = APIClient.getInterface().parcelHistory(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Logistic logistic = gson.fromJson(result.toString(), Logistic.class);
                if (logistic.getResult().equalsIgnoreCase("true")) {
                    if (logistic.getLogisticHistory().size() != 0) {
                        lvlNotfound.setVisibility(View.GONE);
                        recycleviewLogistic.setVisibility(View.VISIBLE);
                        recycleviewLogistic.setAdapter(new LogisticAdapter(getActivity(), logistic.getLogisticHistory(), this));
                    } else {
                        lvlNotfound.setVisibility(View.VISIBLE);
                        recycleviewLogistic.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", "-" + e.getMessage());
        }
    }

    @Override
    public void onClickPackageItem(LogisticHistory item, int position) {
        startActivity(new Intent(getActivity(), LogisticDetailsActivity.class).putExtra("order_id",item.getOrderid()).putExtra("type","parcel"));

    }

    @OnClick({R.id.txt_neworder, R.id.txt_ongoing, R.id.txt_complet})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.txt_neworder:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtTitle.setText("New Order");

                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box1));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box1));
                getOrders("New");
                break;
            case R.id.txt_ongoing:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtTitle.setText(" OnGoing ");
                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box1));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box1));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box));
                getOrders("Ongoing");
                break;
            case R.id.txt_complet:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtTitle.setText("Completed");
                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box1));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box1));
                getOrders("Completed");

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
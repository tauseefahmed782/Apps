package com.cscodetech.pocketporter.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscodetech.pocketporter.R;
import com.cscodetech.pocketporter.activity.LogisticDetailsActivity;
import com.cscodetech.pocketporter.adepter.LogisticAdapter;
import com.cscodetech.pocketporter.model.Logistic;
import com.cscodetech.pocketporter.model.LogisticHistory;
import com.cscodetech.pocketporter.model.User;
import com.cscodetech.pocketporter.retrofit.APIClient;
import com.cscodetech.pocketporter.retrofit.GetResult;
import com.cscodetech.pocketporter.utility.CustPrograssbar;
import com.cscodetech.pocketporter.utility.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ParcelFragment extends Fragment implements LogisticAdapter.RecyclerTouchListener, GetResult.MyListener {


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

        View view = inflater.inflate(R.layout.fragment_parcel, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        recycleviewLogistic.setLayoutManager(mLayoutManager2);
        recycleviewLogistic.setItemAnimator(new DefaultItemAnimator());
        recycleviewLogistic.setAdapter(new LogisticAdapter(getActivity(), new ArrayList<>(), this));
        getOrders();
        return view;

    }

    private void getOrders() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());

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
                    if (!logistic.getLogisticHistory().isEmpty()) {
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
}
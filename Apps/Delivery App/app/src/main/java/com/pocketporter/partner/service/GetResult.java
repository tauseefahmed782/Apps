package com.pocketporter.partner.service;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.pocketporter.partner.MyApplication;
import com.pocketporter.partner.utility.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetResult {
    public static MyListener myListener;

    public void callForLogin(Call<JsonObject> call, String callno) {
        if (!Utility.internetChack()) {
            Toast.makeText(MyApplication.mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e("message", " : " + response.message());
                    Log.e("body", " : " + response.body());
                    Log.e("callno", " : " + callno);
                     myListener.callback(response.body(), callno);l̥
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    myListener.callback(null, callno);
                    call.cancel();
                    t.printStackTrace();
                }
            });
        }
    }

    public interface MyListener {
        // you can define any parameter as per your requirement
        public void callback(JsonObject result, String callNo);
    }

    public void setMyListener(MyListener myListener) {
        GetResult.myListener = myListener;
    }
}

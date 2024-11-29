package com.pocketporter.partner.service;


import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {



    @POST(APIClient.APPEND_URL + "dboy_login.php")
    Call<JsonObject> login(@Body RequestBody requestBody);


    @POST(APIClient.APPEND_URL + "pending_order.php")
    Call<JsonObject> pendingOrder(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "order_status_change.php")
    Call<JsonObject> orderStatusChange(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "log_status_change.php")
    Call<JsonObject> logStatusChange(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "total_order_report.php")
    Call<JsonObject> totalOrderReport(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "dboy_appstatus.php")
    Call<JsonObject> dboyAppstatus(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "pagelist.php")
    Call<JsonObject> pagelist(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "request_payout.php")
    Call<JsonObject> requestPayout(@Body RequestBody object);

    @POST(APIClient.APPEND_URL + "payout_list.php")
    Call<JsonObject> getPayoutlist(@Body RequestBody object);

    @POST(APIClient.APPEND_URL + "logistic_history.php")
    Call<JsonObject> logisticHistory(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "parcel_history.php")
    Call<JsonObject> parcelHistory(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "logistic_information.php")
    Call<JsonObject> logisticInformation(@Body RequestBody requestBody);


    @POST(APIClient.APPEND_URL + "parcel_information.php")
    Call<JsonObject> parcelInformation(@Body RequestBody requestBody);


    @POST(APIClient.APPEND_URL + "parcel_status_change.php")
    Call<JsonObject> parcelStatusChange(@Body RequestBody requestBody);



}

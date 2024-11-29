package com.cscodetech.pocketporter.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cscodetech.pocketporter.R;
import com.cscodetech.pocketporter.model.SPayment;
import com.cscodetech.pocketporter.model.User;
import com.cscodetech.pocketporter.retrofit.APIClient;
import com.cscodetech.pocketporter.utility.SessionManager;
import com.cscodetech.pocketporter.utility.Utility;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;


public class SenangpayActivity extends AppCompatActivity {
    double amount = 0;

    SessionManager sessionManager;
    User user;
    WebView webView;
    Random r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senangpay);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        webView = findViewById(R.id.webview);
        amount = getIntent().getDoubleExtra(getString(R.string.amount), 0);
        r= new Random();
        int randomNo = r.nextInt(1000 + 1);
        String postData = null;
        String utf="UTF-8";
        try {
            postData = "detail=" + URLEncoder.encode("Qwik Purchase Information", utf)
                    + "&amount=" + URLEncoder.encode(String.valueOf(amount), utf)
                    + "&order_id=" + URLEncoder.encode(String.valueOf(randomNo), utf)
                    + "&name=" + URLEncoder.encode(user.getFname(), utf)
                    + "&email=" + URLEncoder.encode("test@gmail.com", utf)
                    + "&phone=" + URLEncoder.encode(user.getMobile(), utf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = APIClient.baseUrl+"/result.php?" + postData;
        webView.getSettings().setJavaScriptEnabled(true);
        WebViewClientImpl webViewClient = new WebViewClientImpl();
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);


    }

    public class WebViewClientImpl extends WebViewClient {



        public WebViewClientImpl() {

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.indexOf("jenkov.com") > -1) return false;
            webView.loadUrl(url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.e("url", "--->" + url);
            if (url.contains("transaction_id")) {
                URL yahoo = null;
                try {
                    yahoo = new URL(url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    yahoo.openStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {

                        Log.e("PPP", "-->" + inputLine);
                        Gson gson = new Gson();
                        SPayment sPayment = gson.fromJson(inputLine, SPayment.class);

                        if (sPayment.getResult().equalsIgnoreCase("true")) {
                            Utility.tragectionID = sPayment.getTransactionId();
                            Utility.paymentsucsses = 1;
                        } else {
                            Utility.paymentsucsses = 0;
                        }
                        runOnUiThread(() -> Toast.makeText(SenangpayActivity.this,sPayment.getResponseMsg(),Toast.LENGTH_LONG).show());

                        finish();
                    }

                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
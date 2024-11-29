package com.pocketporter.partner.ui;

import static com.pocketporter.partner.utility.SessionManager.currency;
import static com.pocketporter.partner.utility.SessionManager.isChange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.map.FetchURL;
import com.pocketporter.partner.map.TaskLoadedCallback;
import com.pocketporter.partner.model.Dropitem;
import com.pocketporter.partner.model.OrderDataItem;
import com.pocketporter.partner.model.Orderstatus;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, GetResult.MyListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.orderid)
    TextView orderid;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.txt_details)
    TextView txtDetails;
    @BindView(R.id.txt_pickname)
    TextView txtPickname;
    @BindView(R.id.txt_pickaddress)
    TextView txtPickaddress;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_amount)
    TextView txtAmount;
    @BindView(R.id.txt_reject)
    TextView txtReject;
    @BindView(R.id.txt_accept)
    TextView txtAccept;
    @BindView(R.id.txt_pickp)
    TextView txtPickp;
    @BindView(R.id.txt_dpro)
    TextView txtDpro;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.lvl_accept)
    LinearLayout lvlAccept;
    OrderDataItem item;
    private Polyline currentPolyline;
    GoogleMap mMap;
    double dist;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    List<Dropitem> dropitems;
    Dropitem dropitem;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 201);
        }
        item = getIntent().getParcelableExtra("myclass");
        dropitems = (List<Dropitem>) getIntent().getSerializableExtra("list");
        item.setParceldata(dropitems);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        orderid.setText("" + item.getPackageNumber());
        status.setText("" + item.getOrderStatus());
        if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
            lvlAccept.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < dropitems.size(); i++) {
            Dropitem dropitem = dropitems.get(i);
            if (i != 0) {
                dist = dist + distance(dropitems.get(i - 1).getDropPointLat(), dropitems.get(i - 1).getDropPointLng(), dropitem.getDropPointLat(), dropitem.getDropPointLng());
            } else {
                dist = dist + distance(item.getPickLat(), item.getPickLng(), dropitems.get(0).getDropPointLat(), dropitems.get(0).getDropPointLng());
            }
            Log.e("dis", "-->" + dist);

        }

        txtDistance.setText("" + dist);
        txtAmount.setText(sessionManager.getStringData(currency) + item.getSubtotal());
        Double time = dist * 10;
        if (time < 60) {
            txtTime.setText(new DecimalFormat("##").format(time) + " mins");

        } else {
            double tamp = time / 60;
            txtTime.setText(new DecimalFormat("##.##").format(tamp) + " Hours");
        }

        if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
            txtReject.setVisibility(View.VISIBLE);
            txtAccept.setVisibility(View.VISIBLE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.GONE);
            imgCall.setVisibility(View.VISIBLE);
            txtPickname.setText("" + item.getPickName());
            txtPickaddress.setText("" + item.getPickAddress());
        } else if (item.getOrderStatus().equalsIgnoreCase("Processing")) {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.VISIBLE);
            txtDpro.setVisibility(View.GONE);
            txtPickname.setText("" + item.getPickName());
            txtPickaddress.setText("" + item.getPickAddress());
            imgCall.setVisibility(View.VISIBLE);

        } else if (item.getOrderStatus().equalsIgnoreCase("On Route")) {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.VISIBLE);
            for (int i = 0; i < dropitems.size(); i++) {
                if (dropitems.get(i).getDropPointStatus().equalsIgnoreCase("pending")) {
                    dropitem=dropitems.get(i);
                    txtPickname.setText("" + dropitems.get(i).getDropPointName());
                    txtPickaddress.setText("" + dropitems.get(i).getDropPointAddress());
                    if(dropitems.size()>=1){
                        if(i==dropitems.size()-1){
                            txtDpro.setText("Drop Done");
                        }else {
                            int temp=i+1;
                            txtDpro.setText("Stop "+temp+" Reached");
                        }

                    }
                    break;
                }
            }
//
            imgCall.setVisibility(View.VISIBLE);

        } else {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.GONE);
            txtPickname.setText("" + item.getPickName());
            txtPickaddress.setText("" + item.getPickAddress());
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {

                Gson gson = new Gson();
                Orderstatus orderstatus = gson.fromJson(result.toString(), Orderstatus.class);
                Toast.makeText(this, orderstatus.getResponseMsg(), Toast.LENGTH_LONG).show();
                isChange = true;
                finish();
            }

        } catch (Exception e) {
            Log.e("Error", "==>" + e.getMessage());

        }
    }


    private void orderStatusChange(String status,String stopid, String oid, String lat, String longs) {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("stopid", stopid);
            jsonObject.put("oid", oid);
            jsonObject.put("status", status);
            jsonObject.put("lats", lat);
            jsonObject.put("longs", longs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(jsonObject.toString(),MediaType.parse(getString(R.string.application_json)));
        Call<JsonObject> call = APIClient.getInterface().orderStatusChange(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    public void bottonDetails() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.tripe_deails_bottom, null);
        ImageView img_icon = sheetView.findViewById(R.id.img_icon);
        TextView txt_type = sheetView.findViewById(R.id.txt_type);
        TextView txt_date = sheetView.findViewById(R.id.txt_date);
        TextView txt_totle = sheetView.findViewById(R.id.txt_totle);
        TextView txt_pickname = sheetView.findViewById(R.id.txt_pickname);
        TextView txt_pickaddress = sheetView.findViewById(R.id.txt_pickaddress);
        LinearLayout lvlDrop = sheetView.findViewById(R.id.lvl_drop);
        TextView txt_distance = sheetView.findViewById(R.id.txt_distance);
        TextView txt_time = sheetView.findViewById(R.id.txt_time);
        TextView txt_ptype = sheetView.findViewById(R.id.txt_ptype);


        Glide.with(this).load(APIClient.baseUrl + "/" + item.getCatImg()).thumbnail(Glide.with(this).load(R.drawable.emty)).into(img_icon);
        txt_type.setText("" + item.getCatName());
        txt_date.setText("" + item.getDateTime());
        txt_totle.setText(sessionManager.getStringData(currency) + item.getSubtotal());
        txt_pickname.setText("" + item.getPickName());
        txt_pickaddress.setText("" + item.getPickAddress());


        for (int i = 0; i < item.getParceldata().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);

            View view = inflater.inflate(R.layout.custome_droplocation, null);
            TextView txtDropname = view.findViewById(R.id.txt_dropname);
            TextView txtDropaddress = view.findViewById(R.id.txt_dropaddress);
            txtDropname.setText("" + item.getParceldata().get(i).getDropPointName());
            txtDropaddress.setText("" + item.getParceldata().get(i).getDropPointAddress());

            lvlDrop.addView(view);
        }

        txt_ptype.setText("" + item.getPMethodName());
        txt_distance.setText("" + dist);

        Double time = dist * 10;

        if (time < 60) {
            txt_time.setText(new DecimalFormat("##").format(time) + " mins");

        } else {
            double tamp = time / 60;
            txt_time.setText(new DecimalFormat("##.##").format(tamp) + " Hours");
        }
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }


    @OnClick({R.id.img_back, R.id.txt_details, R.id.img_call, R.id.txt_reject, R.id.txt_accept, R.id.txt_pickp, R.id.txt_dpro})

    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_details:
                bottonDetails();
                break;
            case R.id.img_call:
                if(dropitem!=null){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + dropitem.getDropPointMobile()));
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + item.getPickMobile()));
                    startActivity(intent);
                }

                break;
            case R.id.txt_reject:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("reject","0", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }
                            }
                        });
                break;
            case R.id.txt_accept:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("accept","0", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }
                            }
                        });


                break;
            case R.id.txt_pickp:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("pickup","0", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }
                            }
                        });


                break;
            case R.id.txt_dpro:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    if(txtDpro.getText().toString().equalsIgnoreCase("Drop Done")){
                                        orderStatusChange("complete",dropitem.getId(), item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    }else {
                                        orderStatusChange("Stop_point",dropitem.getId(), item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    }
                                }
                            }
                        });


                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        final MarkerOptions[] place2 = new MarkerOptions[1];
        final MarkerOptions[] place1 = new MarkerOptions[1];
        if (item.getOrderStatus().equalsIgnoreCase("On Route")) {
            for (int i = 0; i < dropitems.size(); i++) {


                if (dropitems.get(i).getDropPointStatus().equalsIgnoreCase("pending")) {
                    if(i!=0){
                        place1[0] = new MarkerOptions().position(new LatLng(dropitems.get(i - 1).getDropPointLat(), dropitems.get(i - 1).getDropPointLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
                    }else {
                        place1[0] = new MarkerOptions().position(new LatLng(item.getPickLat(), item.getPickLng())).title("Path").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_long));
                    }
                    place2[0] = new MarkerOptions().position(new LatLng(dropitems.get(i).getDropPointLat(), dropitems.get(i).getDropPointLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
                    mMap.addMarker(place2[0]);
                    mMap.addMarker(place1[0]);

                    new FetchURL(TripDetailsActivity.this).execute(getUrl(place1[0].getPosition(), place2[0].getPosition(), "driving"), "driving");
                    break;
                }
            }
        }else {
            place1[0] = new MarkerOptions().position(new LatLng(item.getPickLat(), item.getPickLng())).title("Path").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_long));
            mMap.addMarker(place1[0]);
            for (int i = 0; i < dropitems.size(); i++) {
                if (i != 0) {
                    place1[0] = new MarkerOptions().position(new LatLng(dropitems.get(i - 1).getDropPointLat(), dropitems.get(i - 1).getDropPointLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
                }
                place2[0] = new MarkerOptions().position(new LatLng(dropitems.get(i).getDropPointLat(), dropitems.get(i).getDropPointLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
                mMap.addMarker(place2[0]);
                new FetchURL(TripDetailsActivity.this).execute(getUrl(place1[0].getPosition(), place2[0].getPosition(), "driving"), "driving");

            }
        }

        LatLng coordinate = new LatLng(item.getPickLat(), item.getPickLng());
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 12);
        mMap.animateCamera(yourLocation);
    }


    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = strOrigin + "&" + strDest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.API_KEY);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

//        if (currentPolyline != null)
//            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = (int) Math.round(dist);

            return (dist);
        }
    }


}
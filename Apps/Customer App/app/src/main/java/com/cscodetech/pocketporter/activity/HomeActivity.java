package com.cscodetech.pocketporter.activity;

import static com.cscodetech.pocketporter.utility.SessionManager.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cscodetech.pocketporter.R;
import com.cscodetech.pocketporter.fregment.AccountFragment;
import com.cscodetech.pocketporter.fregment.HomeSelectFragment;
import com.cscodetech.pocketporter.fregment.LogisticFragment;
import com.cscodetech.pocketporter.fregment.ParcelFragment;
import com.cscodetech.pocketporter.fregment.TripFragment;
import com.cscodetech.pocketporter.utility.SessionManager;
import com.cscodetech.pocketporter.utility.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        sessionManager = new SessionManager(HomeActivity.this);

        bottomNavigation.setOnNavigationItemSelectedListener(this);

        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && Utility.hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            Utility.enableLoc(this);
        } else {
            openFragment(new HomeSelectFragment(), "Home");
        }

    }

    public void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.e("Error", "-->" + e.getMessage());
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                HomeSelectFragment home = (HomeSelectFragment) getSupportFragmentManager().findFragmentByTag("Home");
                if (home != null && home.isVisible()) {
                    // DO STUFF
                } else {
                    openFragment(home, "Home");
                }
                break;

            case R.id.navigation_orders:
                openFragmentIfLoggedIn(new TripFragment(), "Trip");
                break;

            case R.id.navigation_logistic:
                openFragmentIfLoggedIn(new LogisticFragment(), "Logistic");
                break;

            case R.id.navigation_wallet:
                openFragmentIfLoggedIn(new ParcelFragment(), "Parcel");
                break;

            case R.id.navigation_user:
                openFragmentIfLoggedIn(new AccountFragment(), "Account");
                break;

            default:
                return false;
        }
        return true;
    }
    private void openFragmentIfLoggedIn(Fragment fragment, String tag) {
        if (sessionManager.getBooleanData(login)) {
            openFragment(fragment, tag);
        } else {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
package grab.com.thuexetoancau.UI.Activity.Passenger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Controller.PlaceArrayAdapter;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.FormFragment.ListPassengerBookingFragment;
import grab.com.thuexetoancau.UI.MapFragment.MapPassengerBookingFragment;
import grab.com.thuexetoancau.Utilities.Constants;
import grab.com.thuexetoancau.Utilities.GPSTracker;
import grab.com.thuexetoancau.Utilities.Utilities;

public class ListPassengerBookingActivity extends AppCompatActivity implements ListPassengerBookingFragment.CheckLocationListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{


    private Context mContext;
    private onDataRefresh dataRefresh;
    private onMapRefresh mapRefresh;

    private GPSTracker mLocation;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.roster,
            R.mipmap.maps
    };
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private OnConnected connected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_passenger_booking);
        mContext = this;
        initComponents();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initComponents() {

        viewPager       =   (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        // init google api
        mGoogleApiClient = new GoogleApiClient.Builder(ListPassengerBookingActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);

        ImageView imgBack =(ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    public void updateRefresh(onDataRefresh dataRefresh){
        this.dataRefresh = dataRefresh;
    }
    public void updateMap(onMapRefresh mapRefresh){
        this.mapRefresh = mapRefresh;

    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListPassengerBookingFragment(), "Thông tin");
        adapter.addFrag(new MapPassengerBookingFragment(), "Bản đồ");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onChecking() {
        requestPermission();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        //Log.i(LOG_TAG, "Google Places API connected.");
        if (connected != null)
            connected.onConnected(mGoogleApiClient, mPlaceArrayFromAdapter, mPlaceToArrayAdapter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayFromAdapter.setGoogleApiClient(null);
        mPlaceToArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Vui lòng kiểm tra lại kết nối", Toast.LENGTH_LONG).show();
    }
    public void updateApi(OnConnected listener) {
        connected = listener;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void requestPermission(){
        if (Utilities.isOnline(mContext)) {
            mLocation = new GPSTracker(this);
            if (mLocation.handlePermissionsAndGetLocation()) {
                if (!mLocation.canGetLocation()) {
                    settingRequestTurnOnLocation();
                } else {
                    ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    if (dataRefresh!= null)
                        dataRefresh.onLocationSuccess(dialog);
                    if (mapRefresh!= null)
                        mapRefresh.onLocationSuccess();
                }
            }
        }else {
            if (dataRefresh != null)
                dataRefresh.onOffline();
            if (mapRefresh!= null)
                mapRefresh.onOffline();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSIONS) {
            requestPermission();
        }
    }
    private void settingRequestTurnOnLocation() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Thông báo");  // GPS not found
        alertDialogBuilder.setMessage("Chức năng này cần lấy vị trí hiện tại của bạn.Bạn có muốn bật định vị?")
                .setCancelable(false)
                .setPositiveButton("Tiếp tục",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent,1000);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Không",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dataRefresh!= null)
                            dataRefresh.onLocationFailure();
                        if (mapRefresh!= null)
                            mapRefresh.onLocationFailure();
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                mLocation = new GPSTracker(this);
                if (!mLocation.canGetLocation()) {
                    if (dataRefresh != null)
                        dataRefresh.onLocationFailure();
                    if (mapRefresh!= null)
                        mapRefresh.onLocationFailure();
                }else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage("Đang lấy vị trí...");
                    dialog.show();
                    if (mLocation.getLongitude() == 0 && mLocation.getLatitude() == 0) {
                        mLocation.getLocationCoodinate(new GPSTracker.LocateListener() {
                            @Override
                            public void onLocate(double mlongitude, double mlatitude) {
                                if (dataRefresh != null)
                                    dataRefresh.onLocationSuccess(dialog);
                            }
                        });
                    } else {

                        if (dataRefresh != null)
                            dataRefresh.onLocationSuccess(dialog);
                        if (mapRefresh!= null)
                            mapRefresh.onLocationSuccess();
                    }
                }
                break;
        }
    }
    public interface onDataRefresh{
        public void onLocationSuccess(ProgressDialog dialog);
        public void onLocationFailure();
        public void onOffline();
    }
    public interface onMapRefresh{
        public void onLocationSuccess();
        public void onLocationFailure();
        public void onOffline();
    }
    public interface OnConnected {
        public void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo);
    }
}

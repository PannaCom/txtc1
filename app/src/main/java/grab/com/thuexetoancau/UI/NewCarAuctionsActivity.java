package grab.com.thuexetoancau.UI;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
public class NewCarAuctionsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,NewCarFormFragment.DataPassListener {
    private static final String LOG_TAG = "NewCarAuctionsActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter, mPlaceToArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private OnConnected connected;
    private OnDataMap onMap;
    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.roster,
            R.mipmap.maps
    };
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car_auctions);
        mContext = this;
        initComponents();
    }


    private void initComponents() {

        btnBack   = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ListDriverBookingActivity.class);
                startActivity(intent);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        // init google api
        mGoogleApiClient = new GoogleApiClient.Builder(NewCarAuctionsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this, BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this, BOUNDS_MOUNTAIN_VIEW, null);


    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment carList = new NewCarFormFragment();


        adapter.addFrag(carList, "Danh sách");
        adapter.addFrag(new MapNewCarFragment(), "Bản đồ");
        viewPager.setAdapter(adapter);
    }

    public void updateApi(NewCarAuctionsActivity.OnConnected listener) {
        connected = listener;
    }

    public void updateMap(NewCarAuctionsActivity.OnDataMap listener) {
        onMap = listener;
    }


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        //Log.i(LOG_TAG, "Google Places API connected.");
        if (connected != null)
            connected.onConnected(mGoogleApiClient, mPlaceArrayFromAdapter, mPlaceToArrayAdapter);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayFromAdapter.setGoogleApiClient(null);
        mPlaceToArrayAdapter.setGoogleApiClient(null);
        //Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void passData(String location, LatLng data) {
        if (onMap != null)
            onMap.OnDataMap(location, data);
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

    public interface OnConnected {
        public void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo);
    }

    public interface OnDataMap {
        public void OnDataMap(String location, LatLng latLng);
    }
}
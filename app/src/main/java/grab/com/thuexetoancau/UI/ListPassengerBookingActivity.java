package grab.com.thuexetoancau.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.Controller.PassengerCarAdapter;
import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Constants;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.GPSTracker;
import grab.com.thuexetoancau.Utilities.Utilities;

public class ListPassengerBookingActivity extends AppCompatActivity {

    private RecyclerView vehicleView;
    private ArrayList<BookingObject> vehicles;
    private Context mContext;
    private boolean isFilter = false;
    private TextView txtNoResult;
    private MenuItem itemFilter;
    private double longitude, latitude;
    private PassengerCarAdapter adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeToRefresh;
    private GPSTracker mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_passenger_booking);
        mContext = this;
        initComponents();
    }
    private void initComponents() {
        vehicleView                 =   (RecyclerView)          findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    findViewById(R.id.swipe_view);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBooking();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilities.isOnline(mContext)) {
            mLocation = new GPSTracker(this);
            if (mLocation.handlePermissionsAndGetLocation()) {
                if (!mLocation.canGetLocation()) {
                    settingRequestTurnOnLocation();
                } else {
                    dummyData();
                }
            }
        } else
            showOffline();

    }
    private void dummyData() {
        mLocation = new GPSTracker(this);
        if (!mLocation.canGetLocation()) {
            settingRequestTurnOnLocation();
        } else {
            dialog = new ProgressDialog(mContext);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Đang lấy vị trí...");
            dialog.show();
            longitude = mLocation.getLongitude();
            latitude = mLocation.getLatitude();

            getBooking();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSIONS) {
            dummyData();
        }
    }
    private void getBooking() {
        vehicles = new ArrayList<>();
        dialog.setMessage("Đang tải dữ liệu");
        RequestParams params;
        params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("order", 1);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET__BOOKING, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        parseJsonResult(jsonobject);
                    }
                    if(vehicles.size()>0) {
                        adapter = new PassengerCarAdapter(mContext, vehicles);
                        vehicleView.setAdapter(adapter);
                        adapter.setOnRequestComplete(new PassengerCarAdapter.onClickListener() {
                            @Override
                            public void onItemClick() {
                                dummyData();
                            }
                        });
                        //swipeToRefresh.setRefreshing(false);

                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Không có xe nào cho tuyến này");
                        //swipeToRefresh.setRefreshing(false);
                    }
                    dialog.dismiss();
                    //prepareDataSliding();
                    if (swipeToRefresh.isRefreshing())
                        swipeToRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void parseJsonResult(JSONObject jsonobject) {
        try {
            int id              = jsonobject.getInt("id");
            String carFrom      = jsonobject.getString("car_from");
            String carTo        = jsonobject.getString("car_to");
            String carType      = jsonobject.getString("car_type");

            String carHireType  = jsonobject.getString("car_hire_type");
            String carWhoHire   = jsonobject.getString("car_who_hire");
            String fromDate     = jsonobject.getString("from_datetime");
            String toDate       = jsonobject.getString("to_datetime");
            String dateBook     = jsonobject.getString("datebook");

            int price           = jsonobject.getInt("book_price");
            int priceMax        = jsonobject.getInt("book_price_max");
            int currentPrice = 0;
            if (!jsonobject.getString("current_price").equals("null"))
                currentPrice    = jsonobject.getInt("current_price");
            String timeReduce   = jsonobject.getString("time_to_reduce");

            double lon1         = jsonobject.getDouble("lon1");
            double lat1         = jsonobject.getDouble("lat1");
            double lon2         = jsonobject.getDouble("lon2");
            double lat2         = jsonobject.getDouble("lat2");

            double distance     = jsonobject.getDouble("D");

            BookingObject busInfor = new BookingObject(id, carFrom,carTo,carType,carHireType,carWhoHire,fromDate,toDate,dateBook,price,priceMax,currentPrice, timeReduce, new LatLng(lat1,lon1), new LatLng(lat2, lon2),distance);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
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
                dialog = new ProgressDialog(mContext);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setMessage("Đang lấy vị trí...");
                dialog.show();
                mLocation = new GPSTracker(this);
                if (mLocation.getLongitude() == 0 && mLocation.getLatitude() ==0) {
                    mLocation.getLocationCoodinate(new GPSTracker.LocateListener() {
                        @Override
                        public void onLocate(double mlongitude, double mlatitude) {
                            longitude = mlongitude;
                            latitude = mlatitude;
                            Toast.makeText(mContext, longitude+","+latitude, Toast.LENGTH_SHORT).show();
                            getBooking();
                        }
                    });
                }else{
                    longitude = mLocation.getLongitude();
                    latitude = mLocation.getLatitude();
                    Toast.makeText(mContext, longitude+","+latitude, Toast.LENGTH_SHORT).show();
                    getBooking();
                }
                break;
            case 999:
                if (mLocation.handlePermissionsAndGetLocation()) {
                    if (!mLocation.canGetLocation()) {
                        settingRequestTurnOnLocation();
                    } else {
                        dummyData();
                    }
                }
                break;
        }
    }

    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
}

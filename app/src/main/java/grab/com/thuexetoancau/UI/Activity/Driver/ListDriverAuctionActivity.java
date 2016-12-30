package grab.com.thuexetoancau.UI.Activity.Driver;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.Controller.BookingCarAdapter;
import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Passenger.FormPassengerBookingActivity;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Constants;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.GPSTracker;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

public class ListDriverAuctionActivity extends AppCompatActivity {

    private RecyclerView vehicleView;
    private ArrayList<BookingObject> vehicles;
    private Context mContext;
    private boolean isFilter = false;
    private TextView txtNoResult;
    private MenuItem itemFilter;
    private double longitude, latitude;
    private BookingCarAdapter adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeToRefresh;
    private GPSTracker mLocation;
    private ImageView imgMenu;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView txtAccount, txtStatus;
    private FrameLayout layoutStatus;
    private int money;
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_driver_auction);
        mContext = this;
        preference = new SharePreference(this);
        initComponents();
    }
    private void initComponents() {
        vehicleView                 =   (RecyclerView)          findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    findViewById(R.id.swipe_view);
        ImageView btnBack           =   (ImageView)             findViewById(R.id.btn_back);
        imgMenu                     =   (ImageView)             findViewById(R.id.img_menu);
        txtAccount                  =   (TextView)              findViewById(R.id.txt_account);
        txtStatus                   =   (TextView)              findViewById(R.id.txt_status);
        layoutStatus                =   (FrameLayout)           findViewById(R.id.layout_status);
        if (preference.getStatus() == 1){
            txtStatus.setText("Ngoại tuyến");
            layoutStatus.setBackgroundColor(getResources().getColor(R.color.md_red_600));
        }else{
            txtStatus.setText("Trực tuyến");
            layoutStatus.setBackgroundColor(getResources().getColor(R.color.green_1));
        }

        layoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preference.getStatus() == 0){
                    preference.saveStatus(1);
                    txtStatus.setText("Ngoại tuyến");
                    layoutStatus.setBackgroundColor(getResources().getColor(R.color.md_red_600));
                }else{
                    preference.saveStatus(0);
                    txtStatus.setText("Trực tuyến");
                    layoutStatus.setBackgroundColor(getResources().getColor(R.color.green_1));
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doubleBackToExitPressedOnce) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.notice_close_app), Toast.LENGTH_SHORT).show();
                    doubleBackToExitPressedOnce = true;
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ListDriverAuctionActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_edit:
                                Intent intent = new Intent(mContext, EditDriverActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.action_list_driver:
                                Intent intentList = new Intent(mContext, ListDriverBookingActivity.class);
                                startActivity(intentList);
                                return true;
                            case R.id.action_post_drive:
                                Intent intentPost = new Intent(mContext, GetInforDriverBookingActivity.class);
                                startActivity(intentPost);
                                return true;
                            case R.id.action_list_auction:
                                Intent intentAuction = new Intent(mContext, ListAuctionSuccessActivity.class);
                                startActivity(intentAuction);
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBooking();
            }
        });

        checkValidAccount();

    }

    private void checkValidAccount() {
        SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_STATUS_DRIVER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                int status = Integer.valueOf(new String(responseBody));
               if (status == -1){
                   showDialogInValidAccount();
               }else{
                   // set cardview
                   vehicleView.setHasFixedSize(true);
                   LinearLayoutManager llm = new LinearLayoutManager(mContext);
                   vehicleView.setLayoutManager(llm);
                   if (Utilities.isOnline(mContext)) {
                       if (Build.VERSION.SDK_INT >= 22) {
                           if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                               ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                           }else{
                               getCurrentAccount();
                               mLocation = new GPSTracker(mContext);
                               if (mLocation.handlePermissionsAndGetLocation()) {
                                   if (!mLocation.canGetLocation()) {
                                       settingRequestTurnOnLocation();
                                   } else {
                                       dummyData();
                                   }
                               }
                           }
                       }
                   } else
                       showOffline();
               }
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

    private void showDialogInValidAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Tài khoản của bạn đã bị hạn chế quyền sử dụng, vui lòng liên hệ với công ty để được hỗ trợ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void sendGPSDriver() {
        if (!Defines.startThread) {
            Thread t = new Thread(new sendLocate());
            t.start();
            Defines.startThread = true;
        }
    }

    public void newDriverCar(View v) {
        Intent intentList = new Intent(mContext, NewCarAuctionsActivity.class);
        startActivity(intentList);
    }
    private void getCurrentAccount() {
        SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("id_driver", preference.getDriverId());
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_MONEY_DRIVER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                money = Integer.valueOf(new String(responseBody));
                txtAccount.setText(Utilities.convertCurrency(money)+" VNĐ");
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

    public void list_passenger_booking_listener(View v) {
        Intent intent = new Intent(mContext, ListDriverBookingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(mContext, getResources().getString(R.string.notice_close_app), Toast.LENGTH_SHORT).show();
                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
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
            sendGPSDriver();
            getBooking();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS) {
            getCurrentAccount();
            mLocation = new GPSTracker(this);
            if (mLocation.handlePermissionsAndGetLocation()) {
                if (!mLocation.canGetLocation()) {
                    settingRequestTurnOnLocation();
                } else {
                    dummyData();
                }
            }
        }else if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSIONS && grantResults[0] == 0) {
            dummyData();
        }
    }
    private void getBooking() {
        vehicles = new ArrayList<>();
        if (dialog == null){
            dialog = new ProgressDialog(mContext);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setMessage("Đang tải dữ liệu");
        RequestParams params;
        params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("lon", longitude);
        params.put("car_hire_type","Khứ hồi,Một chiều,Sân bay");
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
                        removeAuctionExpire();
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
    private void removeAuctionExpire() {
        ArrayList<BookingObject> temp = new ArrayList<>();
        for (BookingObject bookin : vehicles)
            temp.add(bookin);
        for (BookingObject bookin : temp){
            DateTime lastDay = new DateTime(bookin.getFromDate());
            DateTime now = new DateTime();
            long diffInMillis = 0;
            if (bookin.getBookPrice() > Defines.BOUNDER_TRIP_PRICE)
                diffInMillis = lastDay.getMillis() - now.getMillis()-Defines.TIME_BEFORE_AUCTION_LONG;
            else
                diffInMillis = lastDay.getMillis() - now.getMillis()-Defines.TIME_BEFORE_AUCTION_SHORT;

            if (diffInMillis <= 0) {
                for (BookingObject candidate : vehicles)
                if (bookin.getId() == candidate.getId()) {
                    vehicles.remove(candidate);
                    break;
                }
            }
        }
        adapter = new BookingCarAdapter(mContext, vehicles,money);
        vehicleView.setAdapter(adapter);
        adapter.setOnRequestComplete(new BookingCarAdapter.onClickListener() {
            @Override
            public void onItemClick() {
                getBooking();
            }
        });
        adapter.setOnPaySuccess(new BookingCarAdapter.onPayMoneyListener() {
            @Override
            public void onSuccess() {
                getCurrentAccount();
            }
        });
        //swipeToRefresh.setRefreshing(false);
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
    private class sendLocate implements Runnable {
        public void run() {
            try {
                while (true) {
                    if (preference.getStatus() ==0) {
                        Log.e("TAG", "loop");
                        sendLocationToServer();
                        Thread.sleep(Defines.LOOP_TIME);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendLocationToServer() {
        RequestParams params;
        params = new RequestParams();
        params.put("car_number", preference.getCarNumber());
        params.put("phone", preference.getPhone());
        params.put("lon", longitude);
        params.put("lat", latitude);
        params.put("status", 0);
        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_POST_DRIVER_GPS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }
}

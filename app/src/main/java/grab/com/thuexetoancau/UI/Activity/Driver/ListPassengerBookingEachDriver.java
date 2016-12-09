package grab.com.thuexetoancau.UI.Activity.Driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.Controller.PassengerBookingAdapter;
import grab.com.thuexetoancau.Model.PassengerBookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.Utilities;

public class ListPassengerBookingEachDriver extends AppCompatActivity {
    private RecyclerView vehicleView;
    private ArrayList<PassengerBookingObject> passengers;
    private Context mContext;
    private TextView txtNoResult;
    private PassengerBookingAdapter adapter;
    private SwipeRefreshLayout swipeToRefresh;
    private ProgressDialog dialog;
    private int bookingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_list_passenger_booking_each_driver);
        initComponents();
    }
    private void initComponents() {
        vehicleView                 =   (RecyclerView)          findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    findViewById(R.id.swipe_view);
        ImageView btnBack           =   (ImageView)             findViewById(R.id.btn_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bookingId = extras.getInt("ID");
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListPassengerBooking();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilities.isOnline(mContext)) {
            getListPassengerBooking();
        } else
            showOffline();




    }

    private void getListPassengerBooking() {
        passengers = new ArrayList<>();
        dialog = new ProgressDialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage("Đang tải dữ liệu");
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_BOOKING_LOG, params, new AsyncHttpResponseHandler() {

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
                    if(passengers.size()>0) {
                        adapter = new PassengerBookingAdapter(mContext, passengers);
                        vehicleView.setAdapter(adapter);
                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Bạn chưa đăng ký chuyến nào");
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
            int bookingId       = jsonobject.getInt("id_booking");
            String name         = jsonobject.getString("name");
            int phone           = jsonobject.getInt("phone");

            String dateTime  = jsonobject.getString("date_time");


            PassengerBookingObject passengerObject = new PassengerBookingObject(id,bookingId,phone,name,dateTime);
            passengers.add(passengerObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
}

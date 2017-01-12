package grab.com.thuexetoancau.UI.Activity.Driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.Controller.AuctionSuccessAdapter;
import grab.com.thuexetoancau.Model.AuctionSuccessObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

public class ListAuctionSuccessActivity extends AppCompatActivity {
    private RecyclerView vehicleView;
    private ArrayList<AuctionSuccessObject> vehicles;
    private Context mContext;
    private TextView txtNoResult;
    private double longitude, latitude;
    private AuctionSuccessAdapter adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeToRefresh;
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_auction_success);
        mContext = this;
        preference = new SharePreference(this);
        initComponents();
    }
    private void initComponents() {
        vehicleView                 =   (RecyclerView)          findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    findViewById(R.id.swipe_view);
        ImageView btnBack           =   (ImageView)             findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListAuctionSuccess();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilities.isOnline(mContext)) {
            getListAuctionSuccess();
        } else
            showOffline();
    }
    private void getListAuctionSuccess() {
        vehicles = new ArrayList<>();
        dialog = new ProgressDialog(mContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.show();

        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_BOOKING_SUCCESS, params, new AsyncHttpResponseHandler() {

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
                        adapter = new AuctionSuccessAdapter(mContext, vehicles);
                        vehicleView.setAdapter(adapter);
                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Bạn chưa đấu giá chuyến nào");
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
            int phone           = jsonobject.getInt("phone");
            AuctionSuccessObject busInfor = new AuctionSuccessObject(id, carFrom,carTo,carType,carHireType,carWhoHire,fromDate,toDate,dateBook,price,priceMax,currentPrice, timeReduce, new LatLng(lat1,lon1), new LatLng(lat2, lon2),distance, phone);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
}
package grab.com.thuexetoancau.UI.FormFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import grab.com.thuexetoancau.UI.Activity.Passenger.ListPassengerBookingActivity;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.GPSTracker;

/**
 * Created by DatNT on 12/6/2016.
 */

public class ListPassengerBookingFragment extends Fragment {
    private RecyclerView vehicleView;
    private ArrayList<BookingObject> vehicles;
    private TextView txtNoResult;
    private double longitude, latitude;
    private PassengerCarAdapter adapter;
    private SwipeRefreshLayout swipeToRefresh;
    private ProgressDialog dialog;
    private GPSTracker mLocation;
    private CheckLocationListener mCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.get_passenger_booking_fragment, container, false);
        vehicleView                 =   (RecyclerView)          view.findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              view.findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    view.findViewById(R.id.swipe_view);
        initComponents();

        ((ListPassengerBookingActivity) getActivity()).updateRefresh(new ListPassengerBookingActivity.onDataRefresh() {
            @Override
            public void onLocationSuccess(ProgressDialog mdialog) {
                dialog = mdialog;
                getBooking();
            }

            @Override
            public void onLocationFailure() {
                showOffline("Bạn phải bật chức năng định vị để dùng chức năng này");

            }

            @Override
            public void onOffline() {
                showOffline("Không có kết nối mạng");
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void initComponents() {

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCallback.onChecking();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        vehicleView.setLayoutManager(llm);
    }

    private void getBooking() {
        mLocation = new GPSTracker(getActivity());
        longitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        vehicles = new ArrayList<>();
        dialog.setMessage("Đang tải dữ liệu");
        RequestParams params;
        params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("car_hire_type", "Chiều về,Đi chung");
        params.put("order", 1);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_BOOKING_CUSTOMER, params, new AsyncHttpResponseHandler() {

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
                        adapter = new PassengerCarAdapter(getActivity(), vehicles);
                        vehicleView.setAdapter(adapter);
                        adapter.setOnRequestComplete( new PassengerCarAdapter.onClickListener() {
                            @Override
                            public void onItemClick() {
                                getBooking();
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
                swipeToRefresh.setRefreshing(false);
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

    private void showOffline(String text) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(text);
    }
    public interface CheckLocationListener{
        public void onChecking();
    }
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (ListPassengerBookingFragment.CheckLocationListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DataPassListener");
        }
    }
}

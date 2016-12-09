package grab.com.thuexetoancau.UI.MapFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Passenger.ListPassengerBookingActivity;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.GPSTracker;

/**
 * Created by DatNT on 10/7/2016.
 */

public class MapPassengerBookingFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mMapView;
    private double longitude, latitude;
    private ProgressDialog dialog;
    private LatLng curLatLng;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private GPSTracker mLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_active_car_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        ((ListPassengerBookingActivity) getActivity()).updateMap(new ListPassengerBookingActivity.onMapRefresh() {
            @Override
            public void onLocationSuccess() {
                getBooking();
            }

            @Override
            public void onLocationFailure() {
            }

            @Override
            public void onOffline() {
            }
        });

    }
    private void getBooking() {
        mLocation = new GPSTracker(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Đang lấy vị trí...");
        dialog.show();
        longitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        curLatLng = new LatLng(latitude, longitude);
        Marker markerTo = mMap.addMarker(new MarkerOptions().position(curLatLng).title("Vị trí của bạn"));
        markerList.add(markerTo);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));
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
                    dialog.dismiss();
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

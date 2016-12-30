package grab.com.thuexetoancau.UI.MapFragment;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
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
    private ImageView imgRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_active_car_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        imgRefresh  =   (ImageView) view.findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllMarker();
                getCarAround();
            }
        });
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
        getCarAround();
    }
    private void getCarAround() {
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
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_AROUND, params, new AsyncHttpResponseHandler() {

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
            double lon         = jsonobject.getDouble("lon");
            double lat         = jsonobject.getDouble("lat");
            double distance    = jsonobject.getDouble("D");
            DecimalFormat df = new DecimalFormat("#.#");
            String gap = "";
            if ((int) distance == 0)
                gap = df.format(distance*1000) + " m";
            else
                gap = df.format(distance) + " km";
            LatLng aroundLatLon = new LatLng(lon, lat);
            Marker marker = mMap.addMarker(new MarkerOptions().position(curLatLng).title("Xe cách bạn "+gap));
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon));
            markerList.add(marker);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void removeAllMarker(){
        for (Marker marker : markerList)
            marker.remove();
    }
}

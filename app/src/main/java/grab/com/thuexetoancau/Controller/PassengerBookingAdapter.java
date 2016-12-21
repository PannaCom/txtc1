package grab.com.thuexetoancau.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.Model.PassengerBookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Driver.ListPassengerBookingEachDriver;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class PassengerBookingAdapter extends RecyclerView.Adapter<PassengerBookingAdapter.ViewHolder>{


    private static final String LOG_TAG = PassengerBookingAdapter.class.getSimpleName();
    private Context mContext;
    private List<PassengerBookingObject> mVehicle;
    private SharePreference preference;
    public PassengerBookingAdapter(Context context, ArrayList<PassengerBookingObject> vehicle) {
        mContext = context;
        preference = new SharePreference(mContext);
        this.mVehicle = vehicle;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_passenger_booking_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtName.setText(mVehicle.get(position).getName());
        holder.txtPhone.setText(mVehicle.get(position).getPhone()+"");


        DateTime jDateFrom = new DateTime(mVehicle.get(position).getDateTimeOrder());

        holder.txtDateTimeOrder.setText(Utilities.convertTime2Lines(jDateFrom));
        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                        return;
                    }
                }
                Log.e("tag", "tag");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mVehicle.get(position).getPhone()));
                mContext.startActivity(intent);
                payMoney(position);
            }
        });
    }
    private void payMoney(int id_booking) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_driver",preference.getDriverId());
        params.put("id_booking",id_booking);
        BaseService.getHttpClient().post(Defines.URL_CONFIRM, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

  /*  private void bookingFinal(int position) {
        RequestParams params = new RequestParams();
        params.put("id_driver",preference.getDriverId());
        params.put("driver_number", carNumber);
        params.put("driver_phone", driverPhone);
        params.put("price", price);
        params.put("id_booking", mVehicle.get(position).getId());
        params.put("type", type);
        BaseService.getHttpClient().post(Defines.URL_BOOKING_FINAL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    public int getItemCount() {

        if (mVehicle == null) return 0;
        else return mVehicle.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public List<PassengerBookingObject> getData() {
        if (mVehicle != null){
           return mVehicle;
        } else return null;
    };

    public class ViewHolder extends RecyclerView.ViewHolder{


        CardView    cardview;
        TextView    txtName;
        TextView    txtPhone;
        TextView    txtDateTimeOrder;
        ImageView   imgCall;

        public ViewHolder(View itemView) {
            super(itemView);


            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtName         = (TextView)        itemView.findViewById(R.id.txt_name);
            txtPhone        = (TextView)        itemView.findViewById(R.id.txt_phone);
            txtDateTimeOrder= (TextView)        itemView.findViewById(R.id.txt_date_time_order);
            imgCall         = (ImageView)       itemView.findViewById(R.id.img_call);


        }
    }

}
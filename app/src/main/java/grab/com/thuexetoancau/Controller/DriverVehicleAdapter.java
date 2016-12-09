package grab.com.thuexetoancau.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Driver.ListPassengerBookingEachDriver;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class DriverVehicleAdapter extends RecyclerView.Adapter<DriverVehicleAdapter.ViewHolder>{


    private static final String LOG_TAG = DriverVehicleAdapter.class.getSimpleName();
    private Context mContext;
    private List<BookingObject> mVehicle;
    public DriverVehicleAdapter(Context context, ArrayList<BookingObject> vehicle) {
        mContext = context;
        this.mVehicle = vehicle;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_driver_booking_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtCarFrom.setText(mVehicle.get(position).getCarFrom());
        holder.txtCarTo.setText(mVehicle.get(position).getCarTo());
        holder.txtCarHireType.setText(mVehicle.get(position).getCarHireType());

        DateTime jDateFrom = new DateTime(mVehicle.get(position).getFromDate());

        holder.txtDateFrom.setText(Utilities.convertTime(jDateFrom));
        DateTime jDateTo = new DateTime(mVehicle.get(position).getToDate());
        holder.txtDateTo.setText(Utilities.convertTime(jDateTo));
        holder.txtBookingId.setText("Chuyến số #"+mVehicle.get(position).getId());
        holder.txtCarSize.setText(mVehicle.get(position).getCarType() +" chỗ");
        holder.txtCarTargeType.setText(mVehicle.get(position).getCarWhoHire());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ListPassengerBookingEachDriver.class);
                intent.putExtra("ID",mVehicle.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (mVehicle == null) return 0;
        else return mVehicle.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public List<BookingObject> getData() {
        if (mVehicle != null){
           return mVehicle;
        } else return null;
    };

    public class ViewHolder extends RecyclerView.ViewHolder{


        CardView cardview;
        TextView txtCarFrom;
        TextView txtCarTo;
        TextView txtCarHireType;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtBookingId;
        TextView txtCarSize;
        TextView txtCarTargeType;

        public ViewHolder(View itemView) {
            super(itemView);


            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtCarFrom      = (TextView)        itemView.findViewById(R.id.txt_from);
            txtCarTo        = (TextView)        itemView.findViewById(R.id.txt_to);
            txtCarHireType  = (TextView)        itemView.findViewById(R.id.txt_car_hire_type);
            txtDateFrom     = (TextView)        itemView.findViewById(R.id.txt_date_from);
            txtDateTo       = (TextView)        itemView.findViewById(R.id.txt_date_to);
            txtCarTargeType = (TextView)        itemView.findViewById(R.id.txt_car_who_hire);
            txtBookingId    = (TextView)        itemView.findViewById(R.id.txt_id);
            txtCarSize      = (TextView)        itemView.findViewById(R.id.txt_car_size);


        }
    }

}
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
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.AuctionSuccessObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class AuctionSuccessAdapter extends RecyclerView.Adapter<AuctionSuccessAdapter.ViewHolder>{


    private static final String LOG_TAG = AuctionSuccessAdapter.class.getSimpleName();
    private Context mContext;
    private List<AuctionSuccessObject> mVehicle;
    private SharePreference preference;

    public AuctionSuccessAdapter(Context context, ArrayList<AuctionSuccessObject> vehicle) {
        mContext = context;
        this.mVehicle = vehicle;
        preference = new SharePreference(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_success_aution, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtCarFrom.setText(mVehicle.get(position).getCarFrom());
        holder.txtCarTo.setText(mVehicle.get(position).getCarTo());
        holder.txtPhone.setText(mVehicle.get(position).getPhone()+"");

        DateTime jDateFrom = new DateTime(mVehicle.get(position).getFromDate());
        holder.txtDateFrom.setText(Utilities.convertTime(jDateFrom));

        DateTime jDateTo = new DateTime(mVehicle.get(position).getToDate());
        holder.txtDateTo.setText(Utilities.convertTime(jDateTo));

        holder.txtCarHire.setText(mVehicle.get(position).getCarHireType());

        holder.txtPrice.setText(Utilities.convertCurrency(mVehicle.get(position).getCurrentPrice())+" VNĐ");

        holder.layoutCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                        return;
                    }
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mVehicle.get(position).getPhone()));
                mContext.startActivity(callIntent);
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

    public class ViewHolder extends RecyclerView.ViewHolder{


        CardView cardview;
        TextView txtCarFrom;
        TextView txtCarTo;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtPhone;
        TextView txtCarHire;
        TextView txtPrice;
        FrameLayout layoutCall;
        public ViewHolder(View itemView) {
            super(itemView);


            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtCarFrom      = (TextView)        itemView.findViewById(R.id.txt_from);
            txtCarTo        = (TextView)        itemView.findViewById(R.id.txt_to);
            txtPhone        = (TextView)        itemView.findViewById(R.id.txt_phone);
            txtDateFrom     = (TextView)        itemView.findViewById(R.id.txt_date_from);
            txtDateTo       = (TextView)        itemView.findViewById(R.id.txt_date_to);
            txtCarHire      = (TextView)        itemView.findViewById(R.id.txt_hire_type);
            txtPrice        = (TextView)        itemView.findViewById(R.id.txt_price);
            layoutCall      = (FrameLayout)     itemView.findViewById(R.id.layout_call);
        }
    }

}
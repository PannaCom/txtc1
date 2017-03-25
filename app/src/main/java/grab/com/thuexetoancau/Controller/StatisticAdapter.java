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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.AuctionSuccessObject;
import grab.com.thuexetoancau.Model.Statistic;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder>{
    private Context mContext;
    private List<Statistic> arrayStatistic;

    public StatisticAdapter(Context context, ArrayList<Statistic> vehicle) {
        mContext = context;
        this.arrayStatistic = vehicle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_statistic, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.id.setText(arrayStatistic.get(position).getId()+"");
        holder.type.setText(arrayStatistic.get(position).getType());

        DateTime date = new DateTime(arrayStatistic.get(position).getDate());
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        holder.date.setText(dtf.print(date));

        holder.money.setText(arrayStatistic.get(position).getMoney()+"");
    }
    @Override
    public int getItemCount() {

        if (arrayStatistic == null) return 0;
        else return arrayStatistic.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView type;
        TextView date;
        TextView money;
        public ViewHolder(View itemView) {
            super(itemView);
            id        = (TextView)        itemView.findViewById(R.id.txt_id);
            type      = (TextView)        itemView.findViewById(R.id.txt_deal_type);
            date      = (TextView)        itemView.findViewById(R.id.txt_deal_date);
            money     = (TextView)        itemView.findViewById(R.id.txt_deal_cost);        }
    }

}
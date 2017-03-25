package grab.com.thuexetoancau.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.Statistic;
import grab.com.thuexetoancau.Model.StatisticNoDetail;
import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 11/17/2016.
 */
public class StatisticNoDetailAdapter extends RecyclerView.Adapter<StatisticNoDetailAdapter.ViewHolder>{
    private Context mContext;
    private List<StatisticNoDetail> arrayStatistic;

    public StatisticNoDetailAdapter(Context context, ArrayList<StatisticNoDetail> vehicle) {
        mContext = context;
        this.arrayStatistic = vehicle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_statistic_no_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.carNum.setText(arrayStatistic.get(position).getCarNumber());
        DecimalFormat df = new DecimalFormat("#.##############");
        holder.sum.setText(df.format(arrayStatistic.get(position).getSum())+"");
        holder.count.setText(arrayStatistic.get(position).getCount()+"");
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
        TextView carNum;
        TextView sum;
        TextView count;
        public ViewHolder(View itemView) {
            super(itemView);
            carNum        = (TextView)   itemView.findViewById(R.id.txt_car_register);
            sum           = (TextView)   itemView.findViewById(R.id.txt_deal_sum);
            count         = (TextView)   itemView.findViewById(R.id.txt_deal_count);
        }
    }

}
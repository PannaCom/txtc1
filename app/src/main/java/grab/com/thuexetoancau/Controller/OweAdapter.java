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

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.Model.Owe;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class OweAdapter extends RecyclerView.Adapter<OweAdapter.ViewHolder>{
    private Context mContext;
    private List<Owe> arrayOwe;

    public OweAdapter(Context context, ArrayList<Owe> owe) {
        mContext = context;
        this.arrayOwe = owe;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_owe, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

        DateTime month = new DateTime(arrayOwe.get(position).getDateMonth());
        holder.txtMonth.setText(dtf.print(month));
        holder.moneyMonth.setText(Utilities.convertCurrency(arrayOwe.get(position).getMoneyMonth()));

        DateTime date = new DateTime(arrayOwe.get(position).getDatePeriod());
        holder.txtPeriod.setText(dtf.print(date));
        holder.moneyPeriod.setText(Utilities.convertCurrency(arrayOwe.get(position).getMoneyPeriod()));

        DateTime year = new DateTime(arrayOwe.get(position).getDateYear());
        holder.txtYear.setText(dtf.print(year));
        holder.moneyYear.setText(Utilities.convertCurrency(arrayOwe.get(position).getMoneyYear()));
    }
    @Override
    public int getItemCount() {

        if (arrayOwe == null) return 0;
        else return arrayOwe.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtMonth;
        TextView moneyMonth;
        TextView txtPeriod;
        TextView moneyPeriod;
        TextView txtYear;
        TextView moneyYear;
        public ViewHolder(View itemView) {
            super(itemView);
            txtMonth        = (TextView)        itemView.findViewById(R.id.txt_month);
            moneyMonth      = (TextView)        itemView.findViewById(R.id.money_month);
            txtPeriod       = (TextView)        itemView.findViewById(R.id.txt_period);
            moneyPeriod     = (TextView)        itemView.findViewById(R.id.money_period);
            txtYear         = (TextView)        itemView.findViewById(R.id.txt_year);
            moneyYear       = (TextView)        itemView.findViewById(R.id.money_year);
        }
    }

}
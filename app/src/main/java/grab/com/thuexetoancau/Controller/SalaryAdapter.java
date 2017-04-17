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

import grab.com.thuexetoancau.Model.Salary;
import grab.com.thuexetoancau.Model.Statistic;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder>{
    private Context mContext;
    private List<Salary> arraySalary;

    public SalaryAdapter(Context context, ArrayList<Salary> salaries) {
        mContext = context;
        this.arraySalary = salaries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salary, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DateTime date = new DateTime(arraySalary.get(position).getFromDate());
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        holder.txtDateFrom.setText(dtf.print(date));

        DateTime dateTo = new DateTime(arraySalary.get(position).getToDate());
        holder.txtDateTo.setText(dtf.print(dateTo));

        holder.txtSalary.setText(Utilities.convertCurrency(arraySalary.get(position).getMoney()));
    }
    @Override
    public int getItemCount() {

        if (arraySalary == null) return 0;
        else return arraySalary.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtSalary;
        public ViewHolder(View itemView) {
            super(itemView);
            txtDateFrom      = (TextView)        itemView.findViewById(R.id.txt_date_from);
            txtDateTo      = (TextView)        itemView.findViewById(R.id.txt_date_to);
            txtSalary     = (TextView)        itemView.findViewById(R.id.txt_salary);        }
    }

}
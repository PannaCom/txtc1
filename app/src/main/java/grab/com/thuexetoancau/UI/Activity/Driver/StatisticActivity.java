package grab.com.thuexetoancau.UI.Activity.Driver;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import grab.com.thuexetoancau.Controller.StatisticAdapter;
import grab.com.thuexetoancau.Controller.StatisticNoDetailAdapter;
import grab.com.thuexetoancau.Model.Statistic;
import grab.com.thuexetoancau.Model.StatisticNoDetail;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SimpleDividerItemDecoration;

public class StatisticActivity extends AppCompatActivity {
    private AutoCompleteTextView edtCarRegister;
    private Context mContext;
    private TextView txtDateFrom, txtDateTo, txtNoStatistic;
    private RecyclerView listStatistic;
    private Button btnSearch;
    private CheckBox isDetail;
    private ArrayList<Statistic> arrayStatistic;
    private ArrayList<StatisticNoDetail> arrayStatisticNoDetail;
    private LinearLayout headerStatistic, layoutOverall;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        txtDateFrom = (TextView) findViewById(R.id.btn_date_from);
        txtDateTo = (TextView) findViewById(R.id.btn_date_to);
        listStatistic = (RecyclerView) findViewById(R.id.list_statistic);
        edtCarRegister = (AutoCompleteTextView) findViewById(R.id.car_name);
        isDetail = (CheckBox) findViewById(R.id.check_detail);
        btnSearch = (Button) findViewById(R.id.btn_search);
        headerStatistic = (LinearLayout) findViewById(R.id.header_statistic);
        txtNoStatistic = (TextView) findViewById(R.id.txt_no_statistic);
        listStatistic = (RecyclerView) findViewById(R.id.list_statistic);
        imgBack = (ImageView)           findViewById(R.id.btn_back);
        layoutOverall = (LinearLayout) findViewById(R.id.result_no_detail);

        listStatistic.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        listStatistic.setLayoutManager(llm);
        listStatistic.addItemDecoration(new SimpleDividerItemDecoration(this));


        edtCarRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                requestCarRegister(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(txtDateFrom);
            }
        });

        txtDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(txtDateTo);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letsSearch();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void requestCarRegister(String s) {
        final ArrayList<String> arrayCar = new ArrayList<>();
        RequestParams params;
        params = new RequestParams();
        params.put("keyword",s);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().get(Defines.URL_CAR_REGISTATION, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        String result = arrayresult.getString(i);
                        arrayCar.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,arrayCar);
                edtCarRegister.setAdapter(adapter);
                edtCarRegister.setThreshold(1);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void letsSearch() {
        if (isDetail.isChecked()) {
            if (edtCarRegister.getText().toString().equals("")) {
                Toast.makeText(mContext, "Bạn chưa nhập biển số xe", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        RequestParams params;
        params = new RequestParams();
        params.put("carNumber", edtCarRegister.getText().toString());
        params.put("fDate",txtDateFrom.getText().toString());
        params.put("tDate",txtDateTo.getText().toString());
        params.put("isDetail",isDetail.isChecked());
        Log.e("TAG",params.toString());
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().get(Defines.URL_STATISTIC, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtCarRegister.getWindowToken(), 0);
                if(isDetail.isChecked()) {
                    headerStatistic.setVisibility(View.VISIBLE);
                    layoutOverall.setVisibility(View.GONE);
                    arrayStatistic = new ArrayList<>();
                    try {
                        JSONArray arrayresult = new JSONArray(new String(responseBody));
                        for (int i = 0; i < arrayresult.length(); i++) {
                            JSONObject jsonobject = arrayresult.getJSONObject(i);
                            parseJsonResult(jsonobject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (arrayStatistic.size() > 0) {
                        headerStatistic.setVisibility(View.VISIBLE);
                        txtNoStatistic.setVisibility(View.GONE);
                        listStatistic.setVisibility(View.VISIBLE);
                        StatisticAdapter adapter = new StatisticAdapter(mContext, arrayStatistic);
                        listStatistic.setAdapter(adapter);
                    } else {
                        headerStatistic.setVisibility(View.GONE);
                        txtNoStatistic.setVisibility(View.VISIBLE);
                        listStatistic.setVisibility(View.GONE);
                    }

                }else{
                    arrayStatisticNoDetail = new ArrayList<>();
                    headerStatistic.setVisibility(View.GONE);
                    layoutOverall.setVisibility(View.VISIBLE);
                    showResultOverall(responseBody);
                    StatisticNoDetailAdapter adapter = new StatisticNoDetailAdapter(mContext, arrayStatisticNoDetail);
                    listStatistic.setAdapter(adapter);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                dialog.dismiss();
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResultOverall(byte[] responseBody) {
        try {
            JSONArray arrayresult = new JSONArray(new String(responseBody));
            for (int i = 0; i < arrayresult.length(); i++) {
                JSONObject jsonobject = arrayresult.getJSONObject(i);
                int count         = jsonobject.getInt("count");
                Double sum        = jsonobject.getDouble("sum");
                String car = jsonobject.getString("carNum");
                StatisticNoDetail sta = new StatisticNoDetail(car, sum,count);
                arrayStatisticNoDetail.add(sta);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonResult(JSONObject jsonobject) {
        try {
            int id              = jsonobject.getInt("id");
            String type         = jsonobject.getString("type");
            String sDate        = jsonobject.getString("date");
            int money           = jsonobject.getInt("money");
            String note         = jsonobject.getString("note");

            long date = Long.parseLong(sDate.substring(sDate.lastIndexOf("(")+1,sDate.lastIndexOf(")")));

            Statistic sta = new Statistic(id,type,date,money,note);
            arrayStatistic.add(sta);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDatePicker(final TextView txtDate) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                txtDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        fromDatePickerDialog.show();
    }
}

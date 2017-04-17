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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import grab.com.thuexetoancau.Controller.OweAdapter;
import grab.com.thuexetoancau.Controller.SalaryAdapter;
import grab.com.thuexetoancau.Model.Owe;
import grab.com.thuexetoancau.Model.Salary;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SimpleDividerItemDecoration;

public class OweActivity extends AppCompatActivity {
    private AutoCompleteTextView edtCarRegister;
    private Context mContext;
    private TextView txtDateFrom, txtDateTo, txtNoInfo;
    private RecyclerView listOwes;
    private ArrayList<Owe> arrayOwes;
    private Button btnSearch;
    private LinearLayout headerOwe;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owe);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        txtDateFrom = (TextView) findViewById(R.id.btn_date_from);
        txtDateTo = (TextView) findViewById(R.id.btn_date_to);
        listOwes = (RecyclerView) findViewById(R.id.list_owes);
        edtCarRegister = (AutoCompleteTextView) findViewById(R.id.car_name);
        btnSearch = (Button) findViewById(R.id.btn_search);
        headerOwe = (LinearLayout) findViewById(R.id.header_owe);
        txtNoInfo = (TextView) findViewById(R.id.txt_no_info);
        imgBack = (ImageView) findViewById(R.id.btn_back);

        listOwes.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        listOwes.setLayoutManager(llm);
        listOwes.addItemDecoration(new SimpleDividerItemDecoration(this));


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
        params.put("keyword", s);
        Log.e("TAG", params.toString());
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, arrayCar);
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
        if (edtCarRegister.getText().toString().equals("")) {
            Toast.makeText(mContext, "Bạn chưa nhập biển số xe", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("carNumber", edtCarRegister.getText().toString());
        params.put("fDate", txtDateFrom.getText().toString());
        params.put("tDate", txtDateTo.getText().toString());
        Log.e("TAG", params.toString());
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().get(Defines.URL_OWE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtCarRegister.getWindowToken(), 0);
                headerOwe.setVisibility(View.VISIBLE);
                arrayOwes = new ArrayList<>();
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        JSONObject jsonobject = arrayresult.getJSONObject(i);
                        parseJsonResult(jsonobject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (arrayOwes.size() > 0) {
                    headerOwe.setVisibility(View.VISIBLE);
                    txtNoInfo.setVisibility(View.GONE);
                    listOwes.setVisibility(View.VISIBLE);
                    OweAdapter adapter = new OweAdapter(mContext, arrayOwes);
                    listOwes.setAdapter(adapter);
                } else {
                    headerOwe.setVisibility(View.GONE);
                    txtNoInfo.setVisibility(View.VISIBLE);
                    listOwes.setVisibility(View.GONE);
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

    private void parseJsonResult(JSONObject jsonobject) {
        try {
            int id = jsonobject.getInt("id");
            String carNumber = jsonobject.getString("car_number");
            String driverName = jsonobject.getString("driver_name");
            int moneyMonth = jsonobject.getInt("money_month");
            String dateMonth = jsonobject.getString("date_month");
            int moneyPeriod = jsonobject.getInt("money_period");
            String datePeriod = jsonobject.getString("date_period");
            int moneyYear = jsonobject.getInt("money_year");
            String dateYear = jsonobject.getString("date_year");
            String dateTime = jsonobject.getString("date_time");


            Owe sta = new Owe(id, carNumber, driverName, moneyMonth, dateMonth, moneyPeriod, datePeriod, moneyYear, dateYear, dateTime);
            arrayOwes.add(sta);

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

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        fromDatePickerDialog.show();
    }
}

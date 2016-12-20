package grab.com.thuexetoancau.UI.Activity.Driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Together.SelectRoleActivity;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;

public class LoginDriverActivity extends AppCompatActivity {
    private SharePreference preference;
    private Context mContext;
    private EditText edtPhone, edtPass;
    private TextInputLayout newPhone, newPass;
    private TextView txtRegister;
    private Button btnLogin;
    private ImageView btnBack;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);
        preference = new SharePreference(this);
        mContext = this;
        initComponents();
    }
    private void initComponents() {
        // Toolbar toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        edtPhone                = (EditText)            findViewById(R.id.edt_phone);
        edtPass                 = (EditText)            findViewById(R.id.edt_pass);
        newPhone                = (TextInputLayout)     findViewById(R.id.new_phone);
        newPass                 = (TextInputLayout)     findViewById(R.id.new_pass);
        btnLogin                = (Button)              findViewById(R.id.btn_login);
        txtRegister             = (TextView)            findViewById(R.id.txt_register);
        btnBack                 = (ImageView)           findViewById(R.id.btn_back);

        txtRegister.setPaintFlags(txtRegister.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        btnLogin.setOnClickListener(login_click_listener);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterDriverActivity.class);
                startActivity(intent);
                finish();
            }
        });
       btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(mContext, SelectRoleActivity.class);
               startActivity(intent);
               finish();
           }
       });

    }
    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            letsLogin();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(mContext, SelectRoleActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private void letsLogin() {
        String phone = edtPhone.getText().toString();
        String pass = edtPass.getText().toString();
        newPhone.setError("");
        newPass.setError("");
        if (phone == null || phone.equals("")) {
            newPhone.setError("Hãy nhập số điện thoại của bạn");
            requestFocus(edtPhone);
            return;
        }

        if (pass == null || pass.equals("")) {
            newPass.setError("Hãy nhập password của bạn");
            requestFocus(edtPass);
            return;
        }
        requestLogin(phone, pass);
    }

    private void requestLogin(final String phone, String pass) {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", phone);
        params.put("pass",pass);
        Log.i("params deleteDelivery", params.toString());
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
        BaseService.getHttpClient().post(Defines.URL_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                dialog.dismiss();
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    if (data.length() == 0) {
                        Toast.makeText(mContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(mContext, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        saveVehicleInfor(jsonobject);
                        Intent intent = new Intent(mContext,ListDriverAuctionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
    private void saveVehicleInfor(JSONObject result) {
        preference.saveRole(1);
        try {

            int id      = result.getInt("id");
            preference.saveDriverId(id);
            String name         = result.getString("name");
            preference.saveName(name);
            String phone        = result.getString("phone");
            preference.savePhone(phone);

            String  carNumber   = result.getString("car_number");
            preference.saveCarNumber(carNumber);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

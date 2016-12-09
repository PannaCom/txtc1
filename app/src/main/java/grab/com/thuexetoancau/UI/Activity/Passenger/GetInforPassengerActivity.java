package grab.com.thuexetoancau.UI.Activity.Passenger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.SharePreference;

public class GetInforPassengerActivity extends AppCompatActivity {

    private EditText edtName, edtPass;
    private TextInputLayout newName, newPass;
    private Button btnLogin;
    private ProgressDialog dialog;
    private TextView txtRegister;
    private RelativeLayout root;
    private SharePreference preference;
    private Context mContext;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preference = new SharePreference(this);
        mContext = this;
        initComponents();

    }

    private void initComponents() {
       // Toolbar toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        edtName                 = (EditText)            findViewById(R.id.edt_name);
        edtPass                 = (EditText)            findViewById(R.id.edt_pass);
        newName                 = (TextInputLayout)     findViewById(R.id.new_name);
        newPass                 = (TextInputLayout)     findViewById(R.id.new_pass);
        btnLogin                = (Button)              findViewById(R.id.btn_login);
        root                    = (RelativeLayout)      findViewById(R.id.root);
        imgBack                 = (ImageView)           findViewById(R.id.btn_back);
        btnLogin.setOnClickListener(login_click_listener);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void letsLogin() {
        newName.setError(null);
        newPass.setError(null);
        String name = edtName.getText().toString();
        String phone = edtPass.getText().toString();

        if (name == null || name.equals("")) {
            newName.setError("Hãy nhập tên của bạn");
            requestFocus(edtName);
            return;
        }

        if (phone == null || phone.equals("")) {
            newPass.setError("Hãy nhập số điện thoại của bạn");
            requestFocus(edtPass);
            return;
        }
        requestLogin(name, phone);
    }

    private void requestLogin(final String name, final String phone) {
        preference.saveName(name);
        preference.savePhone(phone);
        preference.saveRole(2);
        Intent intent = new Intent(this, PassengerSelectActionActivity.class);
        startActivity(intent);
        finish();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

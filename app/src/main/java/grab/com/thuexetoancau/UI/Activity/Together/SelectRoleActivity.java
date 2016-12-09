package grab.com.thuexetoancau.UI.Activity.Together;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.UI.Activity.Driver.LoginDriverActivity;
import grab.com.thuexetoancau.UI.Activity.Passenger.GetInforPassengerActivity;
import grab.com.thuexetoancau.Utilities.SharePreference;

public class SelectRoleActivity extends AppCompatActivity {
    private Button btnPassenger, btnDriver;
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);
        preference = new SharePreference(this);
        initComponents();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initComponents() {
        btnPassenger = (Button) findViewById(R.id.btn_passenger);
        btnDriver = (Button) findViewById(R.id.btn_driver);

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, GetInforPassengerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, LoginDriverActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

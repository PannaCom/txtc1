package grab.com.thuexetoancau.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import grab.com.thuexetoancau.R;

public class SelectRoleActivity extends AppCompatActivity {
    private Button btnPassenger, btnDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);
        initComponents();
    }

    private void initComponents() {
        btnPassenger = (Button) findViewById(R.id.btn_passenger);
        btnDriver = (Button) findViewById(R.id.btn_driver);

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, GetInforPassengerActivity.class);
                startActivity(intent);
            }
        });

        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, LoginDriverActivity.class);
                startActivity(intent);
            }
        });
    }
}

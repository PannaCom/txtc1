package grab.com.thuexetoancau.UI.Activity.Passenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import grab.com.thuexetoancau.R;

public class PassengerSelectActionActivity extends AppCompatActivity {
    private Button btnBooking, btnInfor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);


        btnBooking  = (Button)      findViewById(R.id.btn_booking);
        btnInfor    = (Button)      findViewById(R.id.btn_infor);

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, FormPassengerBookingActivity.class);
                startActivity(intent);
            }
        });

        btnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, ListPassengerBookingActivity.class);
                startActivity(intent);
            }
        });

        ImageView btnBack           =   (ImageView)             findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

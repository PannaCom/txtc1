package grab.com.thuexetoancau.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.Utilities;

public class BookingResultActivity extends AppCompatActivity {
    private TextView txtMaxPrice, txtDistance, txtPriceBasis, txtPricePerDay;
    private String result;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_result);
        initComponents();
    }

    private void initComponents() {
        txtMaxPrice     = (TextView) findViewById(R.id.txt_max_price);
        txtDistance     = (TextView) findViewById(R.id.txt_distance);
        txtPriceBasis   = (TextView) findViewById(R.id.txt_price_basis);
        txtPricePerDay  = (TextView) findViewById(R.id.txt_price_per_day);

        btnBack         = (ImageView)findViewById(R.id.btn_back);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            result = (String) extras.getString("RESULT");
        }
        String[] arrResult = result.split("_");

        txtMaxPrice.setText(Utilities.convertCurrency(Integer.valueOf(arrResult[0]))+" VNĐ");
        txtDistance.setText(arrResult[1]+ "km");
        txtPriceBasis.setText(Utilities.convertCurrency(Integer.valueOf(arrResult[2]))+" VNĐ");
        txtPricePerDay.setText(Utilities.convertCurrency(Integer.valueOf(arrResult[3]))+" VNĐ");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingResultActivity.this, PassengerBookingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

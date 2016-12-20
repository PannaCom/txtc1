package grab.com.thuexetoancau.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import grab.com.thuexetoancau.Model.BookingObject;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.Utilities.BaseService;
import grab.com.thuexetoancau.Utilities.Defines;
import grab.com.thuexetoancau.Utilities.SharePreference;
import grab.com.thuexetoancau.Utilities.Utilities;

/**
 * Created by DatNT on 11/17/2016.
 */
public class BookingCarAdapter extends RecyclerView.Adapter<BookingCarAdapter.ViewHolder>{


    private static final String LOG_TAG = BookingCarAdapter.class.getSimpleName();
    private Context mContext;
    private List<BookingObject> mVehicle;
    private SharePreference preference;
    private int price;
    private HashMap<TextView,CountDownTimer> counters;
    private onClickListener onClick;
    private onPayMoneyListener onSuccess;
    private int  money;

    public BookingCarAdapter(Context context, ArrayList<BookingObject> vehicle, int money) {
        mContext = context;
        this.mVehicle = vehicle;
        preference = new SharePreference(mContext);
        this.counters = new HashMap<>();
        this.money = money;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_car_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtCarFrom.setText(mVehicle.get(position).getCarFrom());
        holder.txtCarTo.setText(mVehicle.get(position).getCarTo());
        holder.txtHireType.setText(mVehicle.get(position).getCarHireType());

        DateTime jDateFrom = new DateTime(mVehicle.get(position).getFromDate());

        holder.txtDateFrom.setText(Utilities.convertTime(jDateFrom));
        DateTime jDateTo = new DateTime(mVehicle.get(position).getToDate());
        holder.txtDateTo.setText(Utilities.convertTime(jDateTo));
        holder.txtBookPrice.setText(Utilities.convertCurrency(mVehicle.get(position).getBookPrice())+" Đ");
        if (mVehicle.get(position).getCurrentPrice() == 0)
            holder.txtMaxPrice.setText(Utilities.convertCurrency(mVehicle.get(position).getBookPriceMax())+" Đ");
        else
            holder.txtMaxPrice.setText(Utilities.convertCurrency(mVehicle.get(position).getCurrentPrice())+" Đ");
     //   holder.txtTimeReduce.setText(mVehicle.get(position).getTimeToReduce());

        holder.txtCarSize.setText(mVehicle.get(position).getCarType() +" chỗ");

        DateTime lastDay = new DateTime(mVehicle.get(position).getFromDate());
        DateTime now = new DateTime();

        final TextView tv = holder.txtTimeReduce;

        CountDownTimer cdt = counters.get(holder.txtTimeReduce);
        if(cdt!=null)
        {
            cdt.cancel();
            cdt=null;
        }

        //int days = Days.daysBetween(lastDay.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays();
        //int minutes = Minutes.minutesBetween(lastDay,now).getMinutes();
        long diffInMillis = 0;
        if (mVehicle.get(position).getBookPrice() > Defines.BOUNDER_TRIP_PRICE)
            diffInMillis = lastDay.getMillis() - now.getMillis()-Defines.TIME_BEFORE_AUCTION_LONG;
        else
            diffInMillis = lastDay.getMillis() - now.getMillis()-Defines.TIME_BEFORE_AUCTION_SHORT;

        cdt = new CountDownTimer(diffInMillis, 1000) {

            public void onTick(final long millisUntilFinished) {
                if (millisUntilFinished > 0) {
                    String timeRemaining ="";

                    int hour = (int) ((millisUntilFinished / (1000 * 60*60)) % 60);
                    if (hour >=10)
                        timeRemaining += hour;
                    else
                        timeRemaining += "0"+hour;

                    timeRemaining+=":";

                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    if (minutes >=10)
                        timeRemaining += minutes;
                    else
                        timeRemaining += "0"+minutes;

                    timeRemaining+=":";
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    if (seconds >=10)
                        timeRemaining += seconds;
                    else
                        timeRemaining += "0"+seconds;

                    tv.setText(timeRemaining );
                } else {
                    whoWinDriver(position);
                }
            }

            public void onFinish() {
                whoWinDriver(position);
            }

        };
        counters.put(tv, cdt);
        cdt.start();
        holder.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                price = mVehicle.get(position).getBookPrice();
                if (price > Integer.valueOf(money)){
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
                    alert.setTitle("Thông báo");
                    alert.setMessage("Bạn không đủ tiền mua chuyến này");
                    alert.setCancelable(false);
                    alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    return;
                }
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn chắc chắn muốn mua chuyến xe này");
                alert.setCancelable(false);
                alert.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        purchaseTrip(1, position, null, null);
                    }
                });
                alert.show();

            }
        });
        holder.btnCompetitive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPurchase(position, tv);
            }
        });


    }

    private void showDialogPurchase(final int position, final TextView tv) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.price_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final TextView edtCode = (TextView) dialog.findViewById(R.id.edt_code);
        Button btnSet = (Button) dialog.findViewById(R.id.btn_set);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final ArrayList<String> arrPrice = new ArrayList<>();
        final ArrayList<Integer> realPrice = new ArrayList<>();
        int maxPrice = 0;
        if (mVehicle.get(position).getCurrentPrice() == 0)
            maxPrice = mVehicle.get(position).getBookPriceMax();
        else
            maxPrice = mVehicle.get(position).getCurrentPrice();
        for (int temp = mVehicle.get(position).getBookPrice() + 50000; temp < maxPrice; temp += 50000) {
            arrPrice.add(Utilities.convertCurrency(temp));
            realPrice.add(temp);
        }
        edtCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Chọn giá")
                        .setSingleChoiceItems(arrPrice.toArray(new CharSequence[arrPrice.size()]), -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                price = realPrice.get(which);
                                edtCode.setText(arrPrice.get(which));
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCode.getText().toString() == null || edtCode.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Bạn chưa trả giá", Toast.LENGTH_SHORT).show();
                } else {
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
                    alert.setTitle("Thông báo");
                    alert.setMessage("Bạn chắc chắn muốn đấu giá chuyến xe này");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            purchaseTrip(0, position, dialog, tv);
                        }
                    });
                    alert.show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void whoWinDriver(final int position) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_driver", preference.getDriverId());
        params.put("id_booking", mVehicle.get(position).getId());
        BaseService.getHttpClient().get(Defines.URL_WHO_WIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                if (onSuccess != null)
                    onSuccess.onSuccess();
                String result = new String(responseBody);
                final String phone = result.split("_")[0];
                int idBooking = Integer.parseInt(result.split("_")[1]);
                int totalMoney = Integer.parseInt(result.split("_")[2]);
                for (int i = 0; i < mVehicle.size(); i++) {
                    if (mVehicle.get(i).getId() == idBooking) {
                        final DateTime jDateFrom = new DateTime(mVehicle.get(i).getFromDate());
                        final DateTime jDateTo = new DateTime(mVehicle.get(i).getToDate());

                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
                        alert.setTitle("Thông tin chuyến xe đấu giá thành công");
                        alert.setMessage("Điểm đi: " + mVehicle.get(i).getCarFrom() + "\nĐiểm đến: " +
                                mVehicle.get(i).getCarTo() +  "\n Ngày giờ đến: " + Utilities.convertTime(jDateFrom) +
                                "\nNgày giờ về: " + Utilities.convertTime(jDateTo) +
                                "\nSố tiền còn lại trong tài khoản: "+ totalMoney +
                                "\nSố điện thoại của hành khách: "+ phone);
                        alert.setCancelable(false);
                        alert.setPositiveButton("Gọi ngay cho hành khách", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= 22) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                                        return;
                                    }
                                }
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phone));
                                mContext.startActivity(callIntent);
                            }
                        });
                        alert.show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }

    private void purchaseTrip(final int type, final int position, final Dialog rootDialog, final TextView tv) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_driver", preference.getDriverId());
        params.put("driver_number", preference.getCarNumber());
        params.put("driver_phone", preference.getPhone());
        params.put("price", price);
        params.put("id_booking", mVehicle.get(position).getId());
        params.put("type", type);
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        if (type == 0) {
            dialog.setMessage("Đang đấu giá...");
        } else
            dialog.setMessage("Đang trả giá...");
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_BOOKING_FINAL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result > 0)
                    notifyDataSetChanged();
                if (type == 0) {
                    Toast.makeText(mContext, "Đã đấu giá thành công, xin chờ kết quả sau khi hết phiên đấu giá này", Toast.LENGTH_SHORT).show();
                    Handler handlerRun = new Handler();
                    handlerRun.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            whoWinDriver(position);
                        }
                    }, Utilities.changeTimeFromStringToInt(tv.getText().toString()));
                } else {
                    Toast.makeText(mContext, "Đã mua thành công", Toast.LENGTH_SHORT).show();
                    whoWinDriver(position);
                }

                if (rootDialog != null)
                    rootDialog.dismiss();
                if (onClick != null)
                    onClick.onItemClick();
                else
                    Toast.makeText(mContext, "Giao dịch thất bại", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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

    private void payMoney(int id_booking) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_driver",preference.getDriverId());
        params.put("id_booking",id_booking);
        BaseService.getHttpClient().post(Defines.URL_CONFIRM, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (onSuccess != null)
                    onSuccess.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setOnRequestComplete(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    public void setOnPaySuccess(final onPayMoneyListener onSuccess)
    {
        this.onSuccess = onSuccess;
    }
    @Override
    public int getItemCount() {

        if (mVehicle == null) return 0;
        else return mVehicle.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void swapData(List<BookingObject> itemForMultipleSelectionList) {
        mVehicle = itemForMultipleSelectionList;
    }

    public List<BookingObject> getData() {
        if (mVehicle != null){
           return mVehicle;
        } else return null;
    };

    public class ViewHolder extends RecyclerView.ViewHolder{


        CardView cardview;
        TextView txtCarFrom;
        TextView txtCarTo;
        TextView txtHireType;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtBookPrice;
        TextView txtMaxPrice;
        TextView txtTimeReduce;
        TextView txtCarSize;
        Button btnCompetitive, btnPurchase;

        public ViewHolder(View itemView) {
            super(itemView);


            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtCarFrom      = (TextView)        itemView.findViewById(R.id.txt_from);
            txtCarTo        = (TextView)        itemView.findViewById(R.id.txt_to);
            txtHireType     = (TextView)        itemView.findViewById(R.id.txt_hire_type);
            txtDateFrom     = (TextView)        itemView.findViewById(R.id.txt_date_from);
            txtDateTo       = (TextView)        itemView.findViewById(R.id.txt_date_to);
            txtBookPrice    = (TextView)        itemView.findViewById(R.id.txt_book_price);
            txtMaxPrice     = (TextView)        itemView.findViewById(R.id.txt_book_price_max);
            txtTimeReduce   = (TextView)        itemView.findViewById(R.id.txt_time_remaining);
            txtCarSize      = (TextView)        itemView.findViewById(R.id.txt_car_size);
            btnCompetitive  = (Button)          itemView.findViewById(R.id.btn_competitive);
            btnPurchase     = (Button)          itemView.findViewById(R.id.btn_purchase);


        }
    }
    public interface onClickListener
    {
        public void onItemClick();

    }

    public interface onPayMoneyListener
    {
        public void onSuccess();

    }

}
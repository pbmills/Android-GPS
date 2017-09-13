package activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.text.SimpleDateFormat;

import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rush.rushdigital.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapter.MainActivityAdapter;
import Bean.MainActivityVo;
import localStorage.DataBaseHelper;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private Paint paint;
    private ArrayList<MainActivityVo> mainActivityVoArrayList = new ArrayList<MainActivityVo>();
    private MainActivityAdapter mainActivityAdapter = new MainActivityAdapter(MainActivity.this, mainActivityVoArrayList);
    public String time, Address, lat, lon;
    public MainActivityVo mainActivityVo = new MainActivityVo();
    private ListView listView;
    private TextView tv_add, tv_text, tv_DIALOG, tv_tim, tv_location, tv_lat, tv_lon, tv_swipe;
    private Button btn_getlocation;
    private DataBaseHelper dataBaseHelper;
    public double latitude, longitude;
    private Toast toast;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Cursor cursor;
    private Handler handler;
    private LatLng loc;
    private AlertDialog alertDialog;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        paint = new Paint();
        dataBaseHelper = new DataBaseHelper(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        time = dateFormat.format(new Date());
        Log.e("time", "" + time);

        listView = (ListView) findViewById(R.id.list_address);
        cursor = dataBaseHelper.GetData();
        {
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        mainActivityVo = new MainActivityVo();
                        mainActivityVo.setTime(cursor.getString(1));
                        mainActivityVo.setAddress(cursor.getString(2));

                    } while (cursor.moveToNext());
                }
            }
        }
        cursor.close();
        mainActivityVoArrayList.add(mainActivityVo);
        listView.setAdapter(mainActivityAdapter);
        btn_getlocation = (Button) findViewById(R.id.btn_getlocation);
        btn_getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    Log.e("latitude", ">" + latitude);
                                    Log.e("longitude", ">" + longitude);
                                    Double x = location.getLatitude();
                                    Double y = location.getLongitude();
                                    lat = x.toString();
                                    lon = y.toString();
                                    onMapReady(mMap);
                                    Log.e("lat", ">" + lat);
                                    Log.e("lon", ">" + lon);

                                    try {
                                        Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext(), Locale.getDefault());
                                        List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                                        if (addresses.isEmpty()) {
                                            Toast.makeText(getApplicationContext(), "Not Found:- ", Toast.LENGTH_LONG).show();
                                        } else {
                                            if (addresses.size() > 0) {
                                                Address = addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality();
                                                Log.e("Address", ">" + Address);
                                                mainActivityVo.setAddress(Address);
                                                // Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mainActivityVo = new MainActivityVo();
                                    mainActivityVo.setTime(time);
                                    mainActivityVo.setAddress(Address);
                                    dataBaseHelper.InsertData(mainActivityVo);

                                    String Allrows = dataBaseHelper.getTableAsString();
                                    mainActivityVoArrayList.add(mainActivityVo);
                                    mainActivityAdapter = new MainActivityAdapter(MainActivity.this, mainActivityVoArrayList);
                                    listView.setAdapter(mainActivityAdapter);
                                    ((MainActivityAdapter) listView.getAdapter()).notifyDataSetChanged();

                                   /* cursor = dataBaseHelper.GetData();
                                    {

                                        if (cursor != null) {
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    mainActivityVoArrayList = new ArrayList<MainActivityVo>();

                                                   // for (int i = 0; i < mainActivityVoArrayList.size(); i++) {

                                                        mainActivityVo.setTime(cursor.getString(1));
                                                        mainActivityVo.setAddress(cursor.getString(2));

                                                   // }
                                                    mainActivityVoArrayList.add(mainActivityVo);
                                                    mainActivityAdapter = new MainActivityAdapter(MainActivity.this, mainActivityVoArrayList);
                                                    listView.setAdapter(mainActivityAdapter);

                                                }
                                                while (cursor.moveToNext());
                                            }

                                        }
                                    }
                                    cursor.close();
*/
                                }
                            }
                        });
                FirstDialog();
                handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        Dialog();
                    }
                }, 6000);

            }

        });

    }

    public void FirstDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.raw_first_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_DIALOG = (TextView) dialogView.findViewById(R.id.tv_DIALOG);
        tv_DIALOG.setGravity(Gravity.CENTER);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, 6000);

    }

    public void Dialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.raw_dailog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) dialogView.findViewById(R.id.swipeToRefresh);
        tv_add = (TextView) dialogView.findViewById(R.id.tv_add);
        tv_add.setText(Address);
        tv_text = (TextView) dialogView.findViewById(R.id.tv_text);
        tv_tim = (TextView) dialogView.findViewById(R.id.tv_tim);
        tv_tim.setText(time);
        tv_location = (TextView) dialogView.findViewById(R.id.tv_location);
        tv_lat = (TextView) dialogView.findViewById(R.id.tv_latitude);
        tv_lat.setText(lat);
        tv_lon = (TextView) dialogView.findViewById(R.id.tv_longitude);
        tv_lon.setText(lon);
        tv_swipe = (TextView) dialogView.findViewById(R.id.tv_swipe);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                alertDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);

            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            loc = new LatLng(latitude, longitude);
                            Log.e("Location", "" + loc);

                            Bitmap.Config config = Bitmap.Config.ARGB_8888;
                            Bitmap bmp = Bitmap.createBitmap(200, 50, config);
                            Canvas canvas = new Canvas(bmp);
                            canvas.drawText("TEXT", 0, 50, paint);

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }

                            mMap.setMyLocationEnabled(true);
                            mMap.addMarker(new MarkerOptions().position(loc).title("Your Destination ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }

                });
    }
}
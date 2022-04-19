package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.annotation.SuppressLint;
import android.content.ContentValues;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    EditText nameTxt, descriptionTxt;
    Button addBtn,deleteBtn;
    LocationManager locationManager;
    String tempLat,tempLongi;


    GpsTracker gpsTracker;

    int REQUEST_CODE_LOCATION=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = findViewById(R.id.nameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        addBtn = findViewById(R.id.addBtn);
        deleteBtn=findViewById(R.id.deleteBtn);





        //runtime permission for accessing

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameTxt.getText().toString();
                String description=descriptionTxt.getText().toString();
                getLocation(name,description);
            }


        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int uri=getContentResolver().delete(MyContentProvider.CONTENT_URI,null,null);

                Toast.makeText(MainActivity.this, "deleted", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @SuppressLint("Range")
    public void onClickShowDetails(View view) {
        // inserting complete table details in this text field

        TextView resultView= (TextView) findViewById(R.id.res);

        // creating a cursor object of the
        // content URI
        Cursor cursor = MainActivity.this.getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null, null);

        // iteration of the cursor
        // to print whole table
        if(cursor.moveToFirst()) {
            StringBuilder strBuild=new StringBuilder();
            while (!cursor.isAfterLast()) {
                strBuild.append("\n"+cursor.getString(cursor.getColumnIndex("id"))+ "-"+ cursor.getString(cursor.getColumnIndex("name"))+"-"+cursor.getString(cursor.getColumnIndex("lat")) + "-"+ cursor.getString(cursor.getColumnIndex("longi")));
                cursor.moveToNext();
            }
            resultView.setText(strBuild);
        }
        else {
            resultView.setText("No Records Found");
        }
    }



    private void getLocation(String name, String description) {
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            tempLat=String.valueOf(latitude);
            tempLongi=String.valueOf(longitude);

            Toast.makeText(MainActivity.this, tempLat+" "+tempLongi, Toast.LENGTH_SHORT).show();

                ContentValues values = new ContentValues();

                // fetching text from user
                values.put(MyContentProvider.name, name);
                values.put(MyContentProvider.description,description);
                values.put(MyContentProvider.lat,tempLat);
                values.put(MyContentProvider.longi,tempLongi);

                // inserting into database through content URI
                Uri uri = getContentResolver().insert(
                        MyContentProvider.CONTENT_URI, values);
//                 displaying a toast message
                Toast.makeText(MainActivity.this, "New Record Inserted", Toast.LENGTH_LONG).show();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
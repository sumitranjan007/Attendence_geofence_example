package com.athentech.attendence;

import static com.athentech.attendence.BuildConfig.MAPS_API_KEY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.athentech.attendence.database.GeofenceList;
import com.athentech.attendence.database.GeofenceModel;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
   private GoogleMap gMap;
   private GoogleMapOptions options;
   private Util util;
   private static final String TAG_MAIN="main_activity";
   private FusedLocationProviderClient fusedLocationProviderClient;
   private String[] locationPermission={Manifest.permission.ACCESS_FINE_LOCATION,
   Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION};
   ActivityResultLauncher<String[]> locationPemissionRequest=registerForActivityResult(
           new ActivityResultContracts.RequestMultiplePermissions(),result->{
               Boolean fineLocationGranted=result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false);
               Boolean couraceLocationGranted=result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
               Boolean backgroundLocationGranted=result.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION,false);
               if (fineLocationGranted!=null && fineLocationGranted){
                   Log.d(TAG_MAIN,"fine location granted");
                   checkBackgroundPermission();

               }else if (couraceLocationGranted!=null && couraceLocationGranted){
                   Log.d(TAG_MAIN,"Cource location granted");
               }else if (backgroundLocationGranted!=null && backgroundLocationGranted){
                   Log.d(TAG_MAIN,"Background location granted");
                   initAllValues();
               }else{
                   Log.d(TAG_MAIN,"No  location granted");
               }

           }
   );
   private LatLng selectedLatLang;
    private GeofenceModel geofenceModel;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geofenceModel= new ViewModelProvider(MainActivity.this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(GeofenceModel.class);
        geofenceModel.getAllGeofenceList().observe(MainActivity.this, new Observer<List<GeofenceList>>() {
            @Override
            public void onChanged(List<GeofenceList> geofenceLists) {
                if (geofenceLists!=null && geofenceLists.size()>0){
                    gMap.clear();
                    for (int i=0;i<geofenceLists.size();i++){

                        drawGeofenceOnMap(geofenceLists.get(i).latitude,geofenceLists.get(i).longitude);
                    }

                }
            }
        });
       if (askPermission()) {
           message("All permission granted");
           initAllValues();
       }else{
           locationPemissionRequest.launch(new String[]{
                   Manifest.permission.ACCESS_FINE_LOCATION,
                   Manifest.permission.ACCESS_COARSE_LOCATION,
           });
       }

        FloatingActionButton addGeofenceBtn=findViewById(R.id.addFence);
       addGeofenceBtn.setOnClickListener(v->{
           if (selectedLatLang!=null){
               geofenceCreationDialog(selectedLatLang);
           }
       });


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
            gMap=googleMap;
            gMap.getUiSettings().setZoomControlsEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
            gMap.setMyLocationEnabled(true);

            gMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
                @Override
                public void onCameraMoveCanceled() {

                }
            });
            gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    selectedLatLang = gMap.getCameraPosition().target;
                    util = new Util(selectedLatLang, MainActivity.this);
                    try {
                        message(util.getAddress());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG_MAIN, e.getMessage());
                    }
                    Log.d(TAG_MAIN, "OnCameraIdle()-> Latitude " + selectedLatLang + " longitude " + selectedLatLang.longitude);
                }
            });



    }
    private void message(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
    private boolean askPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
          return false;
        }else if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return false;
        }else{
            return true;
        }
    }
    private boolean checkBackgroundPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            askForBackgroundLocation();
            return false;
        }else{
            return true;
        }
    }
    @SuppressLint("MissingPermission")
    private void initAllValues(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCamera(lng);
                }

            }
        });
        Places.initialize(this, MAPS_API_KEY);
        Places.createClient(this);
        options=new GoogleMapOptions();
        MapFragment mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }else{
            message("Map fragment is null showing please check");
        }
    }
    private void askForBackgroundLocation(){
        locationPemissionRequest.launch(new String[]{
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
        });
    }
    private void moveCamera(LatLng latLng){
      CameraPosition cameraPosition=  CameraPosition.builder()
                .target(latLng)
                .zoom(18f)
                .bearing(90f)
                .tilt(10f)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    @SuppressLint("MissingPermission")
    public void addGeofences(String geofenceID, LatLng latLng){
        GeofenceFunctions geofenceFunctions=new GeofenceFunctions(MainActivity.this);
        GeofencingClient geofencingClient= LocationServices.getGeofencingClient(MainActivity.this);
        geofencingClient.addGeofences(geofenceFunctions.requestGeofence(geofenceID,latLng,20f),geofenceFunctions.geofencingPendingIntent())
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        drawGeofenceOnMap(latLng.latitude,latLng.longitude);
                        GeofenceList g=new GeofenceList();
                        g.title=geofenceID;
                        g.latitude=latLng.latitude;
                        g.longitude=latLng.longitude;
                        geofenceModel.insert(g);
                        message("Geofence created");
                    }
                });

    }
    private void geofenceCreationDialog(LatLng latLng){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.geofence_creation_dialog,null);
        alertDialog.setView(view);
        Button create=view.findViewById(R.id.btnCreate);
        EditText idEdt=view.findViewById(R.id.idEdt);
        AlertDialog dialog=alertDialog.create();
       create.setOnClickListener(v->{
           if (idEdt.getText().toString().isEmpty()){
               message("Please enter geofence name");
           }else{
               addGeofences(idEdt.getText().toString(),latLng);
               dialog.dismiss();
           }
       });

        dialog.show();
    }
    private void drawGeofenceOnMap(double lat,double longs){
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat,longs) )
                .radius( 20 )
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        gMap.addCircle(circleOptions);
    }
}




























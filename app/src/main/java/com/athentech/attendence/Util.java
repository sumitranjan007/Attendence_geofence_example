package com.athentech.attendence;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Util {
   private LatLng latLng;
   private Context context;
   public Util(LatLng lng,Context context){
       latLng=lng;
       this.context=context;
   }
   public String getAddress() throws Exception {
       Geocoder geocoder=new Geocoder(context, Locale.getDefault());
       List<Address> addresses=new ArrayList<>();
       addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
       return addresses.get(0).getAddressLine(0);
   }
}



























package com.gionee.note.noteMedia.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.gionee.note.domain.NoteLocation;
import com.gionee.note.utils.Log;

public class GNLocateService2 {

	private static GNLocateService2 mInstance;

	private LocationManager locationManager;
	private LocationListener locationListener;

	private Context mContext;
	private GNLocationListener mGNLocationListener;
	
	private ArrayList<NoteLocation> addrArr;
	
	private double longitude;
	private double latitude;
	
	private GNLocateService2() {
	}

	private GNLocateService2(Context context) {
		mContext = context;
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		initLocationListener();
	}
	
	public static synchronized GNLocateService2 getInstance(Context context) {
		Log.d("GNLocateService2------getInstance!");
		if (mInstance == null) {
			Log.i("GNLocateService2------mInstance == null!");
			mInstance = new GNLocateService2(context);
		}
		return mInstance;
	}
	
	private void initLocationListener(){
		Log.i("GNLocateService------initLocationListener begin!");
		
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("GNLocateService------onLocationChanged!");
				
				if (location != null) {
					longitude = location.getLongitude(); // 经度
					latitude = location.getLatitude(); // 纬度
					Log.d("GNLocateService------Longitude: " + longitude	+ ", Latitude: " + latitude);
					
					new Thread(new Runnable(){
						@Override
						public void run() {
							getAddrs(longitude, latitude);
						}
					}).start();
					
				}else{
					Log.e("GNLocateService------location == null!");
					mGNLocationListener.onLocatePoi(null,0);
				}
			}
			@Override
			public void onProviderDisabled(String provider) {
				Log.i("GNLocateService------onProviderDisabled, provider: " + provider);
				mGNLocationListener.onProviderDisabled();
			}
			@Override
			public void onProviderEnabled(String provider) {
				Log.i("GNLocateService------onProviderEnabled, provider: " + provider);
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i("GNLocateService------onStatusChanged, provider: " + provider + ", status: " + status + ".");
			}
		};
		
		Log.i("GNLocateService------initLocationListener end!");
	}
	
	public void destroy() {
		Log.i("GNLocateService2------destroy begin!");
		
		releaseLocation();
		locationManager = null;
		clearInstance();
		
		Log.i("GNLocateService2------destroy end!");
	}

    private synchronized static void clearInstance() {
        mInstance = null;
    }
	
	public void requestLocation() {
		Log.i("GNLocateService2------requestLocation begin!");
		
		if(locationManager != null){
			Log.d("GNLocateService2------locationManager != null!");
			Log.v("GNLocateService2------locationManager---LocationManager.NETWORK_PROVIDER == " + LocationManager.NETWORK_PROVIDER);
			List<String> providers = locationManager.getAllProviders();
			Log.v("GNLocateService2------locationManager---providers == " + providers);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 45000, 0, locationListener);
		}else{
			Log.e("GNLocateService2------locationManager == null!");
		}
		
		Log.i("GNLocateService2------requestLocation end!");
	}
	
	public void releaseLocation(){
		Log.i("GNLocateService2------releaseLocation begin!");
		
		if(locationManager != null){
			locationManager.removeUpdates(locationListener);
		}else{
			Log.e("GNLocateService2------locationManager == null!");
		}
		
		Log.i("GNLocateService2------releaseLocation end!");
	}
	
	public interface GNLocationListener {
		void onLocatePoi(ArrayList<String> poi);
		void onLocatePoi(ArrayList<NoteLocation> poi,int temp);
		void onProviderDisabled();
	}

	public void setGNLocationListener(GNLocationListener listener) {
		mGNLocationListener = listener;
	}
	
	private void getAddrs(double longitude, double latitude){
		//Gionee liuliang 2014-5-24 modify for CR01245190 begin
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		//Gionee liuliang 2014-5-24 modify for CR01245190 end
		List<Address> listAddr = null;
	
		try{
			listAddr = geocoder.getFromLocation(latitude, longitude, 1);  
			
			if(listAddr == null || listAddr.size() <= 0){
				Log.d("GNLocateService2------listAddr == null or size <= 0!");
				mGNLocationListener.onLocatePoi(null,0);
			}else{
				Log.d("GNLocateService2------listAddr size: " + listAddr.size());

				Address addr = listAddr.get(0);

				if(addr == null){
					Log.d("GNLocateService2------addr == nul!");
					mGNLocationListener.onLocatePoi(null,0);
				}else{
					
					int maxAddressLineIndex = addr.getMaxAddressLineIndex();
					
					Log.d("GNLocateService2------MaxAddressLineIndex: " + maxAddressLineIndex);
					
					addrArr = new ArrayList<NoteLocation>();
					for(int i = 0; i<= maxAddressLineIndex; i++){
						Log.d("GNLocateService2------AddressLine: " + addr.getAddressLine(i));
						NoteLocation noteLocation = new NoteLocation();
						noteLocation.setName(addr.getAddressLine(i));
						noteLocation.setAddress(addr.getAddressLine(i));
						addrArr.add(noteLocation);
					}
					
					mGNLocationListener.onLocatePoi(addrArr,0);
				}
				
			}
		}catch(Exception e){
			Log.e("GNLocateService2------get address list error!", e);
			mGNLocationListener.onLocatePoi(null,0);
		}
		
	}
	
}

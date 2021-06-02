package com.example.weather

class LocationService : Service() {
    var locationManager: LocationManager? = null
    var listener: MyLocationListener? = null
    var previousBestLocation: Location? = null
    var intent: Intent? = null
    var counter = 0
    fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
    }

    fun onStart(intent: Intent?, startId: Int) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        listener = MyLocationListener()
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener)
    }

    fun onBind(intent: Intent?): IBinder? {
        return null
    }

    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta: Long = location.getTime() - currentBestLocation.getTime()
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.getAccuracy() - currentBestLocation.getAccuracy()) as Int
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location.getProvider(),
            currentBestLocation.getProvider()
        )

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /** Checks whether two providers are the same  */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    fun onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);     
        super.onDestroy()
        Log.v("STOP_SERVICE", "DONE")
        locationManager.removeUpdates(listener)
    }

    inner class MyLocationListener : LocationListener {
        fun onLocationChanged(loc: Location) {
            Log.i("**************************************", "Location changed")
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude()
                loc.getLongitude()
                intent.putExtra("Latitude", loc.getLatitude())
                intent.putExtra("Longitude", loc.getLongitude())
                intent.putExtra("Provider", loc.getProvider())
                sendBroadcast(intent)
                /*final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());         
            String Text = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);                
                Text = "My current location is: "+addresses.get(0).getAddressLine(0); 

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Text = "My current location is: " +"Latitude = " + loc.getLatitude() + ", Longitude = " + loc.getLongitude();  
            }
            */
                //Toast.makeText( getApplicationContext(), "Location polled to server", Toast.LENGTH_SHORT).show();
            }
        }

        fun onProviderDisabled(provider: String?) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show()
        }

        fun onProviderEnabled(provider: String?) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val BROADCAST_ACTION = "Hello World"
        private const val TWO_MINUTES = 1000 * 60 * 2
        fun performOnBackgroundThread(runnable: Runnable): Thread {
            val t: Thread = object : Thread() {
                override fun run() {
                    try {
                        runnable.run()
                    } finally {
                    }
                }
            }
            t.start()
            return t
        }
    }
}
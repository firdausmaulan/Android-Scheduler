package com.wm.example;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocationWorker extends Worker {

    private static final String DEFAULT_START_TIME = "06:00";
    private static final String DEFAULT_END_TIME = "21:00";

    public static final String TAG = "LocationWorker";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 60 * 1000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Done");
        Log.d(TAG, "onStartJob: STARTING JOB..");

        if (isOnSchedule()) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                }
            };

            try {
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    mLocation = task.getResult();
                                    Log.d(TAG, "Location : " + mLocation);
                                    showNotification();
                                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                } else {
                                    Log.d(TAG, "Failed to get location.");
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }

    private boolean isOnSchedule() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            String formattedDate = dateFormat.format(date);
            Date currentDate = dateFormat.parse(formattedDate);
            Date startDate = dateFormat.parse(DEFAULT_START_TIME);
            Date endDate = dateFormat.parse(DEFAULT_END_TIME);
            return currentDate.after(startDate) && currentDate.before(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.app_name);
            String description = mContext.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(mContext, mContext.getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("New Location Update")
                .setContentText("You are at " + mLocation.getLatitude() + "," + mLocation.getLongitude())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());
    }
}

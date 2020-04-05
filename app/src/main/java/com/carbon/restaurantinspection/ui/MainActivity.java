package com.carbon.restaurantinspection.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.UpdateDownloader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.carbon.restaurantinspection.model.Favourites.setDateList;
import static com.carbon.restaurantinspection.model.Favourites.setFavouriteList;
import static com.carbon.restaurantinspection.model.Favourites.stringToArrayList;

/** Set up the map view and ensure services from google maps API**/
public class MainActivity extends AppCompatActivity {
    // reference code from Youtuber: CodingWithMitch Playlist: Google Maps & Google Places Android Course
    //https://www.youtube.com/watch?v=urLA8z6-l3k&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=2
    //https://www.youtube.com/watch?v=M0bYvXlhgSI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=3
    private UpdateDownloader updateDownloader;
    private Button downloadButton;
    private Button cancelButton;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Dialog myDialog;
    private static Context contextOfApplication;
    private static final String FAVOURITE_PREFS = "FavouriteList";
    private static final String DATE_PREFS = "DateList";
    public static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();
        myDialog = new Dialog(this);

        getDataFromSharedPrefs();

        if (isServicesOK()) {
            Log.d("main", "sup");
            updateDownloader = new UpdateDownloader(this);
            startLoadingScreen();
            checkForUpdates();
        }
    }

    private void getDataFromSharedPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().clear().commit();
        String favouriteString = preferences.getString(FAVOURITE_PREFS, "");
        setFavouriteList(stringToArrayList(favouriteString));
        String dateString = preferences.getString(DATE_PREFS, "");
        setDateList(stringToArrayList(dateString));
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    private void setUpDownloadButton() {
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDownloader.downloadUpdates(MainActivity.this);
                final TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                final Animation rotate = AnimationUtils.loadAnimation(getApplicationContext()
                        , R.anim.rotate);
                TextView message = myDialog.findViewById(R.id.loadingMessage);
                message.setText(R.string.downloading);
                LinearLayout holder = myDialog.findViewById(R.id.buttonHolder);
                holder.removeView(downloadButton);
                cancelButton.setGravity(Gravity.CENTER);
                cancelDownload();
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadingIndicator.startAnimation(rotate);
                        if (!updateDownloader.downloadComplete() && !updateDownloader.downloadFailed()) {
                            handler.postDelayed(this, 1000);
                        } else if (updateDownloader.downloadComplete()){
                            finishDownload();
                        } else {
                            downloadFailed();
                        }
                    }
                };
                handler.post(runnable);
            }
        });
    }

    private void downloadFailed() {
        cancelButton.setText(R.string.finishButton);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.finishBlue, null));
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.downloadFailed);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        LinearLayout downloadLayout = myDialog.findViewById(R.id.downloadLayout);
        downloadLayout.removeView(loadingIndicator);
        setUpCancelButton();
    }

    private void cancelDownload() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLoadingScreen();
                updateDownloader.cancelUpdate();
            }
        });
    }

    private void finishDownload() {
        cancelButton.setText(R.string.finishButton);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.finishBlue, null));
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.finishDownload);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        LinearLayout downloadLayout = myDialog.findViewById(R.id.downloadLayout);
        downloadLayout.removeView(loadingIndicator);
        setUpCancelButton();
    }

    private void setUpCancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLoadingScreen();
            }
        });
    }

    public void checkForUpdates() {
        Handler handler = new Handler();
        if(!updateDownloader.isReady()) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                    TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
                    loadingIndicator.startAnimation(rotate);
                    checkForUpdates();
                }
            }, 1000);
        } else {
            if (updateDownloader.updatesAvailable(MainActivity.this)){
                updatesAvailable();
            } else{
                stopLoadingScreen();
            }
        }
    }

    public void updatesAvailable() {
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        loadingIndicator.setVisibility(View.INVISIBLE);
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.updatesAvailable);
        cancelButton.setVisibility(View.VISIBLE);
        downloadButton.setVisibility(View.VISIBLE);
        setUpCancelButton();
        setUpDownloadButton();
    }

    public void startLoadingScreen() {
        // used fragment tutorial from
        // https://www.youtube.com/watch?v=0DH2tZjJtm0
        myDialog.setContentView(R.layout.downloadscreen);
        downloadButton = myDialog.findViewById(R.id.downloadButton);
        cancelButton = myDialog.findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.INVISIBLE);
        downloadButton.setVisibility(View.INVISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.startAnimation(rotate);
        myDialog.show();
    }

    public void stopLoadingScreen() {
        myDialog.dismiss();
        finish();
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            myDialog.cancel();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_LONG).show();
            myDialog.cancel();
        }
        return false;
    }
}
package com.carbon.restaurantinspection.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class MainActivity extends AppCompatActivity {
    private UpdateDownloader updateDownloader;
    private Button downloadButton;
    private Button cancelButton;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Dialog myDialog;

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);
        if (isServicesOK()) {
            Log.d("main", "sup");
            updateDownloader = new UpdateDownloader(this);
            startLoadingScreen();
            checkForUpdates();
        }
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

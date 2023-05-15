package isuru.ic.com.callrecorder;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class MainActivity extends AppCompatActivity {
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private static final int REQUEST_CODE = 22;
    SampleAlarmReceiver alarm = new SampleAlarmReceiver();
    private static final int PERMISSION_ALL = 1;
    private static final int MY_PERMISSIONS_REQUEST =2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdminDemo.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        alarm.setAlarm(this);
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        }
        else {

            Intent intent3 = new Intent(MainActivity.this, TService.class);

            startService(intent3);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {




        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent3 = new Intent(MainActivity.this, TService.class);
                    startService(intent3);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {




                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }
}

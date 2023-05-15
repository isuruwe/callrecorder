package isuru.ic.com.callrecorder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Isuru on 7/8/2017.
 */
public class autostart extends BroadcastReceiver
{ @Override
public void onReceive(Context context, Intent arg1)
{


    Intent intent2 = new Intent(context, TService.class);
    context.startService(intent2);

    Log.d("Autostart", "started");
}



}
package isuru.ic.com.callrecorder;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

/**
 * Created by Isuru on 5/12/2018.
 */
public class TService extends Service {
    MediaRecorder recorder;
    File audiofile;
    String name, phonenumber;
    String audio_format;
    public String Audio_Type;
    int audioSource;
    Context context;
    private Handler handler;
    Timer timer;
    Boolean offHook = false, ringing = false;
    Toast toast;
    Boolean isOffHook = false;
    private boolean recordstarted = false;

    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    private CallBr br_call;




    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("service", "destroy");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // final String terminate =(String)
        // intent.getExtras().get("terminate");//
        // intent.getStringExtra("terminate");
        // Log.d("TAG", "service started");
        //
        // TelephonyManager telephony = (TelephonyManager)
        // getSystemService(Context.TELEPHONY_SERVICE); // TelephonyManager
        // // object
        // CustomPhoneStateListener customPhoneListener = new
        // CustomPhoneStateListener();
        // telephony.listen(customPhoneListener,
        // PhoneStateListener.LISTEN_CALL_STATE);
        // context = getApplicationContext();
        Intent notificationIntent = new Intent(this, TService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 22, notificationIntent, 0);

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification = new Notification.Builder(this)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText(getText(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.app_name))
                        .build();
            }
        }

      //  startForeground(222, notification); // for new
        //old

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(222, notification);
        }
        else{
            Notification note = new Notification( 0, null, System.currentTimeMillis() );
            note.flags |= Notification.FLAG_NO_CLEAR;
            startForeground( 222, note );
        }


        /////
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My1:1987");
        wl.acquire();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OUT);
        filter.addAction(ACTION_IN);
        this.br_call = new CallBr();
        this.registerReceiver(this.br_call, filter);
        return Service.START_STICKY;




        // if(terminate != null) {
        // stopSelf();
        // }

    }

    public class CallBr extends BroadcastReceiver {
        Bundle bundle;
        String state;
        String inCall, outCall;
        public boolean wasRinging = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_IN)) {
                if ((bundle = intent.getExtras()) != null) {
                    state = bundle.getString(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        wasRinging = true;
                        //Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                        Log.d("CALLINC","DFFDFDF");
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        if (wasRinging == true) {

                            Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/Dasa1");
                            if (!sampleDir.exists()) {
                                sampleDir.mkdirs();
                            }

                            String pno=    getContactName(TService.this,bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                            String file_name = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)+"-"+pno+"-"+out;
                            try {
                                audiofile = File.createTempFile(file_name, ".amr", sampleDir);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if(recorder!=null){ //add this check
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                recorder.reset();
                                recorder.release();
                                recorder=null;
                            }
                            recorder = new MediaRecorder();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                            }
                            else {
                                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            }
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            recorder.setOutputFile(audiofile.getAbsolutePath());
                            try {
                                recorder.prepare();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            recorder.start();
                            recordstarted = true;
                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        wasRinging = false;
                        Toast.makeText(context, "END", Toast.LENGTH_LONG).show();
                        if (recordstarted) {
                            recorder.stop();
                            recordstarted = false;
                            audiofile.setWritable(false, false);
                            audiofile.setReadable(true, false);
                        }
                    }
                }
            } else if (intent.getAction().equals(ACTION_OUT)) {
                if ((bundle = intent.getExtras()) != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    wasRinging = true;
                    //Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
                    Log.d("CALLOUT","FDF");
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (wasRinging == true) {

                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/Dasa1");
                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }


                        String file_name = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)+"Record"+out;
                        try {
                            audiofile = File.createTempFile(file_name, ".amr", sampleDir);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if(recorder!=null){ //add this check
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            recorder.reset();
                            recorder.release();
                            recorder=null;
                        }
                        recorder = new MediaRecorder();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        }
                        else {
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        }

                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(audiofile.getAbsolutePath());
                        try {
                            recorder.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recorder.start();
                        recordstarted = true;
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    wasRinging = false;
                    Toast.makeText(context, "REJECT ", Toast.LENGTH_LONG).show();
                    if (recordstarted) {
                        recorder.stop();
                        recordstarted = false;
                        audiofile.setWritable(false, false);
                        audiofile.setReadable(true, false);
                    }
                }



            }
        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


}
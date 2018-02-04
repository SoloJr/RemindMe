package com.android.remindmeapp;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {

    private Button btnAddActivity, btnDeleteActivity;

    private int increment = 1;

    /**
     * Activities
     */
    private List<Activity> activities;

    /**
     * Activities shown on UI
     */
    private List<String> activitiesOnListView;

    /**
     * The key used for saving the list of activities.
     */
    private final String ACTIVITIES_LIST = "activity_list";

    /**
     * This object is used for saving the activities.
     */
    private SharedPreferences sharedPreferences;

    /**
     * Text field in the UI for the name of the activity
     */
    private EditText notificationName;

    /**
     * Text filed in the UI for the minutes interval of the activity
     */
    private EditText notificationInterval;

    private boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddActivity = (Button) findViewById(R.id.button);
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotification();
            }
        });

        btnDeleteActivity = (Button) findViewById(R.id.buttonDelete);
        btnDeleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotification();
            }
        });

        notificationName = (EditText)findViewById(R.id.notificationName);
        notificationInterval = (EditText)findViewById(R.id.intervalNumber);

        //1. get activities from memory and show on UI
        getActivities();

        //set activities for UI & add notif
        activitiesOnListView = new ArrayList<String>();
        for (Activity ac:activities)
        {
            ac.setPosition(-1);
            activitiesOnListView.add(ac.toString());
            addScheduler(ac);
        }

        //2. show activities
        showActivitiesOnUI();
    }

    class MyTimerTask extends TimerTask {

        String message;

        public MyTimerTask(String message) {
            this.message = message;
        }

        public void run() {

            generateNotification(getApplicationContext(), this.message);
        }
    }

    /**
     * Edit activity on click
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String item = (String) getListAdapter().getItem(position);
        String[] splited = item.split("-");

        this.notificationName.setText(splited[0]);
        this.notificationInterval.setText(splited[1]);

        for(Activity act : activities)
        {
            if(act.getName().contentEquals(splited[0])){
                act.setPosition(position);
            }
            else {
                act.setPosition(-1);
            }
        }
    }

    private void deleteNotification(){
        for(int i = 0; i< activities.size(); i++) {
            Activity act = activities.get(i);
            if (act.getName().contentEquals(this.notificationName.getText())) {
                activities.remove(i);
                activitiesOnListView.remove(act.toString());

            }
        }

        saveActivities();
        getActivities();
        showActivitiesOnUI();

        notificationName.getText().clear();
        notificationInterval.getText().clear();
    }

    private void generateNotification(Context context, String message) {

        int icon = R.drawable.ic_small_notification;
        long when = System.currentTimeMillis();
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Notification notification;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(icon).setTicker(appname).setWhen(0)
                .setAutoCancel(true).setContentTitle(appname)
                .setContentText(message).build();

        notificationManager.notify((int) when, notification);
    }

    private void showActivitiesOnUI()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, activitiesOnListView);
        setListAdapter(adapter);
    }

    private void addScheduler(Activity activity)
    {
        MyTimerTask myTask = new MyTimerTask(activity.getName());
        Timer myTimer = new Timer();
        myTimer.schedule(myTask, 0, activity.getIntervalMinutes());
    }

    private void getActivities()
    {
        //take data from memory
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains(ACTIVITIES_LIST))
        {
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.putString(ACTIVITIES_LIST, "");
            prefsEditor.commit();
        }

        String json = sharedPreferences.getString(ACTIVITIES_LIST, "");
        Type listType = new TypeToken<ArrayList<Activity>>(){}.getType();

        if(json == "")
        {
            activities = new ArrayList<Activity>();
        }
        else
        {
            activities = new Gson().fromJson(json, listType);
        }
    }

    private void saveActivities()
    {
        //save the list in memory
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activities);
        prefsEditor.putString(ACTIVITIES_LIST, json);
        prefsEditor.commit();
    }

    private void addNotification() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_small_notification);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

//        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        //get details from UI
        EditText editTextForActivityName = (EditText) findViewById(R.id.notificationName);
        String activityName = editTextForActivityName.getText().toString();
        EditText editTextForIntervalTime = (EditText) findViewById(R.id.intervalNumber);
        String intervalTime = editTextForIntervalTime.getText().toString();

        boolean edit = false;
        // edit selected activity
        for(Activity act : activities) {
            if (act.getPosition() != -1) {
                edit = true;
                act.setName(activityName);
                act.setIntervalMinutes(Integer.parseInt(intervalTime));
                activitiesOnListView.set(act.getPosition(), act.toString());
                act.setPosition(-1);

            }
        }

        //add activity to list
        if(edit == false) {
            Activity activity = (new Activity(activityName, Integer.parseInt(intervalTime)));
            activities.add(activity);
            activitiesOnListView.add(activity.toString());
        }

        saveActivities();
        getActivities();
        showActivitiesOnUI();
        notificationName.getText().clear();
        notificationInterval.getText().clear();
    }
}

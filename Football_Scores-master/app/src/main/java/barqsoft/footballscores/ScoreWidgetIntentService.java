package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.service.myFetchService;

public class ScoreWidgetIntentService extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.SCORES_TABLE + "." + DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    // these indices must match the projection
    private static final int ID = 0;
    private static final int INDEX_HOME_COL = 1;
    private static final int INDEX_AWAY_COL = 2;
    private static final int INDEX_HOME_GOALS_COL = 3;
    private static final int INDEX_AWAY_GOALS_COL = 4;

    private CursorLoader mCursorLoader;
    public static final int SCORES_LOADER = 0;

    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;

    private String[] argumentDate = new String[1];

    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidget.class));

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        argumentDate[0] = mformat.format(date);

        mCursorLoader = new CursorLoader(this,DatabaseContract.scores_table.buildScoreWithDate(),
                SCORES_COLUMNS, null, argumentDate, DatabaseContract.scores_table.TIME_COL + " DESC");
        mCursorLoader.registerListener(SCORES_LOADER, this);
        mCursorLoader.startLoading();

    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.score_widget);

        if (data == null) {
            views.setTextViewText(R.id.home_name_widget, "No Games Today");
            updateWidget(views);
            return;
        }

        views.setTextViewText(R.id.home_name_widget, "No Games Today");

        while (data.moveToNext()) {

            String homeName = data.getString(INDEX_HOME_COL);
            String awayName = data.getString(INDEX_AWAY_COL);
            String homeGoals = data.getString(INDEX_HOME_GOALS_COL);
            String awayGoals = data.getString(INDEX_AWAY_GOALS_COL);

            if (!homeGoals.equals(Integer.toString(-1))) {
                views.setTextViewText(R.id.home_name_widget, homeName);

                views.setTextViewText(R.id.score_widget, homeGoals + " - " + awayGoals);

                views.setTextViewText(R.id.away_name_widget, awayName);

                break;
            }
        }



        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        updateWidget(views);

        data.close();
    }

    public void updateWidget(RemoteViews views) {
        for(int id : appWidgetIds) {
            appWidgetManager.updateAppWidget(id, views);
        }
    }



}

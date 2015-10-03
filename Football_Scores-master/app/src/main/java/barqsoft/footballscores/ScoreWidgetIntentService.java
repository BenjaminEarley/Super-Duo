package barqsoft.footballscores;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.TIME_COL
    };
    // these indices must match the projection
    private static final int ID = 0;
    private static final int INDEX_HOME_COL = 1;
    private static final int INDEX_AWAY_COL = 2;
    private static final int INDEX_HOME_GOALS_COL = 3;
    private static final int INDEX_AWAY_GOALS_COL = 4;
    private static final int INDEX_TIME_COL = 5;

    private CursorLoader mCursorLoader;
    public static final int SCORES_LOADER = 0;

    private String[] argumentDate = new String[1];

    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidget.class));

        update_scores();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        argumentDate[0] = mformat.format(date);

        mCursorLoader = new CursorLoader(this,DatabaseContract.scores_table.buildScoreWithDate(),
                SCORES_COLUMNS, null, argumentDate, DatabaseContract.scores_table.TIME_COL + " ASC");
        mCursorLoader.registerListener(SCORES_LOADER, this);
        mCursorLoader.startLoading();

    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String homeName = data.getString(INDEX_HOME_COL);
        String awayName = data.getString(INDEX_AWAY_COL);
        String homeGoals = data.getString(INDEX_HOME_GOALS_COL);
        String awayGoals = data.getString(INDEX_AWAY_GOALS_COL);
        int time = data.getInt(INDEX_TIME_COL);
    }

    private void update_scores()
    {
        Intent service_start = new Intent(this, myFetchService.class);
        this.startService(service_start);
    }

    /**
     * Showing a toast message, using the Main thread
     */
    private void showToast(final Context context, final String message) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

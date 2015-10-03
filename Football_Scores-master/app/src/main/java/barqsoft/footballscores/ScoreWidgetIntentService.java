package barqsoft.footballscores;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import barqsoft.footballscores.service.myFetchService;

public class ScoreWidgetIntentService extends IntentService implements LoaderManager.LoaderCallbacks<Cursor> {
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

    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidget.class));

        update_scores();

//        Uri ScoresWithDate = DatabaseContract.scores_table.buildScoreWithDate();
//
//        Cursor data = getContentResolver().query(ScoresWithDate, SCORES_COLUMNS, null,
//                null, DatabaseContract.scores_table.DATE_COL + " DESC");


    }

    private void update_scores()
    {
        Intent service_start = new Intent(this, myFetchService.class);
        this.startService(service_start);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,DatabaseContract.scores_table.buildScoreWithDate(),
                null,null,SCORES_COLUMNS,DatabaseContract.scores_table.DATE_COL + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            showToast(this, "data is null");
            return;
        }
        if (!data.moveToFirst()) {
            showToast(this, "no rows");
            data.close();
            return;
        }

        String homeName = data.getString(INDEX_HOME_COL);
        String awayName = data.getString(INDEX_AWAY_COL);
        String homeGoals = data.getString(INDEX_HOME_GOALS_COL);
        String awayGoals = data.getString(INDEX_AWAY_GOALS_COL);
        showToast(this, homeName + " " + awayName);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

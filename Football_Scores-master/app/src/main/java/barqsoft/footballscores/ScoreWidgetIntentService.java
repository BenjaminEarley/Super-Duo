package barqsoft.footballscores;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import barqsoft.footballscores.service.myFetchService;

public class ScoreWidgetIntentService extends IntentService {
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    // these indices must match the projection
    private static final int INDEX_HOME_COL = 0;
    private static final int INDEX_AWAY_COL = 1;
    private static final int INDEX_HOME_GOALS_COL = 2;
    private static final int INDEX_AWAY_GOALS_COL = 3;

    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidget.class));

        Uri ScoresWithDate = DatabaseContract.scores_table.buildScoreWithDate();

        Cursor data = getContentResolver().query(ScoresWithDate, SCORES_COLUMNS, null,
                null, DatabaseContract.scores_table.DATE_COL + " DESC");

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

    }
}

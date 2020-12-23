package com.whimsydigital.ingeldop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


/**
 * An Activity for viewing Ingeldop game play statistics.
 *
 * Handles reading stats from the main Activity, setting the
 * text values of various labels for displaying stats, and
 * updating the history graph with historical games.
 */
public class StatsActivity extends AppCompatActivity {

    // UI Elements
    TextView numWinsTextView;
    TextView numLossTextView;
    TextView percentWinTextView;
    TextView bestHandTextView;
    TextView worstHandTextView;
    TextView avgHandTextView;
    LineChart historyChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout for this activity
        setContentView(R.layout.statslayout);

        // Set up the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Save UI elements we interact with
        numWinsTextView    = (TextView) findViewById (R.id.numWinsTextView);
        numLossTextView    = (TextView) findViewById (R.id.numLossTextView);
        percentWinTextView = (TextView) findViewById (R.id.percentWinTextView);
        bestHandTextView   = (TextView) findViewById (R.id.bestHandTextView);
        worstHandTextView  = (TextView) findViewById (R.id.worstHandTextView);
        avgHandTextView    = (TextView) findViewById (R.id.avgHandTextView);
        historyChart       = (LineChart) findViewById(R.id.chart);

        // Get stats values passed to this Intent
        int numGames = getIntent().getIntExtra(getString(R.string.intent_extra_numGames), 0);
        int numWins = getIntent().getIntExtra(getString(R.string.intent_extra_numWins), 0);
        int numLoss = getIntent().getIntExtra(getString(R.string.intent_extra_numLoss), 0);
        int[] numCards = getIntent().getIntArrayExtra(getString(R.string.intent_extra_numCards));
        int numHist = getIntent().getIntExtra(getString(R.string.intent_extra_numHist), 0);
        ArrayList<int[]> gameHistory = new ArrayList<int[]>();
        for (int i = 0; i < numHist; i++) {
            int[] histGame = getIntent().getIntArrayExtra(getString(R.string.intent_extra_hist) + "_" + i);
            gameHistory.add(histGame);
        }

        // Calculate any additional status values
        int bestHand = Integer.MAX_VALUE;
        int worstHand = -1;
        int sumHand = 0;
        int numFinish = 0;

        for (int i = 0; i < numCards.length; i++) {
            if (i < bestHand && numCards[i] != 0) bestHand = i;
            if (i > worstHand && numCards[i] != 0) worstHand = i;
            if (numCards[i] != 0) {
                numFinish += numCards[i];
                sumHand += numCards[i]*i;
            }
        }

        double averageHand = (double) sumHand / numFinish;
        double winPercent = numGames == 0 ? 0 : (double) numWins / numGames;

        // Set the text on various stats labels with the values
        numWinsTextView.setText(getString(R.string.numWinsText, numWins));
        numLossTextView.setText(getString(R.string.numLossText, numLoss));
        percentWinTextView.setText(getString(R.string.winPercentText,  winPercent));
        bestHandTextView.setText(getString(R.string.bestHandText, bestHand));
        worstHandTextView.setText(getString(R.string.worstHandText, worstHand));
        avgHandTextView.setText(getString(R.string.avgHandText, averageHand));

        // Set the data on the chart to historical game data
        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet> ();
        for (int[] game : gameHistory) {

            // Convert game data to chart entries
            int i = 0;
            ArrayList<Entry> entries = new ArrayList<Entry>();
            for (int val : game) entries.add(new Entry(i++, val));

            // Create & config a line data set; add it to the list
            LineDataSet newLine = new LineDataSet(entries, null);
            newLine.setDrawCircles(false);
            newLine.setDrawValues(false);
            lines.add(newLine);
        }

        // Set history chart data & options
        historyChart.setData(new LineData(lines));
        historyChart.getLegend().setEnabled(false);
        historyChart.getXAxis().setEnabled(false);
        historyChart.getAxisRight().setEnabled(false);
        historyChart.setDescription(null);
        historyChart.setTouchEnabled(false);
        historyChart.invalidate(); // refresh
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statsmenu, menu);
        return true;
    }
}

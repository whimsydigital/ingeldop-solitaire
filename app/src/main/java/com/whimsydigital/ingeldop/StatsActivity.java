package com.whimsydigital.ingeldop;

import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
public class StatsActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

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
        myToolbar.setOnMenuItemClickListener(this);

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
        ArrayList<int[]> gameHistory = new ArrayList<>();
        for (int i = 0; i < numHist; i++) {
            int[] histGame = getIntent().getIntArrayExtra(getString(R.string.intent_extra_hist) + "_" + i);
            gameHistory.add(histGame);
        }

        // Calculate best, worst, average hands and win percent
        double bestHand = Double.NaN;
        double worstHand = Double.NaN;
        int sumHand = 0;
        for (int i = 0; i < numCards.length; i++) {
            if ((Double.isNaN(bestHand) || i < bestHand) && numCards[i] != 0) bestHand = i;
            if ((Double.isNaN(worstHand) || i > worstHand) && numCards[i] != 0) worstHand = i;
            if (numCards[i] != 0) {
                sumHand += numCards[i]*i;
            }
        }
        double averageHand = (double) sumHand / (numWins + numLoss);
        double winPercent = numGames == 0 ? 0 : (double) numWins / numGames;

        // Set the text on various stats labels with the values
        numWinsTextView.setText(getString(R.string.numWinsText, numWins));
        numLossTextView.setText(getString(R.string.numLossText, numLoss));
        percentWinTextView.setText(getString(R.string.winPercentText,  winPercent));
        bestHandTextView.setText(getString(R.string.bestHandText, bestHand));
        worstHandTextView.setText(getString(R.string.worstHandText, worstHand));
        avgHandTextView.setText(getString(R.string.avgHandText, averageHand));

        // Resource that contains colors to plot games with
        TypedArray colors = getResources().obtainTypedArray(R.array.plotColors);

        // Set the data on the chart to historical game data
        ArrayList<ILineDataSet> lines = new ArrayList<> ();
        int cIndex = 0;
        for (int[] game : gameHistory) {

            // Convert game data to chart entries
            int i = 0;
            ArrayList<Entry> entries = new ArrayList<>();
            for (int val : game) entries.add(new Entry(i++, val));

            // Create & config a line data set; add it to the list
            LineDataSet newLine = new LineDataSet(entries, null);
            newLine.setDrawCircles(false);
            newLine.setDrawValues(false);
            newLine.setMode(LineDataSet.Mode.STEPPED);
            newLine.setColor(colors.getColor(cIndex++ % colors.length(), 0));
            lines.add(newLine);
        }

        // Only add data if there's been games played
        if (gameHistory.size() != 0) {
            historyChart.setData(new LineData(lines));
        }

        // Set history chart data & options
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


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        // Set the text on various stats labels with default values
        numWinsTextView.setText(getString(R.string.numWinsText, 0));
        numLossTextView.setText(getString(R.string.numLossText, 0));
        percentWinTextView.setText(getString(R.string.winPercentText,  0.0));
        bestHandTextView.setText(getString(R.string.bestHandText, Double.NaN));
        worstHandTextView.setText(getString(R.string.worstHandText, Double.NaN));
        avgHandTextView.setText(getString(R.string.avgHandText, Double.NaN));

        // Clear & refresh the chart
        historyChart.clear();
        historyChart.invalidate();

        // Return a result so saved stats get cleared
        setResult(RESULT_OK, null);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}

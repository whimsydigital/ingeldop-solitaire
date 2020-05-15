package com.brycekellogg.ingeldop;


import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/* Main activity for Ingeldop. The game is a single activity app, where all interaction
 * is done in the main activity. We have some popups and dialogs for things such as
 * errors, settings, and stats.  */
public class IngeldopActivity extends AppCompatActivity implements View.OnClickListener {
    public Ingeldop game;



    private int marginBetween;
    private int marginBetweenStep;
    private int marginBetweenMin;
    private int marginBetweenMax;

    private float disableAlpha;
    private float enableAlpha;


    // UI Elements
    View dealButton;
    View zoomOutButton;
    View zoomInButton;

    ConstraintLayout.LayoutParams dealButtonLayoutParams;



    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.gamelayout);  // Set layout
        findViewById(R.id.discardButton).setOnClickListener(this);
        findViewById(R.id.dealButton).setOnClickListener(this);
        findViewById(R.id.newGameButton).setOnClickListener(this);
        findViewById(R.id.zoomInButton).setOnClickListener(this);
        findViewById(R.id.zoomOutButton).setOnClickListener(this);
        findViewById(R.id.statsButton).setOnClickListener(this);
        findViewById(R.id.settingsButton).setOnClickListener(this);


        marginBetween     = 100;  // TODO: loaded from saved state
        marginBetweenStep = 30;   // TODO: based on screen size
        marginBetweenMin  = 10;   // TODO: based on screen size
        marginBetweenMax  = 400;  // TODO: based on screen size

        disableAlpha = Float.parseFloat(getString(R.string.disableAlpha));
        enableAlpha  = Float.parseFloat(getString(R.string.enableAlpha));

        // Save UI elements we interact with
        dealButton = findViewById(R.id.dealButton);
        dealButtonLayoutParams = (ConstraintLayout.LayoutParams) dealButton.getLayoutParams();
        zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomInButton  = findViewById(R.id.zoomInButton);

        game = new Ingeldop();
        doZoom(false, false);  // Update scale

//        restoreState();          // Try to load saved state
        update();                // Update layout/graphics
    }


    /* Display an alert with the given message. This currently
     * makes use of the Toast functionality. Any alerts needed
     * by any component should make use of this function.  */
    public void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /* Update the graphical state of the play area. This involves
     * updating the graphical (empty/enabled) state of the deal
     * button, requesting a layout and invalidating the various
     * views, and potentially displaying an alert if the game
     * is over. This happens on every deal and discard, which
     * might cause some overhead.  */
    public void update() {
        // Update deal button state on empty or game over
        DealButton dealButton = (DealButton) findViewById(R.id.dealButton);
        dealButton.setEmpty(game.deckSize() == 0);
        dealButton.setEnabled(!game.gameOver());

        // Update layouts and graphics
        findViewById(R.id.layout).requestLayout();
        findViewById(R.id.dealButton).requestLayout();
        findViewById(R.id.handView).requestLayout();

        // Show alerts if needed
        if (game.gameOver()) alert(getString(R.string.gameOverText));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dealButton:    deal();    break;
            case R.id.discardButton: discard(); break;
            case R.id.newGameButton: newGame(); break;
            case R.id.zoomInButton:  doZoom(true, false); break;
            case R.id.zoomOutButton: doZoom(false, true); break;
        }
    }

    /* Processes a dealButton onClick action. We deal a card and update the button
     * state based on if the deck is empty or if the game is over. If the deck is
     * empty, we set the dealButton's empty state. If the game is over, we set
     * the dealButtons enabled state to false. Finally, we notify the context. */
    public void deal() {
        // Do the deal
        game.deal();

        // Scroll to the right on deal
        ((HorizontalScrollView) findViewById(R.id.scrollView)).fullScroll(View.FOCUS_RIGHT);

        // Update activity
        update();
    }

    /* Process a discard action. We attempt to discard the selected cards, but if a discard
     * is not allowed due to currently selected cards, an error pop-up is displayed. If a
     * discard is allowed, the selected cards are discarded, we potentially update the
     * deal button image if the game is over, and request a redraw of the hand view. If
     * the game is over, we additionally display a popup notification. */
    public void discard() {
        try {
            // Do the discard
            game.discard();

            // Update activity
            update();

        } catch (DiscardException e) {
            // Update activity with error
            alert(e.getMessage());
        }
    }


    /**
     * Update the UI scale and optionally zoom in or out by one step.
     *
     * Zooming consists of adjusting the margins around various UI
     * elements. A zoom out by a single step makes UI elements smaller
     * by increasing the margins by one step. A zoom in by a single
     * step makes UI elements bigger by decreasing the margins by one
     * step. This forces the constraint layout to adjust the sizes of
     * the UI elements to fit within the margins. We also enable or
     * disable the zoom buttons based on if we have reached zoom limits. */
    public void doZoom(boolean in, boolean out) {
        // Update margins if zooming in/out
        if (in)  marginBetween -= marginBetweenStep;
        if (out) marginBetween += marginBetweenStep;

        // Apply layout changes to LayoutParams
        dealButtonLayoutParams.bottomMargin = marginBetween;

//        // Update zoom icons enabled state
//        zoomOutMenuItem.setEnabled(marginBetween < marginBetweenMax);
//        zoomInMenuItem.setEnabled(marginBetween > marginBetweenMin);
//
//        // Update zoom icon color (grey for disabled)
//        zoomOutMenuItem.setAlpha(zoomOutMenuItem.isEnabled() ? enableAlpha : disableAlpha);
//        zoomInMenuItem.setAlpha(zoomInMenuItem.isEnabled() ? enableAlpha : disableAlpha);

        update();
    }


    /* Starts a new game, resets deal button image to full
     * deck, and requests a graphics/layout update.  */
    void newGame() {
        // Start a new game
        this.game = new Ingeldop();

        // Reset deal button state
        DealButton dealButton = (DealButton) findViewById(R.id.dealButton);
        dealButton.setEmpty(false);
        dealButton.setEnabled(true);

        // Redraw the hand view
        update();
    }


//    void saveState() {
//        // Build file contents
//        StringBuilder out = new StringBuilder();
//        out.append("game:" + game + '\n');
//        out.append("scale:" + scale + '\n');
//
//        // Write to file
//        File file = new File(this.getFilesDir(), getString(R.string.saveFilename));
//        try {
//            FileWriter writer = new FileWriter(file);
//            writer.append(out.toString());
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            Log.e("saveState()", "ERROR writing file", e);
//        }
//    }

//    void restoreState() {
//        File file = new File(this.getFilesDir(), getString(R.string.saveFilename));
//        try {
//            // Read file into string
//            Scanner reader = new Scanner(file);
//            while (reader.hasNextLine()) {
//                String s = reader.nextLine();
//
//                // Parse into fields
//                String[] keyvalue = s.split(":");
//                String key = keyvalue[0];
//                String val = keyvalue[1];
//
//                // Extract saved state
//                switch (key) {
//                    case "scale": scale = Double.parseDouble(val); break;
//                    case "game": game = Ingeldop.parseString(val); break;
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            Log.i("restoreState()", "no saved state");
//            newGame();
//            scale = 1;
//        }
//        update();
//    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        saveState();
    }

}



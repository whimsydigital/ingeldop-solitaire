/*   _____  _   _   _____  ______  _       _____    ____   _____
 *  |_   _|| \ | | / ____||  ____|| |     |  __ \  / __ \ |  __ \
 *    | |  |  \| || |  __ | |__   | |     | |  | || |  | || |__) |
 *    | |  | . ` || | |_ ||  __|  | |     | |  | || |  | ||  ___/
 *   _| |_ | |\  || |__| || |____ | |____ | |__| || |__| || |
 *  |_____||_| \_| \_____||______||______||_____/  \____/ |_|
 *
 * Author: Bryce Kellogg (bryce@kellogg.org)
 * Copyright: 2020 Bryce Kellogg
 * License: GPL
 */
package com.whimsydigital.ingeldop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;


/**
 *  Main activity for an Ingeldop Game.
 *
 * The game itself is a single activity app, where all interaction with the cards
 * and the game is done in the main activity. Statistics are viewed and interacted
 * with in their own Activity. Additionally, acts as a listener for it's own menu. */
public class IngeldopActivity extends AppCompatActivity implements View.OnClickListener {

    // Stats & History
    private IngeldopStats stats;
    private IngeldopState state;

    // Zoom margins
    private int marginBetween;
    private int marginBetweenStep;
    private int marginBetweenMin;
    private int marginBetweenMax;

    // UI Elements
    HorizontalScrollView scrollView;
    DealButton dealButton;
    View discardButton;
    View newGameButton;
    View zoomInButton;
    View zoomOutButton;
    View statsButton;
    View handView;


    /**
     * Called when the Activity first launches; initializes the Activity
     *
     * Does everything needed to get the game ready to play. This involves
     * setting layout, caching often used Views, calculating margins, reading
     * preferences, and initializing a new game or loading an existing game.  */
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        // Set layout for this activity
        setContentView(R.layout.gamelayout);

        // Save UI elements we interact with
        scrollView     = findViewById(R.id.scrollView);
        dealButton     = findViewById(R.id.dealButton);
        discardButton  = findViewById(R.id.discardButton);
        newGameButton  = findViewById(R.id.newGameButton);
        zoomInButton   = findViewById(R.id.zoomInButton);
        zoomOutButton  = findViewById(R.id.zoomOutButton);
        statsButton    = findViewById(R.id.statsButton);
        handView       = findViewById(R.id.handView);

        // Set all out click listeners
        dealButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        statsButton.setOnClickListener(this);

        // Get info needed to calculate zoom margins
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        final double marginBetweenFraction     = Double.parseDouble(getString(R.string.marginBetweenFraction));
        final double marginBetweenStepFraction = Double.parseDouble(getString(R.string.marginBetweenStepFraction));
        final double marginBetweenMinFraction  = Double.parseDouble(getString(R.string.marginBetweenMinFraction));
        final double marginBetweenMaxFraction  = Double.parseDouble(getString(R.string.marginBetweenMaxFraction));

        // Calculate zoom margins based on screen size
        marginBetween     = (int) (screenHeight*marginBetweenFraction);
        marginBetweenStep = (int) (screenHeight*marginBetweenStepFraction);
        marginBetweenMin  = (int) (screenHeight*marginBetweenMinFraction);
        marginBetweenMax  = (int) (screenHeight*marginBetweenMaxFraction);

        // Get the zoom margin from saved preferences, use default is not found
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        marginBetween = sharedPref.getInt(getString(R.string.pref_key_margin), marginBetween);

        // Initialize empty stats & state
        this.stats = new IngeldopStats(this);
        this.state = new IngeldopState(this);

        // Try to load saved state and stats
        this.state.load();
        this.stats.load();

        // Update button and scroll state if needed
        dealButton.setEnabled(!this.state.getGame().gameOver());
        dealButton.setEmpty(this.state.getGame().deckSize() == 0);
        discardButton.setEnabled(!this.state.getGame().gameOver());

        // Update UI zoom based on saved or defaults
        doZoom(false, false);
    }


    /**
     * An accessor function for getting the current Ingeldop game.
     *
     * Other classes (like the HandViewExpanded) need access to
     * the current Ingeldop game (for drawing for example). This
     * provides access to it via the state object.  */
    public Ingeldop getGame() {
        return state.getGame();
    }


    /**
     * Start a new game and reset deck/hand views.
     *
     * A new game is initiated via the state object, resetting
     * the state of the deal button, and requesting a layout
     * update of the  hand view. We request a layout update
     * instead of just a redraw because clearing the hand will
     * change the size and force the containing scroll view to
     * remove scroll bars.  */
    private void doNewGame() {
        // Start a new game
        state.newGame();
        stats.newGame();

        // Reset deal & discard button state
        dealButton.setEnabled(true);
        dealButton.setEmpty(false);
        discardButton.setEnabled(true);

        // Redraw the hand view
        handView.requestLayout();
    }


    /**
     * Processes a deal action and update UI elements.
     *
     * We deal a card, request a layout update for the hand view to deal
     * with the change in the hand size, and instruct the scroll view to
     * scroll all the way to the right so the most recent dealt card is
     * displayed. If the deck is empty, we update the deal button state
     * so it can show an empty image. If the game is over, we call the
     * game over logic.  */
    private void doDeal() {
        state.getGame().deal();                                // Do the deal
        stats.update(state.getGame().handSize());              // Update current game
        handView.requestLayout();                              // Update hand
        scrollView.fullScroll(View.FOCUS_RIGHT);               // Scroll to the right on deal
        dealButton.setEmpty(state.getGame().deckSize() == 0);  // Update button state if empty
        if (state.getGame().gameOver()) doGameOver();          // Handle game over
    }


    /**
     * Process a discard action and handle incorrect discards.
     *
     * We attempt to discard the selected cards, but if a discard is
     * not allowed due to currently selected cards we catch the thrown
     * exception and display a pop-up displaying the discard error text. If a
     * discard is allowed, the selected cards are discarded and we update the
     * layout of the hand (because the size changes with a discard). If the
     * game is over, we call the game over logic.  */
    private void doDiscard() {
        try {
            state.getGame().discard();                     // Do the discard
            stats.update(state.getGame().handSize());      // Update game history
            handView.requestLayout();                      // Update hand
            if (state.getGame().gameOver()) doGameOver();  // Handle game over
        } catch (DiscardException ignored) {
            Toast.makeText(this, getString(R.string.DISCARD_ERROR_TEXT), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Update the UI elements when the game is over.
     *
     * If the game is over we disable the deal and discard buttons and
     * display a message to the player about the number of cards left
     * in the hand (or a win message of none left). Additionally, the
     * play statistics are updated accordingly and saved to disk.  */
    private void doGameOver() {
        // Disable deal & discard buttons
        dealButton.setEnabled(false);
        discardButton.setEnabled(false);

        // Update stats & history
        stats.gameOver(state.getGame().handSize());

        // Show game over message
        String msg;
        if (state.getGame().handSize() == 0) {
            msg = getString(R.string.gameWinText);
        } else {
            msg = getString(R.string.gameOverText, state.getGame().handSize());
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        stats.save();
    }


    /**
     * Launch the statistics viewing page.
     *
     * Will launch the Activity for viewing game statistics as a new
     * Intent. We pass game statistics to the new Intent via the
     * putExtra() functions using the stats object. This function
     * sets up the new intent, passes in needed stats, and starts
     * it as a new Activity. The result of the new Intent is used
     * to determine if stats should be cleared or not. */
    private void doStats() {
        Intent intent = new Intent(this, StatsActivity.class);
        stats.put(intent);
        startActivityForResult(intent, 1);
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
    private void doZoom(boolean in, boolean out) {
        // Update margins if zooming in/out
        if (in)  marginBetween -= marginBetweenStep;
        if (out) marginBetween += marginBetweenStep;

        // Apply layout changes to LayoutParams
        ((ConstraintLayout.LayoutParams) dealButton.getLayoutParams()).bottomMargin = marginBetween;

        // Update zoom icons enabled state
        zoomOutButton.setEnabled(marginBetween < marginBetweenMax);
        zoomInButton.setEnabled(marginBetween > marginBetweenMin);

        // Update UI layout
        dealButton.requestLayout();
    }


    /**
     * Handle button clicks and take actions.
     *
     * All clicks on buttons contained in the main game layout are
     * handled here. This is how we initialize is deal, discard, new
     * game, zoom, and change to the stats activity. This method just
     * acts as a dispatch for the dedicated methods for each action.  */
    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.dealButton)     doDeal();
        if (v.getId() ==  R.id.discardButton)  doDiscard();
        if (v.getId() ==  R.id.newGameButton)  doNewGame();
        if (v.getId() ==  R.id.statsButton)    doStats();
        if (v.getId() ==  R.id.zoomInButton)   doZoom(true, false);
        if (v.getId() ==  R.id.zoomOutButton)  doZoom(false, true);
    }


    /**
     * Handle result of a launched child Activity.
     *
     * This is used to handle the result of the StatsActivity. If the
     * clear button is clicked, we return a RESULT_OK from the Activity,
     * receive that here, and actually perform the stats clear action.  */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.stats.clear();
            this.stats.load();
        }
    }


    /**
     * Called when the Activity is killed or suspended.
     *
     * We use this to save the current game state, play statistics,
     * and current zoom level to the Android shared preferences.  */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save the margin (zoom level) to preferences
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.pref_key_margin), marginBetween);
        editor.apply();

        this.state.save();
        this.stats.save();
    }
}

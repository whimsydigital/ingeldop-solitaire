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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;


/* Main activity for Ingeldop. The game is a single activity app, where all interaction
 * is done in the main activity. We have some popups and dialogs for things such as
 * errors, settings, and stats.  */
public class IngeldopActivity extends AppCompatActivity implements View.OnClickListener {
    public Ingeldop game;

    // Zoom margins
    private int marginBetween;
    private int marginBetweenStep;
    private int marginBetweenMin;
    private int marginBetweenMax;

    // Stats
    private int numGames;
    private int numWins;
    private int numLoss;
    private int[] numCards;
    private LinkedList<ArrayList<Integer>> gameHist;
    private final int MAX_GAME_HIST = 10;

    // UI Elements
    HorizontalScrollView scrollView;
    DealButton dealButton;
    View discardButton;
    View newGameButton;
    View zoomInButton;
    View zoomOutButton;
    View statsButton;
    View handView;

    // Layout params used for zooming
    ConstraintLayout.LayoutParams dealButtonLayoutParams;



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

        // Save layout params for zooming
        dealButtonLayoutParams = (ConstraintLayout.LayoutParams) dealButton.getLayoutParams();

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

        // Try to load saved state and stats
        loadState();
        loadStats();

        // Update button and scroll state if needed
        dealButton.setEnabled(!game.gameOver());
        dealButton.setEmpty(game.deckSize() == 0);
        discardButton.setEnabled(!game.gameOver());

        // Update UI zoom based on saved or defaults
        doZoom(false, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dealButton:     doDeal();    break;
            case R.id.discardButton:  doDiscard(); break;
            case R.id.newGameButton:  newGame();   break;
            case R.id.statsButton:    doStats();   break;
            case R.id.zoomInButton:   doZoom(true, false); break;
            case R.id.zoomOutButton:  doZoom(false, true); break;
        }
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
        game.deal();                                // Do the deal
        gameHist.getLast().add(game.handSize());    // Update game history
        handView.requestLayout();                   // Update hand
        scrollView.fullScroll(View.FOCUS_RIGHT);    // Scroll to the right on deal
        dealButton.setEmpty(game.deckSize() == 0);  // Update button state if empty
        if (game.gameOver()) doGameOver();          // Handle game over
    }


    /**
     * Process a discard action and handle incorrect discards.
     *
     * We attempt to discard the selected cards, but if a discard is
     * not allowed due to currently selected cards we catch the thrown
     * exception and display a pop-up describing the discard exception. If a
     * discard is allowed, the selected cards are discarded and we update the
     * layout of the hand (because the size changes with a discard). If the
     * game is over, we call the game over logic.
     *
     * TODO: catch different exception types for different discard fails  */
    private void doDiscard() {
        try {
            game.discard();                           // Do the discard
            gameHist.getLast().add(game.handSize());  // Update game history
            handView.requestLayout();                 // Update hand
            if (game.gameOver()) doGameOver();        // Handle game over
        } catch (DiscardException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Start a new game and reset deck/hand views.
     *
     * A new game is initiated by assigning a new Ingeldop
     * instance to this.game, resetting the state of the
     * deal button, and requesting a layout update of the
     * hand view. We request a layout update instead of just
     * a redraw because clearing the hand will change the size
     * and force the containing scroll view to remove scroll bars.
     * Additionally, the numGames counter is updated and stats
     * are saved to disk.  */
    private void newGame() {
        // Start a new game
        this.game = new Ingeldop();
        numGames++;

        if (gameHist.size() >= MAX_GAME_HIST) gameHist.removeFirst();
        gameHist.add(new ArrayList<Integer>(game.deckSize()));

        saveStats();

        // Reset deal & discard button state
        dealButton.setEnabled(true);
        dealButton.setEmpty(false);
        discardButton.setEnabled(true);

        // Redraw the hand view
        handView.requestLayout();
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
        dealButtonLayoutParams.bottomMargin = marginBetween;

        // Update zoom icons enabled state
        zoomOutButton.setEnabled(marginBetween < marginBetweenMax);
        zoomInButton.setEnabled(marginBetween > marginBetweenMin);

        // Update UI layout
        dealButton.requestLayout();
    }


    /**
     * Update the UI elements when the game is over.
     *
     * If the game is over we disable the deal and discard buttons and
     * display a message to the player about the number of cards left
     * in the hand (or a win message of none left). Additionally, the
     * counters for winning/losing a game are updated accordingly and
     * the statistics are saved to disk.  */
    private void doGameOver() {
        // Disable deal & discard buttons
        dealButton.setEnabled(false);
        discardButton.setEnabled(false);

        // Show game over message
        String msg;
        if (game.handSize() == 0) {
            msg = getString(R.string.gameWinText);
            numWins++;
            numCards[0]++;
        } else {
            msg = getString(R.string.gameOverText, game.handSize());
            numLoss++;
            numCards[game.handSize()]++;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        saveStats();
    }


    /**
     * Launch the statistics viewing page.
     *
     * Will launch the Activity for viewing game statistics as a new
     * Intent. We pass game statistics to the new Intent via the
     * putExtra() functions. This function sets up the new intent,
     * passes in needed stats, and starts it as a new Activity.  */
    private void doStats() {
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra(getString(R.string.intent_extra_numGames), numGames);
        intent.putExtra(getString(R.string.intent_extra_numWins), numWins);
        intent.putExtra(getString(R.string.intent_extra_numLoss), numLoss);
        intent.putExtra(getString(R.string.intent_extra_numCards), numCards);

        intent.putExtra(getString(R.string.intent_extra_numHist), gameHist.size());
        for (int i =0; i < gameHist.size(); i++) {
            int[] game = new int[gameHist.get(i).size()];
            for (int j = 0; j < game.length; j++) game[j] = gameHist.get(i).get(j);
            intent.putExtra(getString(R.string.intent_extra_hist) + "_" + i, game);
        }

        startActivity(intent);
    }


    /**
     * Save game statistics to persistent storage.
     *
     * Whenever the app is closed or suspended, we save the game play
     * statistics to the Android SharedPreferences so that they
     * can be restored when the app is resumed or re-launched. The
     * variables that are saved are:
     *
     *    - numGames = the total number of games played
     *    - numWins  = the number of games won
     *    - numLoss  = the number of games lost
     *    - numCards = an array of counts for the number of games that
     *                 resulted in a specific number of cards left in
     *                 the hand. Indices represent number of cards in
     *                 the hand, values represent the number of games.
     */
    private void saveStats() {

        JSONArray jsonNumCards  = new JSONArray();
        for (int c : numCards) jsonNumCards.put(c);

        JSONArray jsonGameHist = new JSONArray();
        for (ArrayList<Integer> game : gameHist) {
            JSONArray jsonGame = new JSONArray();
            for (Integer i : game) jsonGame.put(i);
            jsonGameHist.put(jsonGame);
        }

        SharedPreferences.Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.pref_key_numGames), numGames);
        editor.putInt(getString(R.string.pref_key_numWins), numWins);
        editor.putInt(getString(R.string.pref_key_numLoss), numLoss);
        editor.putString(getString(R.string.pref_key_numCards), jsonNumCards.toString());
        editor.putString(getString(R.string.pref_key_hist), jsonGameHist.toString());

        editor.apply();
    }


    /**
     * Load game statistics from persistent storage
     *
     * Whenever the app is created or resumed, we load the game stats
     * from the Android SharedPreferences and restore the stats tracking
     * variables to how they were before we closed or suspended. The format
     * and keys for the saved stats are described in the saveStats function doc.
     */
    private void loadStats() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        numGames = sharedPref.getInt(getString(R.string.pref_key_numGames), 0);
        numWins  = sharedPref.getInt(getString(R.string.pref_key_numWins), 0);
        numLoss  = sharedPref.getInt(getString(R.string.pref_key_numLoss), 0);

        try {
            JSONArray numCardsJson = new JSONArray(sharedPref.getString(getString(R.string.pref_key_numCards), "ERROR"));
            numCards = new int[52];
            for (int i = 0; i < numCardsJson.length(); i++) numCards[i] = numCardsJson.getInt(i);
        } catch (JSONException e) {
            numCards = new int[52];
        }

        gameHist = new LinkedList<>();
        try {
            JSONArray jsonGameHist = new JSONArray(sharedPref.getString(getString(R.string.pref_key_hist), "ERROR"));
            for (int i = 0; i < jsonGameHist.length(); i++) {
                JSONArray game = jsonGameHist.getJSONArray(i);
                gameHist.add(new ArrayList<Integer>(game.length()));
                for (int j = 0; j < game.length(); j++) gameHist.getLast().add(game.getInt(j));
            }
        } catch (JSONException ignored) { }
    }


    /**
     * Save the state of the app to persistent storage.
     *
     * Whenever the app is closed or suspended, the state of various
     * variables is saved to the Android SharedPreferences so that they
     * can be restored when the app is resumed or re-launched. The variables
     * that are saved are:
     *
     *     - marginBetween = an integer denoting the space between deal and discard
     *                       buttons. Used to save zoom level.
     *     - game.deck = a JSON array of cards in the game deck
     *     - game.hand = a JSON array of cards in the game hand
     *     - game.sel = a JSON array of booleans denoting if a given card in the hand
     *                  has been selected or not. True means selected.
     *     - game.dealt = a boolean indicating that a card has been dealt and a discard
     *                    can happen. This prevents double discards from happening.
     *
     * Each of these is saved with a corresponding key as defined in
     * the string resources.
     */
    private void saveState() {
        SharedPreferences.Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.pref_key_margin), marginBetween);

        // Serialize the Ingeldop game as a JSON string
        JSONArray jsonDeck  = new JSONArray();
        JSONArray jsonHand  = new JSONArray();
        JSONArray jsonSel   = new JSONArray();
        for (Card c    : game.deck()) jsonDeck.put(c);
        for (Card c    : game.hand()) jsonHand.put(c);
        for (Boolean s : game.sel())  jsonSel.put(s);

        editor.putString(getString(R.string.pref_key_deck),  jsonDeck.toString());
        editor.putString(getString(R.string.pref_key_hand),  jsonHand.toString());
        editor.putString(getString(R.string.pref_key_sel),   jsonSel.toString());
        editor.putBoolean(getString(R.string.pref_key_dealt), game.dealt());
        editor.apply();
    }


    /**
     * Load the state of the app from persistent storage
     *
     * Whenever the app is created or resumed, we load the state
     * from the Android SharedPreferences and restore the state to
     * how it was before we closed or suspended. The format and keys
     * for the saved state are described in the saveState function doc.
     */
    private void loadState() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        // Get the zoom margin, using the default value as a backup
        marginBetween = sharedPref.getInt(getString(R.string.pref_key_margin), marginBetween);

        // Try to load a saved game; if anything goes wrong, create a new game
        try {
            // The deck, hand, and sel are store as JSON arrays
            JSONArray jsonDeck = new JSONArray(sharedPref.getString(getString(R.string.pref_key_deck), "ERROR"));
            JSONArray jsonHand = new JSONArray(sharedPref.getString(getString(R.string.pref_key_hand), "ERROR"));
            JSONArray jsonSel = new JSONArray(sharedPref.getString(getString(R.string.pref_key_sel), "ERROR"));

            // Convert the elements from strings to Card or Boolean
            Card[] deck   = new Card[jsonDeck.length()];
            Card[] hand   = new Card[jsonHand.length()];
            Boolean[] sel = new Boolean[jsonSel.length()];
            for (int i = 0; i < jsonDeck.length(); i++) deck[i] = Card.valueOf(jsonDeck.getString(i));
            for (int i = 0; i < jsonHand.length(); i++) hand[i] = Card.valueOf(jsonHand.getString(i));
            for (int i = 0; i < jsonHand.length(); i++) sel[i]  = jsonSel.getBoolean(i);

            // Get the dealt parameter and assign the game to the saved game
            boolean dealt = sharedPref.getBoolean(getString(R.string.pref_key_dealt), false);
            game = new Ingeldop(deck, hand, sel, dealt);

        } catch (JSONException e) {
            newGame();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        saveState();
        saveStats();
    }

}



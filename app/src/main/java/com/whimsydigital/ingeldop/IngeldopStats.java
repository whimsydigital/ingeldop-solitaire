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

import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * A helper class for tracking game play statistics and history. Keeps
 * track of the number of wins, losses, best & worst hand, and full play
 * log of a set of most recent games.  */
public class IngeldopStats {
    IngeldopActivity context;

    private int numGames;
    private int numWins;
    private int numLoss;
    private int[] numCards;
    private ArrayList<Integer> currentGame;
    private LinkedList<ArrayList<Integer>> gameHist;
    private final int MAX_GAME_HIST = 10;


    /**
     * Constructs a new IngeldopStats object, passing in
     * the IngeldopActivity as context so we can use it
     * to get the shared preferences editor.  */
    public IngeldopStats(IngeldopActivity context) {
        this.context = context;
    }


    /**
     * Update the state of the current game tracking.
     *
     * This is used so that once the game ends, we can save
     * the play log (number of cards in the hand) of the
     * game in the history for plotting.  */
    public void update(int handSize) {
        currentGame.add(handSize);
    }


    /**
     * Track that a new game has been initiated.
     *
     * This allows us to keep track of the number of games
     * played (irrespective of win/loss) and to reset the
     * tracking of the current game.  */
    public void newGame() {
        numGames++;
        currentGame = new ArrayList<>();
    }


    /**
     * Track that a game has finished.
     *
     * This allows us to record a win/loss and the
     * number of cards left in the hand. Additionally,
     * the current game is copied over into the play
     * history for later plotting.  */
    public void gameOver(int handSize) {
        if (gameHist.size() >= MAX_GAME_HIST) gameHist.removeFirst();
        gameHist.add(currentGame);
        numCards[handSize]++;
        if (handSize == 0) {
            numWins++;
        } else {
            numLoss++;
        }
    }


    /**
     * Load game statistics from persistent storage
     *
     * Whenever the app is created or resumed, we load the game stats
     * from the Android SharedPreferences and restore the stats tracking
     * variables to how they were before we closed or suspended. The format
     * and keys for the saved stats are described in the saveStats function doc.
     */
    public void load() {
        currentGame = new ArrayList<>();

        SharedPreferences sharedPref = context.getPreferences(IngeldopActivity.MODE_PRIVATE);

        numGames = sharedPref.getInt(context.getString(R.string.pref_key_numGames), 0);
        numWins  = sharedPref.getInt(context.getString(R.string.pref_key_numWins), 0);
        numLoss  = sharedPref.getInt(context.getString(R.string.pref_key_numLoss), 0);

        try {
            JSONArray numCardsJson = new JSONArray(sharedPref.getString(context.getString(R.string.pref_key_numCards), "ERROR"));
            numCards = new int[53];
            for (int i = 0; i < numCardsJson.length(); i++) numCards[i] = numCardsJson.getInt(i);
        } catch (JSONException e) {
            numCards = new int[53];
        }

        gameHist = new LinkedList<>();
        try {
            JSONArray jsonGameHist = new JSONArray(sharedPref.getString(context.getString(R.string.pref_key_hist), "ERROR"));
            for (int i = 0; i < jsonGameHist.length(); i++) {
                JSONArray game = jsonGameHist.getJSONArray(i);
                gameHist.add(new ArrayList<Integer>(game.length()));
                for (int j = 0; j < game.length(); j++) gameHist.getLast().add(game.getInt(j));
            }
        } catch (JSONException ignored) { }
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
    public void save() {

        JSONArray jsonNumCards  = new JSONArray();
        for (int c : numCards) jsonNumCards.put(c);

        JSONArray jsonGameHist = new JSONArray();
        for (ArrayList<Integer> game : gameHist) {
            JSONArray jsonGame = new JSONArray();
            for (Integer i : game) jsonGame.put(i);
            jsonGameHist.put(jsonGame);
        }

        SharedPreferences.Editor editor = context.getPreferences(IngeldopActivity.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.pref_key_numGames), numGames);
        editor.putInt(context.getString(R.string.pref_key_numWins), numWins);
        editor.putInt(context.getString(R.string.pref_key_numLoss), numLoss);
        editor.putString(context.getString(R.string.pref_key_numCards), jsonNumCards.toString());
        editor.putString(context.getString(R.string.pref_key_hist), jsonGameHist.toString());

        editor.apply();
    }


    /**
     * Clear game statistics from persistent storage.
     *
     * This will clear all saved game statistics both from
     * this class tracking them and from persistent storage.  */
    public void clear() {
        SharedPreferences.Editor editor = context.getPreferences(IngeldopActivity.MODE_PRIVATE).edit();
        editor.remove(context.getString(R.string.pref_key_numGames));
        editor.remove(context.getString(R.string.pref_key_numWins));
        editor.remove(context.getString(R.string.pref_key_numLoss));
        editor.remove(context.getString(R.string.pref_key_numCards));
        editor.remove(context.getString(R.string.pref_key_hist));
        editor.apply();

        numGames = 0;
        numWins = 0;
        numLoss = 0;
        numCards = new int[53];
        gameHist = new LinkedList<>();
    }


    /**
     * Put statistics and history into an Intent.
     *
     * When the stats and history need to be communicated to
     * another Activity, we pass them via an Intent. This method
     * passes all of its relevant data into the given Intent.  */
    public void put(Intent intent) {
        intent.putExtra(context.getString(R.string.intent_extra_numGames), numGames);
        intent.putExtra(context.getString(R.string.intent_extra_numWins),  numWins);
        intent.putExtra(context.getString(R.string.intent_extra_numLoss),  numLoss);
        intent.putExtra(context.getString(R.string.intent_extra_numCards), numCards);

        intent.putExtra(context.getString(R.string.intent_extra_numHist), gameHist.size());
        for (int i = 0; i < gameHist.size(); i++) {
            int[] game = new int[gameHist.get(i).size()];
            for (int j = 0; j < game.length; j++) game[j] = gameHist.get(i).get(j);
            intent.putExtra(context.getString(R.string.intent_extra_hist) + "_" + i, game);
        }
    }
}

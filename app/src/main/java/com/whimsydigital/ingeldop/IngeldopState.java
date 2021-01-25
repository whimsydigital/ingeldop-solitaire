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
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * A helper class for keeping track of game state, as well as
 * saving and loading that state to and from disk.  */
public class IngeldopState {
    IngeldopActivity context;
    Ingeldop game;


    /**
     * Constructs a new IngeldopState object, passing in
     * the IngeldopActivity as context so we can use it
     * to get the shared preferences editor.  */
    public IngeldopState(Context context) {
        this.context = (IngeldopActivity) context;
    }


    /**
     * An accessor function for getting the current Ingeldop game.
     *
     * This allows the main Activity to perform deals and discards
     * as well as forward this to the hand drawing View.  */
    public Ingeldop getGame() {
        return game;
    }


    /**
     * Initiates a new Ingeldop game.  */
    public void newGame() {
        game = new Ingeldop();
    }


    /**
     * Save the state of the app to persistent storage.
     *
     * Whenever the app is closed or suspended, the state of various
     * variables is saved to the Android SharedPreferences so that they
     * can be restored when the app is resumed or re-launched. The
     * variables that are saved are:
     *
     *     - game.deck = a JSON array of cards in the game deck
     *     - game.hand = a JSON array of cards in the game hand
     *     - game.sel = a JSON array of booleans denoting if a given
     *                  card in the hand has been selected or not.
     *                  True means selected.
     *     - game.dealt = a boolean indicating that a card has been
     *                    dealt and a discard can happen. This prevents
     *                    double discards from happening.
     *
     * Each of these is saved with a corresponding
     * key as defined in string resources. */
    public void save() {
        SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();

        // Serialize the Ingeldop game as a JSON string
        JSONArray jsonDeck  = new JSONArray();
        JSONArray jsonHand  = new JSONArray();
        JSONArray jsonSel   = new JSONArray();
        for (Card c    : game.deck()) jsonDeck.put(c);
        for (Card c    : game.hand()) jsonHand.put(c);
        for (Boolean s : game.sel())  jsonSel.put(s);

        editor.putString(context.getString(R.string.pref_key_deck),  jsonDeck.toString());
        editor.putString(context.getString(R.string.pref_key_hand),  jsonHand.toString());
        editor.putString(context.getString(R.string.pref_key_sel),   jsonSel.toString());
        editor.putBoolean(context.getString(R.string.pref_key_dealt), game.dealt());
        editor.apply();
    }


    /**
     * Load the state of the game from persistent storage
     *
     * Whenever the app is created or resumed, we load the state
     * from the Android SharedPreferences and restore the state to
     * how it was before we closed or suspended. The format and keys
     * for the saved state are described in the saveState function doc.  */
    public void load() {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);

        // Try to load a saved game; if anything goes wrong, create a new game
        try {
            // The deck, hand, and sel are store as JSON arrays
            JSONArray jsonDeck = new JSONArray(sharedPref.getString(context.getString(R.string.pref_key_deck), "ERROR"));
            JSONArray jsonHand = new JSONArray(sharedPref.getString(context.getString(R.string.pref_key_hand), "ERROR"));
            JSONArray jsonSel = new JSONArray(sharedPref.getString(context.getString(R.string.pref_key_sel),   "ERROR"));

            // Convert the elements from strings to Card or Boolean
            Card[] deck   = new Card[jsonDeck.length()];
            Card[] hand   = new Card[jsonHand.length()];
            Boolean[] sel = new Boolean[jsonSel.length()];
            for (int i = 0; i < jsonDeck.length(); i++) deck[i] = Card.valueOf(jsonDeck.getString(i));
            for (int i = 0; i < jsonHand.length(); i++) hand[i] = Card.valueOf(jsonHand.getString(i));
            for (int i = 0; i < jsonHand.length(); i++) sel[i]  = jsonSel.getBoolean(i);

            // Get the dealt parameter and assign the game to the saved game
            boolean dealt = sharedPref.getBoolean(context.getString(R.string.pref_key_dealt), false);
            game = new Ingeldop(deck, hand, sel, dealt);

        } catch (JSONException e) {
            newGame();
        }
    }
}

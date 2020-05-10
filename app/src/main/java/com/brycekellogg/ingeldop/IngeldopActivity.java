package com.brycekellogg.ingeldop;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/* Main activity for Ingeldop. The game is a single activity app, where all interaction
 * is done in the main activity. We have some popups and dialogs for things such as
 * errors, settings, and stats.  */
public class IngeldopActivity extends AppCompatActivity {
    public Ingeldop game;
    public double scale;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.gamelayout);  // Set layout
        restoreState();          // Try to load saved state
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


    /* Accessor function for getting the scale. This
     * value is used to scale the UI elements including
     * the deal button, discard button, and hand. A
     * default value of 1.0 means no scaling compared
     * to the default sizes specified in the resources.  */
    public double getScale() {
        return scale;
    }


    /* Increase the UI scale by one step. This causes a zoom in
     * by some amount. If we have reached the limit to how far
     * we can zoom in, we disable the zoom in button. The zoom
     * limits, zoom step size, and button disable alpha
     * value can all be configured in the resources.  */
    public void scaleInc() {
        // Load scale values
        double maxScale = Double.parseDouble(getString(R.string.maxScale));
        double stepScale = Double.parseDouble(getString(R.string.stepScale));
        float disableAlpha = Float.parseFloat(getString(R.string.disableAlpha));

        // Find menu items
        ActionMenuItemView zoomout = (ActionMenuItemView) findViewById(R.id.zoomOut);
        ActionMenuItemView zoomin = (ActionMenuItemView) findViewById(R.id.zoomIn);

        // Scale and enable/disable
        scale *= (1+stepScale);
        zoomout.setEnabled(true);
        zoomout.setAlpha(1.0F);
        if (scale >= maxScale) {
            zoomin.setEnabled(false);
            zoomin.setAlpha(disableAlpha);
        }
    }


    /* Decrease the UI scale by one step. This causes a zoom out
     * by some amount. If we have reached the limit to how far
     * we can zoom out, we disable the zoom out button. The
     * zoom limits, zoom step size, and button disable alpha
     * value can all be configured in the resources.  */
    public void scaleDec() {
        // Load scale values
        double minScale = Double.parseDouble(getString(R.string.minScale));
        double stepScale = Double.parseDouble(getString(R.string.stepScale));
        float  disableAlpha = Float.parseFloat(getString(R.string.disableAlpha));

        // Find menu items
        ActionMenuItemView zoomout = (ActionMenuItemView) findViewById(R.id.zoomOut);
        ActionMenuItemView zoomin = (ActionMenuItemView) findViewById(R.id.zoomIn);

        // Scale and enable/disable
        scale *= (1-stepScale);
        zoomin.setEnabled(true);
        zoomin.setAlpha(1.0F);
        if (scale <= minScale) {
            zoomout.setEnabled(false);
            zoomout.setAlpha(disableAlpha);
        }

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


    void saveState() {
        // Serialize current state to JSON
        JSONObject state = new JSONObject();
        try {
            state.put("zoom", this.scale);
            state.put("game", this.game.toJSON());
            Log.i("SaveState", state.toString());
        } catch (JSONException e) {
            Log.e("SaveState", "Exception", e);
        }

        // Save JSON to internal storage
        try {
            File file = new File(this.getFilesDir(), "save.json");
            FileWriter writer = new FileWriter(file);
            writer.append(state.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("SaveState", "Exception", e);
        }
    }

    void restoreState() {
        File file = new File(this.getFilesDir(), "save.json");
        try {
            // Read in file
            Scanner reader = new Scanner(file);
            StringBuilder string = new StringBuilder();
            while (reader.hasNextLine()) {
                string.append(reader.nextLine());
            }
            reader.close();

            // Convert to JSON and restore
            JSONObject state = new JSONObject(string.toString());
            this.game = Ingeldop.fromJSON(state.getJSONObject("game"));
            this.scale = state.getDouble("zoom");
            findViewById(R.id.dealButton).requestLayout();;


            Log.i("restoreState", string.toString());
        } catch (FileNotFoundException | JSONException e) {
            Log.i("restoreState", "ignoring state - EXCEPTION");
            newGame();
            scale = 1;
            findViewById(R.id.dealButton).requestLayout();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        saveState();
    }

}



package com.brycekellogg.ingeldop;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    public void updateScaleMenu() {
        // Load scale values
        double minScale = Double.parseDouble(getString(R.string.minScale));
        double maxScale = Double.parseDouble(getString(R.string.maxScale));
        float disableAlpha = Float.parseFloat(getString(R.string.disableAlpha));

        // Find menu items
        ActionMenuItemView zoomout = (ActionMenuItemView) findViewById(R.id.zoomOut);
        ActionMenuItemView zoomin = (ActionMenuItemView) findViewById(R.id.zoomIn);

        // Update enabled state
        zoomout.setEnabled(scale > minScale);
        zoomin.setEnabled(scale < maxScale);

        // Update icon
        if (zoomout.isEnabled())  zoomout.setAlpha(1.0F);
        if (zoomin.isEnabled())   zoomin.setAlpha(1.0F);
        if (!zoomout.isEnabled()) zoomout.setAlpha(disableAlpha);
        if (!zoomin.isEnabled())  zoomin.setAlpha(disableAlpha);
    }

    /* Increase the UI scale by one step. This causes a zoom in
     * by some amount. If we have reached the limit to how far
     * we can zoom in, we disable the zoom in button. The zoom
     * limits, zoom step size, and button disable alpha
     * value can all be configured in the resources.  */
    public void scaleInc() {
        // Scale and update
        double stepScale = Double.parseDouble(getString(R.string.stepScale));
        scale *= (1+stepScale);
        updateScaleMenu();
    }


    /* Decrease the UI scale by one step. This causes a zoom out
     * by some amount. If we have reached the limit to how far
     * we can zoom out, we disable the zoom out button. The
     * zoom limits, zoom step size, and button disable alpha
     * value can all be configured in the resources.  */
    public void scaleDec() {
        // Scale and update
        double stepScale = Double.parseDouble(getString(R.string.stepScale));
        scale *= (1-stepScale);
        updateScaleMenu();
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
        // Build file contents
        StringBuilder out = new StringBuilder();
        out.append("game:" + game + '\n');
        out.append("scale:" + scale + '\n');

        // Write to file
        File file = new File(this.getFilesDir(), getString(R.string.saveFilename));
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(out.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("saveState()", "ERROR writing file", e);
        }
    }

    void restoreState() {
        File file = new File(this.getFilesDir(), getString(R.string.saveFilename));
        try {
            // Read file into string
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String s = reader.nextLine();

                // Parse into fields
                String[] keyvalue = s.split(":");
                String key = keyvalue[0];
                String val = keyvalue[1];

                // Extract saved state
                switch (key) {
                    case "scale": scale = Double.parseDouble(val); break;
                    case "game": game = Ingeldop.parseString(val); break;
                }
            }

        } catch (FileNotFoundException e) {
            Log.i("restoreState()", "no saved state");
            newGame();
            scale = 1;
        }
        updateScaleMenu();
        update();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        saveState();
    }

}



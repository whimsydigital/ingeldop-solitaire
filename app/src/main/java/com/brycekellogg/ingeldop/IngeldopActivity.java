package com.brycekellogg.ingeldop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

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
    public int zoomPercent;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.gamelayout);

        // Setup toolbar actions
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.newGame:  newGame();          break;
                    case R.id.zoomIn:   zoom(++zoomPercent); break;
                    case R.id.zoomOut:  zoom(--zoomPercent); break;
                    case R.id.stats:    break;
                    case R.id.settings: break;
                }
                return true;
            }
        });


        // Try to load saved state
        restoreState();
        Log.i("zoom", Integer.toString(this.zoomPercent));
        Log.i("handSize", Integer.toString(this.game.handSize()));

        // Update layout
        findViewById(R.id.layout).requestLayout();
        findViewById(R.id.layout).invalidate();
        findViewById(R.id.handView).requestLayout();
        findViewById(R.id.handView).invalidate();
    }


    /* Starts a new game, resets deal button image to full deck, and
     * requests a redraw of the hand view.  */
    void newGame() {
        // Start a new game
        this.game = new Ingeldop();

        // Reset deal button state
        DealButton dealButton = (DealButton) findViewById(R.id.dealButton);
        dealButton.setEmpty(false);
        dealButton.setEnabled(true);

        // Redraw the hand view
        findViewById(R.id.handView).requestLayout();
        findViewById(R.id.handView).invalidate();
    }


    void zoom(int zoomPercent) {
        // Set deal button scale
        DealButton dealButton = (DealButton) findViewById(R.id.dealButton);
        dealButton.setScale(zoomPercent/100.0);

        // Update layout
        findViewById(R.id.layout).requestLayout();
        findViewById(R.id.layout).invalidate();
    }

    void saveState() {
        // Serialize current state to JSON
        JSONObject state = new JSONObject();
        try {
            state.put("zoom", this.zoomPercent);
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
            this.zoomPercent = state.getInt("zoom");
            zoom(zoomPercent);


            Log.i("restoreState", string.toString());
        } catch (FileNotFoundException | JSONException e) {
            Log.i("restoreState", "ignoring state - EXCEPTION");
            newGame();
            zoomPercent = 15;
            zoom(zoomPercent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        saveState();
    }

}



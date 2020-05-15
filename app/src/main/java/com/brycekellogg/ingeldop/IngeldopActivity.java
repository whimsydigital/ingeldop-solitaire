package com.brycekellogg.ingeldop;


import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

/* Main activity for Ingeldop. The game is a single activity app, where all interaction
 * is done in the main activity. We have some popups and dialogs for things such as
 * errors, settings, and stats.  */
public class IngeldopActivity extends AppCompatActivity implements View.OnClickListener {
    public Ingeldop game;



    private int marginBetween;
    private int marginBetweenStep;
    private int marginBetweenMin;
    private int marginBetweenMax;

    // UI Elements
    HorizontalScrollView scrollView;
    DealButton dealButton;
    View discardButton;
    View zoomOutButton;
    View zoomInButton;
    View handView;

    // Layout params used for zooming
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

        // Save UI elements we interact with
        scrollView    = findViewById(R.id.scrollView);
        dealButton    = findViewById(R.id.dealButton);
        discardButton = findViewById(R.id.discardButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomInButton  = findViewById(R.id.zoomInButton);
        handView      = findViewById(R.id.handView);

        // Layout params for zooming
        dealButtonLayoutParams = (ConstraintLayout.LayoutParams) dealButton.getLayoutParams();


        game = new Ingeldop();
        doZoom(false, false);  // Update scale

//        restoreState();          // Try to load saved state
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dealButton:    doDeal();    break;
            case R.id.discardButton: doDiscard(); break;
            case R.id.newGameButton: newGame();   break;
            case R.id.zoomInButton:  doZoom(true, false); break;
            case R.id.zoomOutButton: doZoom(false, true); break;
            case R.id.statsButton:    break;
            case R.id.settingsButton: break;
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
            game.discard();                     // Do the discard
            handView.requestLayout();           // Update hand
            if (game.gameOver()) doGameOver();  // Handle game over
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
     * and force the containing scroll view to remove scroll bars. */
    private void newGame() {
        // Start a new game
        this.game = new Ingeldop();

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
     * in the hand (or a win message of none left).
     *
     * TODO: save game statistics  */
    private void doGameOver() {
        // Disable deal & discard buttons
        dealButton.setEnabled(false);
        discardButton.setEnabled(false);

        // Show game over message
        String msg;
        if (game.handSize() == 0) msg = getString(R.string.gameWinText);
        else                      msg = getString(R.string.gameOverText, game.handSize());
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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



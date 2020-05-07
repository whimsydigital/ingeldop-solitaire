package com.brycekellogg.ingeldop;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;


public class DealButton extends AppCompatImageButton implements View.OnClickListener {

    public DealButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We are our own click listener
        this.setOnClickListener(this);
    }

    /* Create a custom button state for when the deck is empty. This allows us
     * to set custom drawables for this button depending on an empty state.
     * This logic doesn't directly deal with the deck itself, instead just
     * creates a state and a setter function for notifying us of emptiness.  */
    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.isEmpty) mergeDrawableStates(drawableState, STATE_EMPTY);
        return drawableState;
    }
    private boolean isEmpty = false;
    private static final int[] STATE_EMPTY = {R.attr.state_empty};
    public void setEmpty(boolean isEmpty) {this.isEmpty = isEmpty;}

    /* Processes a dealButton onClick action. We deal a card and update the button
     * state based on if the deck is empty or if the game is over. If the deck is
     * empty, we set the dealButton's empty state. If the game is over, we set
     * the dealButtons enabled state to false. We then request a redraw of the
     * hand view. Additionally, displays a notification if the game is over. */
    @Override
    public void onClick(View v) {
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Do the deal
        game.deal();

        // If deck is empty, change deal button image
        if (game.deckSize() == 0) {
            this.setEmpty(true);
        }

        // If game is over, change deal button image and display alert
        if (game.gameOver()) {
            this.setEnabled(false);
            Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();
        }

        // Redraw the hand view
        ((IngeldopActivity)getContext()).findViewById(R.id.handView).requestLayout();
        ((IngeldopActivity)getContext()).findViewById(R.id.handView).invalidate();
    }
}

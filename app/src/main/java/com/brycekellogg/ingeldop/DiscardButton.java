package com.brycekellogg.ingeldop;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

/* A custom button for discarding. It acts as it's own click listener
 * and handles calling discard on the Ingeldop game and notifying
 * the parent activity of required updates and discard exceptions.  */
public class DiscardButton extends AppCompatButton implements View.OnClickListener {

    public DiscardButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this);  // We are our own click listener
    }

    /* Process a discard action. We attempt to discard the selected cards, but if a discard
     * is not allowed due to currently selected cards, an error pop-up is displayed. If a
     * discard is allowed, the selected cards are discarded, we potentially update the
     * deal button image if the game is over, and request a redraw of the hand view. If
     * the game is over, we additionally display a popup notification. */
    @Override
    public void onClick(View v) {
        try {
            // Get game and do the discard
            Ingeldop game = ((IngeldopActivity)getContext()).game;
            game.discard();

            // Update activity
            ((IngeldopActivity) getContext()).update();

        } catch (DiscardException e) {
            // Update activity with error
            ((IngeldopActivity) getContext()).alert(e.getMessage());
        }
    }
}

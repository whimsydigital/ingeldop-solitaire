package com.brycekellogg.ingeldop;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class DiscardButton extends AppCompatButton implements View.OnClickListener {
    public DiscardButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We are our own click listener
        this.setOnClickListener(this);
    }

    /* Process a discard action. We attempt to discard the selected cards, but if a discard
     * is not allowed due to currently selected cards, an error pop-up is displayed. If a
     * discard is allowed, the selected cards are discarded, we potentially update the
     * deal button image if the game is over, and request a redraw of the hand view. If
     * the game is over, we additionally display a popup notification. */
    @Override
    public void onClick(View v) {
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        try {
            // Do the discard
            game.discard();

            // If game is over, change deal button image and display alert
            if (game.gameOver()) {
                ImageButton dealButton = (ImageButton) findViewById(R.id.dealButton);
                dealButton.setEnabled(false);
                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();
            }

            // Redraw the hand view
            ((IngeldopActivity)getContext()).findViewById(R.id.handView).requestLayout();
            ((IngeldopActivity)getContext()).findViewById(R.id.handView).invalidate();

        } catch (DiscardException e) {
            // Couldn't discard, show a notification
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

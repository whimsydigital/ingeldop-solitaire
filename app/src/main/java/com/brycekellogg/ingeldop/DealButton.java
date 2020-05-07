package com.brycekellogg.ingeldop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class DealButton extends AppCompatImageButton implements View.OnClickListener {

    public DealButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We are our own click listener
        this.setOnClickListener(this);
    }

    /* Processes a card deal action. We deal a card, update the deal button image if the
     * deck is now empty or if the game is over, and then request a redraw of the hand view.
     * Additionally, displays a notification if the game is over. */
    @Override
    public void onClick(View v) {
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Do the deal
        game.deal();

        // If deck is empty, change deal button image
        if (game.deckSize() == 0) {
            Bitmap dealImage = BitmapFactory.decodeResource(getResources(), R.drawable.deal1);
            ImageButton dealButton = (ImageButton) findViewById(R.id.dealButton);
            dealButton.setImageBitmap(dealImage);
        }

        // If game is over, change deal button image and display alert
        if (game.gameOver()) {
            Bitmap dealImage = BitmapFactory.decodeResource(getResources(), R.drawable.deal2);
            ImageButton dealButton = (ImageButton) findViewById(R.id.dealButton);
            dealButton.setImageBitmap(dealImage);
            Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();
        }

        // Redraw the hand view
        ((IngeldopActivity)getContext()).findViewById(R.id.handView).requestLayout();
        ((IngeldopActivity)getContext()).findViewById(R.id.handView).invalidate();
    }
}

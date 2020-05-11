package com.brycekellogg.ingeldop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * A View for a hand of cards that does not collapse
 *
 * This class handles laying out the cards in the hand
 * in a game of Ingeldop. It draws them as overlapping
 * cards that continue to the right indefinitely. There
 * is no action taken to constrain hand view width.
 */
public class HandViewExpanded extends View {
    private Map<Card, Drawable> spritesheet;
    private Rect dstRect = new Rect();

    private double overlap = 0.8;

    public HandViewExpanded(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Import card images from resources and
        // save them to a map for access in draw
        spritesheet = new HashMap<Card, Drawable>();
        for (Card c : Card.values()) {
            String strCard  = c.toString().toLowerCase();
            String strType  = "drawable";
            String strPkg   = getContext().getPackageName();
            int resourceID  = getResources().getIdentifier(strCard, strType, strPkg);
            Drawable resImg = getResources().getDrawable(resourceID, null);
            spritesheet.put(c, resImg);
        }

    }

    /* Calculate the destination rect for where a card in the hand should be placed. This is
       dependent on the natural aspect ratio of a playing card, the current zoom ratio, the
       index of the card in the hand, the size of overlap, and whether or not the card is selected.  */
    Rect setDstRect(int i, boolean sel) {
        // Read in resources
        int topMargin       = this.getResources().getDimensionPixelSize(R.dimen.topMargin);
        int topScrollMargin = this.getResources().getDimensionPixelSize(R.dimen.topScrollMargin);
        int cardHeight = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstHeight);
        int cardWidth  = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstWidth);
        double zoomPercent = ((IngeldopActivity)getContext()).scale;

        // Calculate rect boundaries
        int topPadding = topMargin - topScrollMargin;

        dstRect.top    = topPadding - (sel ? topPadding : 0);
        dstRect.bottom = topPadding - (sel ? topPadding : 0) + (int) (cardHeight*zoomPercent);
        dstRect.left  = (int) ((i+0)*cardWidth*zoomPercent - i*cardWidth*zoomPercent*overlap);
        dstRect.right = (int) ((i+1)*cardWidth*zoomPercent - i*cardWidth*zoomPercent*overlap);

        return dstRect;
    }


    @Override
    protected void onMeasure(int width, int height) {
        // Get reference to current game
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Calculate bottom & left bounds
        setDstRect(0, false);
        int bottom = dstRect.bottom;
        int left   = dstRect.left;

        // Calculate top & right bounds
        setDstRect(game.handSize()-1, true);
        int top = dstRect.top;
        int right = dstRect.right;

        // Calculate measurements
        int measuredWidth  = right - left;
        int measuredHeight = bottom - top;

        // Set measurements
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Can't do anything with a null canvas
        if (canvas == null) return;

        // Clear canvas with background color
        canvas.drawColor(this.getResources().getColor(R.color.background));

        // Get reference to current game
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Iterate through hand and draw each card
        for (int i = 0; i < game.handSize(); i++) {
            Card c = game.getCard(i);                        // Get the current card
            Rect r = setDstRect(i, game.isCardSelected(i));  // Get location to place card
            Drawable d = spritesheet.get(c);                 // Get the drawable for card

            // Draw card
            d.setBounds(r);
            d.draw(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP ||
            e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            Ingeldop game = ((IngeldopActivity) getContext()).game;

            // Check if we clicked on any of the cards in the hand
            for (int i = game.handSize() - 1; i >= 0; i--) {
                setDstRect(i, game.isCardSelected(i));
                if (dstRect.contains(x, y)) {
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        game.selectCard(i, !game.isCardSelected(i));
                        this.invalidate();
                    }
                    return true;
                }
            }
        }

        return false;
    }
}

package com.brycekellogg.ingeldop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import static com.brycekellogg.ingeldop.Card.*;

// TODO: REPEATED DISCARDS WORK!!!!!!!! Make it so they don't

/**
 * A View for a hand of cards.
 *
 * This class handles laying out the cards in the hand
 * in a game of Ingeldop.
 */
public class HandView extends View {
    private double overlap = 0.25;
    private int cardWidth = 72*5;
    private int cardHeight = 100*5;
    private int topPadding  = 20;
    private Bitmap cardImage;
    private Rect dstRect;

    public HandView(Context context, AttributeSet attr) {
        super(context, attr);
        dstRect = new Rect();


        Ingeldop game = ((MainActivity)getContext()).game;

        game.deal();
        game.deal();
        game.deal();
        game.deal();
    }

    void setDstRect(int i, boolean isSel) {
        dstRect.top    = topPadding + 0;
        dstRect.bottom = topPadding + cardHeight;

        dstRect.left  = (int) ((i+0)*cardWidth*overlap);
        dstRect.right = (int) ((i+1)*cardWidth*overlap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Can't do anything with a null canvas
        if (canvas == null) return;

        // Clear canvas with white background
        canvas.drawColor(Color.WHITE);

        Ingeldop game = ((MainActivity)getContext()).game;

        // Iterate through hand and draw each card
        for (int i = 0; i < game.handSize(); i++) {
            Card c = game.getCard(i);
            setDstRect(i, game.isCardSelected(i));
            cardImage  = BitmapFactory.decodeResource(this.getResources(), R.drawable.club_q);
            canvas.drawBitmap(cardImage, null, dstRect, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            Ingeldop game = ((MainActivity) getContext()).game;

//            // Check if we clicked on any of the cards in the hand
//            for (int i = game.handSize() - 1; i >= 0; i--) {
//                if (handDstRects[i].contains(x, y)) {
//                    game.selectCard(i, !game.isCardSelected(i));
//                    updateHandDstRects();
//                    this.invalidate();
//                    return true;
//                }
//            }
        }

        return false;
    }
}

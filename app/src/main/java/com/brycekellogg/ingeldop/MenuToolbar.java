package com.brycekellogg.ingeldop;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;

public class MenuToolbar extends Toolbar implements Toolbar.OnMenuItemClickListener {
    public MenuToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inflateMenu(R.menu.menu);
        this.setOnMenuItemClickListener(this);  // We are our own click listener
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        IngeldopActivity context = (IngeldopActivity) getContext();
        switch (menuItem.getItemId()) {
            case R.id.newGame:  context.newGame();  break;
            case R.id.zoomIn:   context.scaleInc(); break;
            case R.id.zoomOut:  context.scaleDec(); break;
            case R.id.stats:    break;
            case R.id.settings: break;
        }
        context.update();
        return true;
    }
}

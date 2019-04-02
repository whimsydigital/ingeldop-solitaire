/* Ingeldop Terminal Interface
 *
 * Author: Bryce Kellogg
 * Copyright: 2019 Bryce Kellogg
 * License: Proprietary
 */
#include <iostream>
#include <ncurses.h>

#include "Ingeldop.hpp"

#define RED_CARD    1
#define BLACK_CARD  2


/* Uses ncurses to print a Unicode glyph for the
 * specified Card at the chosen x,y location. */
void printCard(Card c, int x, int y, bool sel) {
    // Choose Unicode glyph based on Card
    wchar_t unicard = L'\U0001F000' + c.rank + L"\xD0\xC0\xB0\xA0"[c.suit];

    // Assign attributes
    int attrs = 0;
    if (c.suit == diamonds || c.suit == hearts) attrs = COLOR_PAIR(RED_CARD);
    if (c.suit == spades   || c.suit == clubs)  attrs = COLOR_PAIR(BLACK_CARD);
    if (sel) y--;

    // Draw Card
    attron(attrs);               // Set attributes on
    mvaddnwstr(y,x,&unicard,1);  // Print Unicode character
    mvaddnstr(y,x+1," ",1);      // Hack to print full character TODO: not always working
    attroff(attrs);              // Set attributes off
}


/* Uses ncurses to print out the hand of
 * a given Ingeldop game at a given starting
 * x,y location. */
void printHand(Ingeldop* game, int x, int y) {
    for (int i = 0; i < game->handSize(); i++) {
        printCard(game->cardAt(i), x+2*i, y, game->isSelected(i)); // TODO: change y based on selected
    }
}

/* Uses ncurses to print out a cursor showing
 * the location of the cursor for selecting cards. */
void printCursor(int x, int y) {
    wchar_t cursor = L'\u21C8';
    mvaddnwstr(y,x,&cursor,1);  // Print Unicode character
}


int main() {

    setlocale(LC_ALL, ""); // for unicode support
    initscr();
    refresh();
    raw();
    keypad(stdscr, TRUE);
    noecho();
    curs_set(0);

    start_color();
	init_pair(RED_CARD, COLOR_RED, COLOR_BLACK);
	init_pair(BLACK_CARD, COLOR_WHITE, COLOR_BLACK);

    Ingeldop* game = new Ingeldop;
    int cursor = -1;

    bool notDone = true;
    while (notDone) {

        int cmd = getch();
        switch (cmd) {
            case 'q':       notDone = false; break;
            case KEY_LEFT:  cursor--; break;
            case KEY_RIGHT: cursor++; break;
            case KEY_UP:    game->select(cursor, true);   break;
            case KEY_DOWN:  game->select(cursor, false); break;
            case ' ':       game->deal();           break;
            case '\n':      game->discard();        break;
            default: break;  // Do nothing
        }
        
        erase();
        printHand(game, 5, 12);
        printCursor(5+2*cursor, 13);
        refresh();
    }

    endwin();

    delete game;
}





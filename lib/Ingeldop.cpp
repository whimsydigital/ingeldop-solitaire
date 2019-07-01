/* Ingeldop
 *
 * Author: Bryce Kellogg
 * Copyright: 2019 Bryce Kellogg
 * License: Proprietary
 */
#include <algorithm>
#include <random>
#include <vector>

#include "Ingeldop.hpp"

#include <iostream>
using std::vector;
using std::cout;
using std::endl;

Ingeldop::Ingeldop(Card seedDeck[], int seedSize) {
    this->deckSize = 0;
    for (int i = 0; i < seedSize; i++) {
        this->deck[i] = seedDeck[i];
        this->deckSize++;
    }
}

Ingeldop::Ingeldop() {

    // Default un-shuffled deck
    vector<Card> tmpDeck = {CLUB_A,  DIAMOND_A,  HEART_A,  SPADE_A,
                            CLUB_2,  DIAMOND_2,  HEART_2,  SPADE_2,
                            CLUB_3,  DIAMOND_3,  HEART_3,  SPADE_3,
                            CLUB_4,  DIAMOND_4,  HEART_4,  SPADE_4,
                            CLUB_5,  DIAMOND_5,  HEART_5,  SPADE_5,
                            CLUB_6,  DIAMOND_6,  HEART_6,  SPADE_6,
                            CLUB_7,  DIAMOND_7,  HEART_7,  SPADE_7,
                            CLUB_8,  DIAMOND_8,  HEART_8,  SPADE_8,
                            CLUB_9,  DIAMOND_9,  HEART_9,  SPADE_9,
                            CLUB_10, DIAMOND_10, HEART_10, SPADE_10,
                            CLUB_J,  DIAMOND_J,  HEART_J,  SPADE_J,
                            CLUB_Q,  DIAMOND_Q,  HEART_Q,  SPADE_Q,
                            CLUB_K,  DIAMOND_K,  HEART_K,  SPADE_K};

    // Shuffle deck
    std::random_device rd;
    std::mt19937 g(rd());
    std::shuffle(tmpDeck.begin(), tmpDeck.end(), g);
   
    // Copy to deck
    this->deckSize = 0;
    for (int i = 0; i < tmpDeck.size(); i++) {
        this->deck[i] = tmpDeck[i];
        this->deckSize++;
    }
}

void Ingeldop::deal() {
    if (this->getDeckSize() != 0) {
        Card c = this->deck[--this->deckSize];  // Get Card from deck
        this->hand[this->handSize] = c;         // Stick it in hand
        this->sel[this->handSize] = false;      // Set not selected
        this->handSize++;                       // Update hand size
    } else {
        // Save Card and selection
        Card c = this->hand[0];
        bool s = this->sel[0];
        
        // Shift hand
        for (int i = 0; i < this->handSize-1; i++) {
            this->hand[i] = this->hand[i+1];
            this->sel[i] = this->sel[i+1];
        }

        // Stick Card and selection back in hand
        this->hand[this->handSize-1] = c;
        this->sel[this->handSize-1] = s;
    }
}


bool Ingeldop::discard() {

    // Get indices of top four cards
    int i0 = this->handSize - 1;
    int i1 = this->handSize - 2;
    int i2 = this->handSize - 3;
    int i3 = this->handSize - 4;

    // Get card selection masks
    bool sel_1111 =  isSelected(i0) && isSelected(i1) &&  isSelected(i2) &&  isSelected(i3);
    bool sel_1100 =  isSelected(i0) && isSelected(i1) && !isSelected(i2) && !isSelected(i3);
    bool sel_0110 = !isSelected(i0) && isSelected(i1) &&  isSelected(i2) && !isSelected(i3);
   
    // Get card matching masks
    bool suit_1111 = suit(cardAt(i0)) == suit(cardAt(i1)) &&
                     suit(cardAt(i0)) == suit(cardAt(i2)) &&
                     suit(cardAt(i0)) == suit(cardAt(i3));
    bool suit_1001 = suit(cardAt(i0)) == suit(cardAt(i3));
    bool rank_1001 = rank(cardAt(i0)) == rank(cardAt(i3));
    bool rank_1100 = rank(cardAt(i0)) == rank(cardAt(i1));
   
    // Discard
    if (numSel == 4 && sel_1111 && suit_1111) {
        this->handSize -= 4;
        this->numSel   -= 4;
        return true;
    }
    
    if (numSel == 2 && sel_1100 && rank_1100) {
        this->handSize -= 2;
        this->numSel   -= 2;
        return true;
    }
    
    if (numSel == 2 && handSize >= 3 && sel_0110 && (rank_1001 || suit_1001)) {
        this->hand[i2] = this->hand[i0];
        this->sel[i2]  = this->sel[i0];
        this->handSize -= 2;
        this->numSel   -= 2;
        return true;
    } 
    return false;    
}


Card Ingeldop::cardAt(int i) {
    if (0 <= i && i < this->handSize) {
        return this->hand[i];
    } else {
        return ERR_NOCARD;
    }
}


void Ingeldop::select(uint8_t i, bool sel) {
    if (i >= this->handSize) {
        throw std::out_of_range ("blah");
    } else {
        if (sel && !this->sel[i]) {
            this->numSel++;
        } else if (!sel && this->sel[i]) {
            this->numSel--;
        }
        this->sel[i] = sel;
    }
}


bool Ingeldop::isSelected(int i) {
    if (0 <= i && i < this->handSize) {
        return this->sel[i];
    } else {
        return false;
    }
}
    
uint8_t Ingeldop::getHandSize() {
    return this->handSize;
}

uint8_t Ingeldop::getDeckSize() {
    return this->deckSize;
}


bool Ingeldop::gameOver() {
    // Never over if still cards in deck
    if (deckSize != 0) return false;

    for (int i = 0; i < handSize; i++) {
        int i0 = (i+0) % handSize;
        int i1 = (i+1) % handSize;
        int i2 = (i+2) % handSize;
        int i3 = (i+3) % handSize;

        Card c0 = cardAt(i0);
        Card c1 = cardAt(i1);
        Card c2 = cardAt(i2);
        Card c3 = cardAt(i3);

        if (handSize >= 2 && rank(c0) == rank(c1)) return false; // Check for pairs
        if (handSize >= 4 && rank(c0) == rank(c3)) return false; // Check between pairs
        if (handSize >= 4 && suit(c0) == suit(c3)) return false; // Check between suits
    }

    return true;
}

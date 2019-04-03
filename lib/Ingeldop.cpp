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

using std::vector;

Ingeldop::Ingeldop(const vector<Card>& seedDeck) {
    this->deck = vector<Card>(seedDeck);
}

Ingeldop::Ingeldop() {

    // Default un-shuffled deck
    deck = {CLUB_A,  DIAMOND_A,  HEART_A,  SPADE_A,
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
    std::shuffle(deck.begin(), deck.end(), g);
}


void Ingeldop::deal() {
    if (this->deck.empty()) {
        hand.push_front(hand.back());
        hand.pop_back();
        selected.push_front(selected.back());
        selected.pop_back();
    } else {
        hand.push_front(deck.back());
        selected.push_front(false);
        deck.pop_back();
    }
}


void Ingeldop::discard() {

    // TODO: add numSelected member
    int numSelected = count(this->selected.begin(),
                            this->selected.end(),
                            true);
    if (numSelected == 4 &&
        isSelected(0) &&
        isSelected(1) &&
        isSelected(2) &&
        isSelected(3) &&
        suit(cardAt(0)) == suit(cardAt(1)) &&
        suit(cardAt(0)) == suit(cardAt(2)) &&
        suit(cardAt(0)) == suit(cardAt(3))) {

        hand.erase(hand.begin()+3);
        hand.erase(hand.begin()+2);
        hand.erase(hand.begin()+1);
        hand.erase(hand.begin()+0);
        selected.erase(selected.begin()+3);
        selected.erase(selected.begin()+2);
        selected.erase(selected.begin()+1);
        selected.erase(selected.begin()+0);
        
        return;
    }
    
    if (numSelected == 2 &&
        isSelected(0) &&
        isSelected(1) &&
        rank(cardAt(0)) == rank(cardAt(1))) {

        hand.erase(hand.begin()+1);
        hand.erase(hand.begin()+0);
        selected.erase(selected.begin()+1);
        selected.erase(selected.begin()+0);

        return;
    }
    
    if (numSelected == 2 &&
        this->handSize() >= 3 &&
        isSelected(1) &&
        isSelected(2) &&
        rank(cardAt(0)) == rank(cardAt(3))) {

        hand.erase(hand.begin()+2);
        hand.erase(hand.begin()+1);
        selected.erase(selected.begin()+2);
        selected.erase(selected.begin()+1);

        return;
    } 
    
    if (numSelected == 2 &&
        this->handSize() >= 3 &&
        isSelected(1) &&
        isSelected(2) &&
        suit(cardAt(0)) == suit(cardAt(3))) {

        hand.erase(hand.begin()+2);
        hand.erase(hand.begin()+1);
        selected.erase(selected.begin()+2);
        selected.erase(selected.begin()+1);

        return;
    }

    throw discardException();
}


Card Ingeldop::cardAt(uint8_t i) {
    return this->hand.at(i);
}


void Ingeldop::select(uint8_t i, bool sel) {
    this->selected.at(i) = sel;
}


bool Ingeldop::isSelected(uint8_t i) {
    return this->selected.at(i);
}
    
uint8_t Ingeldop::handSize() {
    return this->hand.size();
}

uint8_t Ingeldop::deckSize() {
    return this->deck.size();
}


bool Ingeldop::gameOver() {
    // Never over if still cards in deck
    if (this->deckSize() != 0) return false;

    for (int i = 0; i < this->handSize(); i++) {
        int i0 = (i+0) % this->handSize();
        int i1 = (i+1) % this->handSize();
        int i2 = (i+2) % this->handSize();
        int i3 = (i+3) % this->handSize();

        Card c0 = this->cardAt(i0);
        Card c1 = this->cardAt(i1);
        Card c2 = this->cardAt(i2);
        Card c3 = this->cardAt(i3);

        if (this->handSize() >= 2 && rank(c0) == rank(c1)) return false; // Check for pairs
        if (this->handSize() >= 4 && rank(c0) == rank(c3)) return false; // Check between pairs
        if (this->handSize() >= 4 && suit(c0) == suit(c3)) return false; // Check between suits
    }

    return true;
}

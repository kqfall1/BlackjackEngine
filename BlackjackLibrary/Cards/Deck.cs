//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class Deck
    {
        private List<Card> cards;
        private const byte CARDS_PER_RANK = 4; 
        private const byte CARDS_PER_SUIT = 13;
        private const byte FULL_DECK_CARD_COUNT = 52;
        private readonly Random randomGenerator; 

        internal Deck()
        {
            cards = new List<Card>();
            randomGenerator = new Random();
            Shuffle();
        }

        //FOR TESTING PURPOSES:
        internal Card DrawEight()
        {
            Card card = cards.FirstOrDefault(card => card.Rank is Rank.Eight);
            cards.Remove(card);
            return card;
        }
        internal Card DrawTenCard()
        {
            Card card = cards.FirstOrDefault(card => card.Rank is Rank.Ace || card.Rank is Rank.King || card.Rank is Rank.Queen || card.Rank is Rank.Jack || card.Rank is Rank.Ten);
            cards.Remove(card);
            return card; 
        }

        internal Card DrawCard()
        {
            if (cards.Count == 0)
            {
                throw new EmptyDeckException(); 
            }
            
            Card card = cards[0];
            cards.RemoveAt(0);
            return card; 
        }

        internal void Shuffle()
        {
            Card cardToSwap; 
            byte count; 
            byte randomSwapIndex;
            byte rankIndex;
            byte suitIndex;

            cards.Clear(); 

            for (rankIndex = 0; rankIndex < CARDS_PER_SUIT; rankIndex++)
            {
                for (suitIndex = 0; suitIndex < CARDS_PER_RANK; suitIndex++)
                {
                    cards.Add(new Card((Rank)rankIndex, (Suit)suitIndex));
                }
            }

            for (count = (byte)(FULL_DECK_CARD_COUNT - 1); count > 0; count--)
            {
                randomSwapIndex = (byte)randomGenerator.Next((byte)(count));
                cardToSwap = cards[count];
                cards[count] = cards[randomSwapIndex];
                cards[randomSwapIndex] = cardToSwap;
            }
        }

        public override string ToString()
        {
            return $"{cards.Count} cards remaining in the deck.";
        }
    }
}
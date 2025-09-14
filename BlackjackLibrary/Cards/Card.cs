//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class Card
    {
        public string CardPhotoName
        {
            get
            {
                return $"{Rank.ToString().ToLower()}_of_{Suit.ToString().ToLower()}.png";
            }
        }
        
        internal const byte DIFFERENCE_BETWEEN_HIGH_AND_LOW_ACE_VALUES = 10;
        internal readonly Rank Rank;
        internal readonly Suit Suit;

        internal byte Value
        {
            get
            {
                switch (Rank)
                {
                    case Rank.Two:
                        return 2;
                    case Rank.Three:
                        return 3;
                    case Rank.Four:
                        return 4;
                    case Rank.Five:
                        return 5;
                    case Rank.Six:
                        return 6;
                    case Rank.Seven:
                        return 7;
                    case Rank.Eight:
                        return 8;
                    case Rank.Nine:
                        return 9;
                    case Rank.Ten:
                        return 10;
                    case Rank.Jack:
                        return 10;
                    case Rank.Queen:
                        return 10;
                    case Rank.King:
                        return 10;
                    default:
                        return 11;
                }
            }
        }

        internal Card(Rank rank, Suit suit)
        {
            Rank = rank;
            Suit = suit; 
        }

        public override string ToString()
        {
            return $"{Rank.ToString().ToLower()} of {Suit.ToString().ToLower()}";
        }
    }
}
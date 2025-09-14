//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class DealerHand : Hand
    {
        internal override byte AceCount
        {
            get
            {
                if (DownCard is not null && DownCard.Rank is Rank.Ace)
                {
                    return (byte) (base.AceCount + 1); 
                }

                return base.AceCount;
            }
        }
        
        internal Card[] AllCards
        {
            get
            {
                if (DownCard is null)
                {
                    return UpCards.ToArray();
                }

                return new List<Card>(UpCards){DownCard}.ToArray();
            }
        }

        internal override byte CardValuesSum
        {
            get
            {
                if (DownCard is not null)
                {
                    return (byte)(base.CardValuesSum + DownCard.Value);
                }

                return base.CardValuesSum;
            }
        }

        internal readonly Dealer Dealer; 
        
        private Card downCard;
        public Card DownCard
        {
            get
            {
                return downCard; 
            }
            set
            {
                downCard = value;
            }
        }

        internal override bool IsBlackjack
        {
            get
            {
                if (Score is Game.BUST_SCORE_LIMIT &&
                    AceCount == 1 &&
                    AllCards.Count() == 2)
                {

                    return true;
                }

                return false;
            }
        }
        internal override bool IsBusted
        {
            get
            {
                if (Score > Game.BUST_SCORE_LIMIT)
                {
                    return true;
                }

                return false;
            }
        }

        public override Card MostRecentlyDealtCard
        {
            get
            {
                if (AllCards.Length == 2)
                {
                    return DownCard;
                }

                return base.MostRecentlyDealtCard;
            }
        }

        public override byte Score
        {
            get
            {
                return AlterScoreByReducingTheValueOfAces(CardValuesSum);
            }
        }

        internal DealerHand(Dealer dealer) : base()
        {
            Dealer = dealer;
        }

        internal override void AddCard(Card card)
        {
            try
            {
                if (!CanHit)
                {
                    throw new InvalidOperationException(MessageManager.HIT_NOT_POSSIBLE);
                }
            }
            catch (InvalidOperationException ioException)
            {
                MessageManager.ChangeLatestMessage(ioException.Message);
                return;
            }

            if (DownCard is null && UpCards.Count == 1)
            {
                DownCard = card;
                AlterHandStatusAfterAddingCard();
                OnCardAdded(new CardAddedEventArgs(this));
                return; 
            }

            base.AddCard(card);
        }

        public override string ToString()
        {
            if (DownCard is null)
            {
                return base.ToString();
            }
            
            return $"Dealer's down card: {DownCard}. {base.ToString()}";
        }
    }
}
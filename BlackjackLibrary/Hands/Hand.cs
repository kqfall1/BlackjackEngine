//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public abstract class Hand
    {
        internal virtual byte AceCount
        {
            get
            {
                byte aceCount = 0;

                foreach (Card card in UpCards)
                {
                    switch (card.Rank)
                    {
                        case Rank.Ace:
                            aceCount++;
                            break;
                    }
                }

                return aceCount;
            }
        }

        public EventHandler<DrawingRoundFinalizedEventArgs>? DrawingRoundFinalized; 
        public EventHandler<CardAddedEventArgs>? CardAdded;

        internal virtual bool CanHit
        {
            get
            {
                return !IsBusted; 
            }
        }

        internal virtual byte CardValuesSum
        {
            get
            {
                byte cardValuesSum = 0;

                foreach (Card upCard in UpCards)
                {
                    cardValuesSum += upCard.Value; 
                }

                return cardValuesSum;
            }
        }

        internal bool ContainsSoftAce
        {
            get
            {
                if (CardValuesSum != Score)
                {
                    return true;
                }

                return false;
            }
        }

        internal virtual bool IsBlackjack
        {
            get
            {
                if (Score is Game.BUST_SCORE_LIMIT && 
                    AceCount == 1 && 
                    UpCards.Count == 2)
                {
                    return true; 
                }

                return false; 
            }
        }

        internal virtual bool IsBusted
        {
            get
            {
                return Score is not 0 &&
                       Score > Game.BUST_SCORE_LIMIT;
            }
        }

        public virtual Card MostRecentlyDealtCard
        {
            get
            {
                return UpCards[UpCards.Count - 1];
            }
        }

        public virtual byte Score
        {
            get
            {
                return AlterScoreByReducingTheValueOfAces(CardValuesSum);
            }
        }

        private HandStatus status; 
        public HandStatus Status
        {
            get
            {
                return status;
            }
            set
            {
                status = value;
            }
        }

        private List<Card> upCards;
        public List<Card> UpCards
        {
            get
            {
                return upCards;
            }
            internal set
            {
                upCards = value;
            }
        }

        internal Hand()
        {
            UpCards = new List<Card>();
            Status = HandStatus.WaitingToDraw;
        }

        internal virtual void AddCard(Card card)
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
            
            UpCards.Add(card);
            AlterHandStatusAfterAddingCard(); 
            OnCardAdded(new CardAddedEventArgs(this)); 
        }

        protected void AlterHandStatusAfterAddingCard()
        {
            if (IsBlackjack)
            {
                Status = HandStatus.Blackjack;
            }
            else if (IsBusted)
            {
                Status = HandStatus.Busted;
                OnDrawingRoundFinalized(new DrawingRoundFinalizedEventArgs(this));
            }
        }
        internal byte AlterScoreByReducingTheValueOfAces (byte cardValuesSum)
        {
            byte scoreAdjustmentCount = 0;

            while (cardValuesSum > 21 && scoreAdjustmentCount < AceCount)
            {
                cardValuesSum -= Card.DIFFERENCE_BETWEEN_HIGH_AND_LOW_ACE_VALUES;
                scoreAdjustmentCount++;
            }

            return cardValuesSum; 
        }

        internal void OnDrawingRoundFinalized(DrawingRoundFinalizedEventArgs e)
        {
            DrawingRoundFinalized?.Invoke(this, e);
        }

        protected void OnCardAdded(CardAddedEventArgs e)
        {
            CardAdded?.Invoke(this, e);
        }

        internal void Stand()
        {
            Status = HandStatus.Standing;
            OnDrawingRoundFinalized(new DrawingRoundFinalizedEventArgs(this));
        }

        public virtual string ToString()
        {
            byte count;
            string endingCharacters = ", "; 
            string handString = $"Up cards: ";

            for (count = 0; count < UpCards.Count; count++)
            {
                if (count == UpCards.Count - 1)
                {
                    endingCharacters = "."; 
                }

                handString = $"{handString}{UpCards[count]}{endingCharacters}";
            }

            return $"{handString} Score: {Score}."; 
        }
    }
}
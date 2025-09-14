//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class Dealer : BlackjackEntity
    {
        private readonly Deck deck;

        internal bool MustHit
        {
            get
            {
                return MainHand.Score < Game.DEALER_MINIMUM_SCORE_TO_STAND ||
                       (MainHand.CardValuesSum == 17 &&
                       MainHand.ContainsSoftAce); 
            }
        }

        internal Dealer() : base()
        {
            deck = new Deck();
            MainHand = new DealerHand(this);
        }
        internal void Deal(PlayerHand playerMainHand) 
        {
            DealerHand dealerHand = MainHand as DealerHand;

            //Hit(playerMainHand);
            playerMainHand.AddCard(deck.DrawEight());
            Hit(dealerHand);
            //dealerHand.AddCard(deck.DrawAce());
            //Hit(playerMainHand);
            playerMainHand.AddCard(deck.DrawEight());
            Hit(dealerHand);
            //dealerHand.AddCard(deck.DrawTenCard());
            MainHand = dealerHand;
        }

        internal void Hit(Hand hand)
        {
            hand.AddCard(deck.DrawCard());
        }

        internal bool ShouldDealerPlay(Player player, PlayerHand playerMainHand)
        {
            return player is not null &&
                   playerMainHand is not null &&
                   !player.IsPlaying &&
                   player.HasAnEligibleHandToInduceDealerPlay &&
                   !playerMainHand.IsBlackjack &&
                   playerMainHand.Status is not HandStatus.Surrendered &&
                   MainHand.Status == HandStatus.WaitingToDraw &&
                   MustHit; 
        }

        internal void Shuffle()
        {
            deck.Shuffle(); 
        }

        public override string ToString()
        {
            return $"{base.ToString()}{deck}"; 
        }
    }
}
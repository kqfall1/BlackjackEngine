//Quinn Keenan, 301504914, 19/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.Metadata;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class Game
    {
        internal const byte BUST_SCORE_LIMIT = 21;
        internal const byte DEALER_MINIMUM_SCORE_TO_STAND = 17;
        internal readonly BlackjackEntitiesController EntitiesController;

        internal bool IsActive
        {
            get
            {
                return EntitiesController.Dealer.ChipAmount > 0 && EntitiesController.Player.ChipAmount > 0;
            }
        }

        internal Game()
        {
            EntitiesController = new BlackjackEntitiesController();
            EntitiesController.Dealer.MainHand.DrawingRoundFinalized += (object sender, DrawingRoundFinalizedEventArgs e) => FinalizeDrawingRound(sender, e);
            EntitiesController.Player.MainHand.DrawingRoundFinalized += (object sender, DrawingRoundFinalizedEventArgs e) => FinalizeDrawingRound(sender, e);
            InsuranceManager.InsuranceBetPayoutOccurred += (object sender, InsuranceBetPayoutOccurredEventArgs e) => AlterConditionsAfterInsuranceBet(sender, e);
        }

        public void AlterConditionsAfterDrawingRound(object sender)
        {
            if (sender as PlayerHand == EntitiesController.Player.SplitHand)
            {
                EntitiesController.Player.MainHand.Status = HandStatus.Drawing;
                EntitiesController.OnPlayerDrawingHandSet(new PlayerDrawingHandSetEventArgs(EntitiesController.Player.MainHand as PlayerHand));
            }
            else if (sender as PlayerHand == EntitiesController.Player.MainHand)
            {
                DealerPlay();
                ShowdownPlayerHands();
                ResetConditionsAfterBettingRound();
            }
        }

        private void AlterConditionsAfterInsuranceBet(object sender, InsuranceBetPayoutOccurredEventArgs e)
        {
            if (e.Won)
            {
                FinalizeDrawingRound(sender, e); 
            }
        }

        private void DealerPlay()
        {
            if (EntitiesController.Dealer.ShouldDealerPlay(EntitiesController.Player, EntitiesController.Player.MainHand as PlayerHand))
            {
                while (EntitiesController.Dealer.MustHit)
                {
                    EntitiesController.ExecuteDealerAction();
                }

                if (!EntitiesController.Dealer.MainHand.IsBusted)
                {
                    EntitiesController.Dealer.MainHand.Stand();
                }
            }
        }

        internal void FinalizeDrawingRound(object sender, EventArgs e)
        {
            PlayerHand playerHand = null; 
            
            if (e is DrawingRoundFinalizedEventArgs drawingRoundFinalizedEventArgs)
            {
                playerHand = drawingRoundFinalizedEventArgs.Hand as PlayerHand;
            }
            else if (e is InsuranceBetPayoutOccurredEventArgs insuranceBetPayoutOccurredEventArgs)
            {
                playerHand = insuranceBetPayoutOccurredEventArgs.Player.MainHand as PlayerHand;
                playerHand.Status = HandStatus.Standing; 
            }

            AlterConditionsAfterDrawingRound(sender); 
        }

        internal void ResetConditionsAfterBettingRound()
        {
            if (!IsActive)
            {
                if (EntitiesController.Dealer.ChipAmount <= 0)
                {
                    EntitiesController.OnBlackjackEntityBankrupts(new BlackjackEntityBankruptsEventArgs(EntitiesController.Player, EntitiesController.Dealer));
                }
                else
                {
                    EntitiesController.OnBlackjackEntityBankrupts(new BlackjackEntityBankruptsEventArgs(EntitiesController.Dealer, EntitiesController.Player));
                }

                return; 
            }

            EntitiesController.Dealer.MainHand = new DealerHand(EntitiesController.Dealer);
            EntitiesController.Dealer.MainHand.DrawingRoundFinalized += (object sender, DrawingRoundFinalizedEventArgs e) => FinalizeDrawingRound(sender, e);
            EntitiesController.Player.MainHand = new PlayerHand(null, HandType.Main, EntitiesController.Player);
            EntitiesController.Player.MainHand.DrawingRoundFinalized += (object sender, DrawingRoundFinalizedEventArgs e) => FinalizeDrawingRound(sender, e);
            EntitiesController.OnPlayerDrawingHandSet(new PlayerDrawingHandSetEventArgs(EntitiesController.Player.MainHand as PlayerHand));
            EntitiesController.Player.SplitHand = null;
            EntitiesController.Dealer.Shuffle();
        }

        private void ShowdownPlayerHands()
        {
            BlackjackEntity currentHandWinner; 
            BlackjackEntity mainHandWinner = null;
            BlackjackEntity splitHandWinner = null; 
            PlayerHand[] playerHands = EntitiesController.Player.HandsInShowdownOrder;

            foreach (PlayerHand playerHand in playerHands)
            {
                if (EntitiesController.Dealer.MainHand.IsBlackjack || playerHand.IsBlackjack)
                {
                    currentHandWinner = ShowdownManager.Blackjack(EntitiesController.Dealer, playerHand);
                }
                else if (EntitiesController.Dealer.MainHand.IsBusted || playerHand.IsBusted)
                {
                    currentHandWinner = ShowdownManager.Busted(EntitiesController.Dealer, playerHand);
                }
                else if (playerHand.Status is HandStatus.Surrendered)
                {
                    currentHandWinner = ShowdownManager.Surrendered(EntitiesController.Dealer, playerHand);
                }
                else
                {
                    currentHandWinner = ShowdownManager.Normal(EntitiesController.Dealer, playerHand);
                }

                if (playerHand.HandType is HandType.Main)
                {
                    mainHandWinner = currentHandWinner; 
                }
                else
                {
                    splitHandWinner = currentHandWinner;
                }
            }

            ShowdownManager.OnShowdownOccurred(new ShowdownOccurredEventArgs(EntitiesController.Dealer.MainHand as DealerHand, EntitiesController.Player.MainHand as PlayerHand, EntitiesController.Player.SplitHand, mainHandWinner, splitHandWinner));
        }

        public override string ToString()
        {
            return $"{EntitiesController.Dealer}\n\n{EntitiesController.Player}";
        }
    }
}
//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class PlayerHand : Hand
    {
        internal Bet Bet; 
        
        internal bool CanDoubleDown
        {
            get
            {
                return UpCards.Count == 2 &&
                       Status is HandStatus.Drawing &&
                       Bet.ChipAmountRequiredToDoubleDown <= Player.ChipAmount;
            }
        }

        internal bool CanSplit
        {
            get
            {
                return HandType is HandType.Main &&
                       IsPocketPair &&
                       Bet.ChipAmount <= Player.ChipAmount; 
            }
        }

        public readonly HandType HandType;

        public string HandTypeString
        {
            get
            {
                return $"{HandType.ToString().ToLower()} hand";
            }
        }

        internal override bool IsBlackjack
        {
            get
            {
                return base.IsBlackjack &&
                       UpCards.Count == 2 &&
                       !Player.HasSplit; 
            }
        }

        private Bet insuranceBet;
        internal Bet InsuranceBet
        {
            get
            {
                return insuranceBet;
            }
            set
            {
                insuranceBet = value;
            }
        }

        internal bool IsPocketPair
        { 
            get
            {
                return UpCards.Count == 2 && UpCards[0].Rank == UpCards[1].Rank;
            }
        }

        internal readonly Player Player;

        internal PlayerHand(Bet bet, HandType handType, Player player) : base()
        {
            Bet = bet;
            this.HandType = handType;
            this.Player = player;
        }

        internal void DoubleDownOnBet(Dealer dealer)
        {
            if (!CanDoubleDown)
            {
                throw new InvalidOperationException("You cannot currently double down.");
            }
            else if (Bet.DealerContributionAmount(Bet.ChipAmount * 2, PayoutRatio.MAIN_BET) > dealer.ChipAmount)
            {
                throw new InsufficientChipsException(dealer, Bet.ChipAmount * 2);
            }

            Player.RemoveChips(Bet.ChipAmountRequiredToDoubleDown);
            Bet.DoubleDown();
            dealer.Hit(this);
        }

        internal void PlaceMainBet(decimal chipAmount, Dealer dealer)
        {
            Status = HandStatus.Drawing;
            Bet = Player.CreateBet(dealer, chipAmount);
        }

        internal void Split(Dealer dealer, PlayerHand playerSplitHand, Player.InvokeOnPlayerDrawingHandChangedOfTheGameClass invokeOnPlayerDrawingHandChangedOfTheGameClass)
        {
            Card splitCard = UpCards[1]; 
            UpCards.RemoveAt(1);
            dealer.Hit(this);
            Status = HandStatus.WaitingOnSplitHand;
            invokeOnPlayerDrawingHandChangedOfTheGameClass(new PlayerDrawingHandSetEventArgs(playerSplitHand));

            playerSplitHand.AddCard(splitCard);
            dealer.Hit(playerSplitHand);
            playerSplitHand.Status = HandStatus.Drawing;
        }

        internal void Surrender()
        {
            if (!Player.CanSurrender)
            {
                throw new InvalidOperationException($"You cannot currently surrender your {HandTypeString}.");
            }

            Status = HandStatus.Surrendered;
            OnDrawingRoundFinalized(new DrawingRoundFinalizedEventArgs(this));
        }

        public override string ToString()
        {
            return $"{base.ToString()}";
        }
    }
}
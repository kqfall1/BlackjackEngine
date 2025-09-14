//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class Player : BlackjackEntity
    {
        public delegate void AlterGuiAfterPlayerPlacesBetDelegate(object sender, EventArgs e);
        public EventHandler? PlayerPlacesBet;

        internal bool CanSurrender
        {
            get
            {
                return MainHand.UpCards.Count == 2 &&
                       SplitHand is null;
            }
        }

        internal bool HasAnEligibleHandToInduceDealerPlay
        {
            get
            {
                byte count; 
                Hand[] hands = HandsInShowdownOrder;

                for (count = 0; count < hands.Length; count++)
                {
                    if (!hands[count].IsBusted)
                    {
                        return true;
                    }
                }

                return false; 
            }
        }
        
        internal PlayerHand[] HandsInShowdownOrder
        {
            get
            {
                if (MainHand is not null)
                {
                    if (SplitHand is not null)
                    {
                        return [MainHand as PlayerHand, SplitHand];
                    }

                    return [MainHand as PlayerHand];
                }

                return null; 
            }
        }

        internal bool HasSplit
        {
            get
            {
                return SplitHand is not null;
            }
        }

        internal delegate void InvokeOnPlayerDrawingHandChangedOfTheGameClass(PlayerDrawingHandSetEventArgs e);

        internal bool IsPlaying
        {
            get
            {
                return MainHand.Status is HandStatus.Drawing ||
                       SplitHand?.Status is HandStatus.Drawing;
            }
        }

        private PlayerHand splitHand;
        internal PlayerHand SplitHand
        {
            get
            {
                return splitHand;
            }
            set
            {
                splitHand = value;
            }
        }

        internal Player() : base()
        {
            MainHand = new PlayerHand(null, HandType.Main, this);
        }

        internal Bet CreateBet(Dealer dealer, decimal chipAmount)
        {
            Bet bet; 
            
            if (chipAmount > ChipAmount)
            {
                throw new InsufficientChipsException(this, chipAmount);
            }
            else if (dealer.ChipAmount < chipAmount)
            {
                throw new InsufficientChipsException(dealer, chipAmount);
            }

            RemoveChips(chipAmount);
            bet = new Bet(chipAmount, new Pot() { ChipAmount = chipAmount });
            OnPlayerPlacesBet(EventArgs.Empty); 
            return bet;
        }

        internal void InitializeSplitHand(Dealer dealer)
        {
            PlayerHand playerMainHand = MainHand as PlayerHand;

            if (!playerMainHand.CanSplit)
            {
                throw new InvalidOperationException($"You cannot split hand:\n\n\t{this.ToString()}\n");
            }
            else if (playerMainHand.Bet.DealerContributionAmount(playerMainHand.Bet.ChipAmount, PayoutRatio.MAIN_BET) > dealer.ChipAmount)
            {
                throw new InsufficientChipsException(dealer, playerMainHand.Bet.ChipAmount);
            }

            SplitHand = new PlayerHand(CreateBet(dealer, playerMainHand.Bet.ChipAmount), HandType.Split, this);
            GameController.SubscribePlayerSplitHandToHandEventsAfterItsCreation(SplitHand);
        }

        private void OnPlayerPlacesBet(EventArgs e)
        {
            PlayerPlacesBet.Invoke(this, e);
        }

        internal void SplitMainHand(Dealer dealer, InvokeOnPlayerDrawingHandChangedOfTheGameClass invokeOnPlayerDrawingHandChangedOfTheGameClass)
        {
            PlayerHand playerMainHand = MainHand as PlayerHand;
            playerMainHand.Split(dealer, SplitHand, invokeOnPlayerDrawingHandChangedOfTheGameClass); 
        }

        public override string ToString()
        {
            return $"{base.ToString()}\n\nSplit hand: {SplitHand}\n\n";
        }
    }
}
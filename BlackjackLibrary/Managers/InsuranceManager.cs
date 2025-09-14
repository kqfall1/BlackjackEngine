//Quinn Keenan, 301504914, 29/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public static class InsuranceManager
    {
        public delegate void AlterGuiAfterInsuranceBetBecomesPossible(object sender, EventArgs e); 
        public delegate void AlterGuiAfterInsuranceBetIsDeclinedDelegate(object sender, EventArgs e);
        public static EventHandler<InsuranceBetPayoutOccurredEventArgs> InsuranceBetPayoutOccurred;
        public static EventHandler InsuranceBetBecomesPossible; 

        internal static bool InsuranceBetPlaced(Player player)
        {
            PlayerHand playerMainHand = player.MainHand as PlayerHand;
            return playerMainHand.InsuranceBet is not null; 
        }

        internal static bool InsuranceBetPossible(Dealer dealer, Player player)
        {
            DealerHand dealerHand = dealer.MainHand as DealerHand;
            PlayerHand playerMainHand = player.MainHand as PlayerHand;

            return playerMainHand.HandType is HandType.Main &&
                playerMainHand.UpCards.Count == 2 &&
                dealerHand.UpCards[0].Rank is Rank.Ace &&
                playerMainHand.Bet.ChipAmountRequiredToPlaceInsuranceBet <= player.ChipAmount &&
                playerMainHand.Bet.DealerContributionAmount(playerMainHand.Bet.ChipAmountRequiredToPlaceInsuranceBet, PayoutRatio.INSURANCE_BET) <= dealer.ChipAmount &&
                !InsuranceBetPlaced(player);
        }

        internal static bool InsuranceBetWon(Dealer dealer, Player player)
        {
            PlayerHand playerMainHand = player.MainHand as PlayerHand; 
            return InsuranceBetPlaced(player) && 
                   dealer.MainHand.IsBlackjack &&  
                   playerMainHand.InsuranceBet is not null;
        }

        private static void OnInsuranceBetPayoutOccurred(InsuranceBetPayoutOccurredEventArgs e, Player player)
        {
            InsuranceBetPayoutOccurred.Invoke(player, e); 
        }
        internal static void OnInsuranceBetBecomesPossible(EventArgs e)
        {
            InsuranceBetBecomesPossible.Invoke(null, e);
        }

        internal static void PlaceInsuranceBet(Dealer dealer, Player player)
        {
            PlayerHand playerMainHand = player.MainHand as PlayerHand; 
            playerMainHand.InsuranceBet = player.CreateBet(dealer, playerMainHand.Bet.ChipAmountRequiredToPlaceInsuranceBet);
        }

        internal static void ResolveInsuranceBet(Dealer dealer, Player player)
        {
            PlayerHand playerMainHand = player.MainHand as PlayerHand; 

            if (dealer.MainHand.IsBlackjack)
            {
                player.AddChips(playerMainHand.InsuranceBet.Payout(dealer, PayoutRatio.INSURANCE_BET));
                playerMainHand.OnDrawingRoundFinalized(new DrawingRoundFinalizedEventArgs(playerMainHand));
                OnInsuranceBetPayoutOccurred(new InsuranceBetPayoutOccurredEventArgs(player, true), player);
            }
            else
            {
                dealer.AddChips(playerMainHand.InsuranceBet.Pot.Scoop());
                OnInsuranceBetPayoutOccurred(new InsuranceBetPayoutOccurredEventArgs(player, false), player);
            }
        }
    }
}
//Quinn Keenan, 301504914, 29/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal static class ShowdownManager
    {
        public static EventHandler<ShowdownOccurredEventArgs>? ShowdownOccurred; 
        
        internal static BlackjackEntity Blackjack(Dealer dealer, PlayerHand playerHand)
        {
            BlackjackEntity winner; 

            if (dealer.MainHand.IsBlackjack && playerHand.IsBlackjack)
            {
                playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.PUSH));
                winner = null; 
                MessageManager.ShowdownBlackjackPush(playerHand);
            }
            else if (dealer.MainHand.IsBlackjack)
            {
                dealer.AddChips(playerHand.Bet.Pot.Scoop());
                winner = dealer;
                MessageManager.ShowdownBlackjackDealerWin(playerHand);
            }
            else if (playerHand.IsBlackjack)
            {
                playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.BLACKJACK));
                winner = playerHand.Player;
                MessageManager.ShowdownBlackjackPlayerWin(playerHand);
            }
            else
            {
                throw new InvalidOperationException(MessageManager.NO_BLACKJACK_FOUND_MESSAGE);
            }

            return winner; 
        }
        internal static BlackjackEntity Busted(Dealer dealer, PlayerHand playerHand)
        {
            string showdownString = $"Your {playerHand.HandTypeString}:";
            BlackjackEntity winner;

            if (playerHand.IsBusted)
            {
                dealer.AddChips(playerHand.Bet.Pot.Scoop());
                winner = dealer;
                MessageManager.ShowdownBustedDealerWin(playerHand);
            }
            else if (dealer.MainHand.IsBusted)
            {
                playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.MAIN_BET));
                winner = playerHand.Player;
                MessageManager.ShowdownBustedPlayerWin(dealer, playerHand);
            }
            else
            {
                throw new InvalidOperationException(MessageManager.NO_BUSTED_ENTITY_FOUND_MESSAGE);
            }

            return winner; 
        }
        internal static BlackjackEntity Normal(Dealer dealer, PlayerHand playerHand)
        {
            string showdownPreamble = MessageManager.ShowdownNormalPreamble(dealer, playerHand);
            BlackjackEntity winner; 

            if (playerHand.Score > dealer.MainHand.Score)
            {
                playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.MAIN_BET));
                winner = playerHand.Player;
                MessageManager.ShowdownNormalPlayerWin(playerHand, showdownPreamble);
            }
            else if (playerHand.Score < dealer.MainHand.Score)
            {
                dealer.AddChips(playerHand.Bet.Pot.Scoop());
                winner = dealer; 
                MessageManager.ShowdownNormalDealerWin(playerHand, showdownPreamble); 
            }
            else
            {
                playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.PUSH));
                winner = null; 
                MessageManager.ShowdownNormalPush(playerHand, showdownPreamble);
            }

            return winner; 
        }

        internal static void OnShowdownOccurred(ShowdownOccurredEventArgs e)
        {
            ShowdownOccurred.Invoke(null, e);
        }

        internal static BlackjackEntity Surrendered(Dealer dealer, PlayerHand playerHand)
        {
            playerHand.Player.AddChips(playerHand.Bet.Payout(dealer, PayoutRatio.SURRENDER));
            BlackjackEntity winner = dealer; 
            MessageManager.ShowdownSurrender(playerHand);
            return winner; 
        }
    }
}
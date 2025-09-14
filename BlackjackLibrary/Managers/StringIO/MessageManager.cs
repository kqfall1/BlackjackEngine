//Quinn Keenan, 301504914, 30/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Security;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public static class MessageManager
    {
        internal const string BET_NOT_POSSIBLE_MESSAGE = "You cannot currently place a bet.";
        public const string GREETING_PROMPT = "Welcome to blackjack! Place a bet to proceed.";
        public const string DEFAULT_DEALER_CHIPS_LABEL_TEXT = "Dealer's chips:";
        public const string DEFAULT_DEALER_SCORE_LABEL_TEXT = "Dealer's score:";
        public const string DEFAULT_PLAYER_CHIPS_LABEL_TEXT = "Player's chips:";
        public const string DEFAULT_PLAYER_SCORE_LABEL_TEXT = "Player's score:";
        public const string HIT_NOT_POSSIBLE = "You cannot currently hit on your current hand.";
        public const string PLACE_BET_PROMPT = "Place a bet";
        
        private static string latestMessage; 
        private static string LatestMessage
        {
            get
            {
                return latestMessage; 
            }
            set
            {
                latestMessage = value;
            }
        }
        public static EventHandler<GameNotificationEventArgs>? LatestMessageChanged;

        internal const string NO_BLACKJACK_FOUND_MESSAGE = "Game.ShowdownBlackjack() cannot determine which blackjack entity has a blackjack.";
        internal const string NO_BUSTED_ENTITY_FOUND_MESSAGE = "Game.ShowdownBusted() cannot determine which blackjack entity has busted."; 

        public static void ChangeLatestMessage(string message)
        {
            LatestMessage = message;
            OnLatestMessageChanged(null, new GameNotificationEventArgs(message)); 
        }

        internal static void ChipAmountBrief(Dealer dealer, Player player)
        {
            LatestMessage = $"You have {player.ChipAmount:C} in chips. The dealer has {dealer.ChipAmount:C} in chips.";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void DealBrief(DealerHand dealerHand, PlayerHand playerHand)
        {
            LatestMessage = $"Your hand:\n\n\t{playerHand.ToString()}\n\n\tDealer's up card: {dealerHand.UpCards[0]}.\n";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void DealerHandBrief(DealerHand dealerHand)
        {
            LatestMessage = $"\nDealer's hand:\n\n\t{dealerHand.ToString()}\n";
            OnLatestMessageChanged(dealerHand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void DoubleDown(PlayerHand playerHand)
        {
            LatestMessage = $"You elect to double down and your bet is now {playerHand.Bet.ChipAmount:C}. {PlayerDrawsCardString(playerHand)}";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void EntityDrawsCard(Hand hand)
        {
            if (hand is DealerHand)
            {
                LatestMessage = $"Dealer draws the {hand.MostRecentlyDealtCard}.";
            }
            else
            {
                LatestMessage = $"You draw the {hand.MostRecentlyDealtCard}. Score: {hand.Score}.";
            }

            OnLatestMessageChanged(hand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static string PlayerDrawsCardString(PlayerHand hand)
        {
            return $"You draw the {hand.MostRecentlyDealtCard}. Score: {hand.Score}.";
        }
        internal static void EntityBusts(Hand hand)
        {
            if (hand is DealerHand)
            {
                LatestMessage = $"The dealer busts on {hand.Score}.";
            }
            else
            {
                LatestMessage = $"You bust on {hand.Score}.";
            }

            OnLatestMessageChanged(hand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void EntityStands(Hand hand)
        {
            if (hand is DealerHand)
            {
                LatestMessage = $"The dealer elects to stand on {hand.Score}.";
            }
            else
            {
                LatestMessage = $"You elect to stand on {hand.Score}.";
            }

            OnLatestMessageChanged(hand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static string InsufficientChipsExceptionMessage(BlackjackEntity blackjackEntity, decimal chipAmount)
        {
            if (blackjackEntity is Dealer)
            {
                return $"The dealer doesn't have enough chips to payout a bet of {chipAmount:C}."; 
            }
            else
            {
                return $"You don't have enough chips to bet {chipAmount:C}.";
            }
        }
        internal static void InsuranceBetBrief(decimal chipAmount)
        {
            LatestMessage = $"You have placed an insurance bet of {chipAmount:C}.";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void InsuranceBetPrompt(decimal chipAmount)
        {
            LatestMessage = $"Do you wish to place an insurance bet of {chipAmount:C} (Y/N)?";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void InsuranceBetResolution(Dealer dealer, PlayerHand playerMainHand)
        {
            if (InsuranceManager.InsuranceBetWon(dealer, playerMainHand.Player))
            {
                LatestMessage = $"You win {playerMainHand.InsuranceBet.PayoutAmount(playerMainHand.InsuranceBet.ChipAmount, PayoutRatio.INSURANCE_BET):C} on your insurance bet of {playerMainHand.InsuranceBet.ChipAmount:C}";
            }
            else
            {
                 LatestMessage = $"The dealer does not have a blackjack. You forfeit your bet of {playerMainHand.InsuranceBet.ChipAmount:C}. Play on!";
            }

            OnLatestMessageChanged(playerMainHand, new GameNotificationEventArgs(LatestMessage));
        }
        internal static void MainBetBrief(PlayerHand playerHand)
        {
            LatestMessage = $"You have placed a bet of {playerHand.Bet.ChipAmount:C} on your {playerHand.HandTypeString}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }

        private static void OnLatestMessageChanged(Object sender, GameNotificationEventArgs e)
        {
            LatestMessageChanged.Invoke(sender, e);
        }

        public static void ShowdownBlackjackDealerWin(PlayerHand playerHand)
        {
            LatestMessage = $"The dealer was dealt a blackjack. You have been defeated and forfeit your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownBlackjackPlayerWin(PlayerHand playerHand)
        {
            LatestMessage = $"You were dealt a blackjack. You are victorious and receive {playerHand.Bet.PayoutAmount(playerHand.Bet.ChipAmount, PayoutRatio.BLACKJACK):C} from your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownBlackjackPush(PlayerHand playerHand)
        {
            LatestMessage = $"Both you and the dealer were dealt a blackjack. You have your bet of {playerHand.Bet.ChipAmount:C} pushed.";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownBustedDealerWin(PlayerHand playerHand)
        {
            LatestMessage = $"You bust with a score of {playerHand.Score}. You are defeated and forfeit your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownBustedPlayerWin(Dealer dealer, PlayerHand playerHand)
        {
            LatestMessage = $"The dealer busts with a score of {dealer.MainHand.Score}. You are victorious and receive {playerHand.Bet.PayoutAmount(playerHand.Bet.ChipAmount, PayoutRatio.MAIN_BET):C} from your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownNormalDealerWin(PlayerHand playerHand, string preamble)
        {
            LatestMessage = $"{preamble} You have been defeated and forfeit your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownNormalPlayerWin(PlayerHand playerHand, string preamble)
        {
            LatestMessage = $"{preamble} You are victorious and receive {playerHand.Bet.PayoutAmount(playerHand.Bet.ChipAmount, PayoutRatio.MAIN_BET):C} from your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static string ShowdownNormalPreamble(Dealer dealer, PlayerHand playerHand)
        {
            return $"Your score is {playerHand.Score} and the dealer's score is {dealer.MainHand.Score}.";
        }
        public static void ShowdownNormalPush(PlayerHand playerHand, string preamble)
        {
            LatestMessage = $"{preamble} You have tied the dealer's score and have your bet of {playerHand.Bet.PayoutAmount(playerHand.Bet.ChipAmount, PayoutRatio.PUSH):C} pushed.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void ShowdownSurrender(PlayerHand playerHand)
        {
            LatestMessage = $"You receive {playerHand.Bet.PayoutAmount(playerHand.Bet.ChipAmount, PayoutRatio.SURRENDER):C} from your bet of {playerHand.Bet.ChipAmount:C}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
        public static void Split(PlayerHand activePlayerHand)
        {
            LatestMessage = $"Your {activePlayerHand.HandTypeString} is now:\n\n\t{activePlayerHand.ToString()}\n";
            OnLatestMessageChanged(null, new GameNotificationEventArgs(LatestMessage));
        }
        public static void Surrender(PlayerHand playerHand)
        {
            LatestMessage = $"You elect to surrender on a score of {playerHand.Score}.";
            OnLatestMessageChanged(playerHand, new GameNotificationEventArgs(LatestMessage));
        }
    }
}
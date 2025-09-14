//Quinn Keenan, 301504914, 30/08/2025

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public static class GameController
    {
        public static CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate AlterGuiForPlayerSplitHandAfterCardAdditionEventDelegate; 

        public static bool BettingRoundIsOver
        {
            get
            {
                PlayerHand playerMainHand = game.EntitiesController.Player.MainHand as PlayerHand;
                return playerMainHand.Bet is null; 
            }
        }

        private static Game game;
        public static EventHandler? InsuranceBetIsDeclined; 

        public static bool InsuranceBetPossible
        {
            get
            {
                return InsuranceManager.InsuranceBetPossible(game.EntitiesController.Dealer, game.EntitiesController.Player);
            }
        }

        public static bool MainBetPossible
        {
            get
            {
                PlayerHand playerHand = game.EntitiesController.Player.MainHand as PlayerHand;
                return game.IsActive && playerHand.Bet is null;
            }
        }

        public static void CheckEntitiesForBlackjackBeforeDrawingRound()
        {
            if (game.EntitiesController.Dealer.MainHand.IsBlackjack || game.EntitiesController.Player.MainHand.IsBlackjack)
            {
                ShowdownManager.Blackjack(game.EntitiesController.Dealer, game.EntitiesController.Player.MainHand as PlayerHand);
            }
        }

        public static void CreateGame()
        {
            game = new Game();
        }

        public static bool TryToPlaceInsuranceBet(string inputString)
        {
            PlayerInputAbbreviation playerInputAbbreviation;
            PlayerHand playerMainHand = game.EntitiesController.Player.MainHand as PlayerHand; 

            if (StringInputValidationManager.TryParseYesOrNoInput(inputString, out playerInputAbbreviation))
            {
                switch (playerInputAbbreviation)
                {
                    case PlayerInputAbbreviation.Y:
                        game.EntitiesController.PlaceInsuranceBet();
                        InsuranceManager.ResolveInsuranceBet(game.EntitiesController.Dealer, game.EntitiesController.Player);
                        MessageManager.InsuranceBetResolution(game.EntitiesController.Dealer, playerMainHand);
                        break;
                    case PlayerInputAbbreviation.N:
                        OnInsuranceBetIsDeclined(EventArgs.Empty);
                        MessageManager.ChangeLatestMessage("");
                        break;
                }

                return true; 
            }

            return false; 
        }

        public static bool TryToPlaceMainBetAndDeal(string inputString)
        {
            decimal chipAmount;
            PlayerHand playerMainHand = game.EntitiesController.Player.MainHand as PlayerHand;

            MessageManager.ChipAmountBrief(game.EntitiesController.Dealer, game.EntitiesController.Player);

            if (MainBetPossible && StringInputValidationManager.TryParseBetInput(inputString, out chipAmount) && game.EntitiesController.TryToPlaceMainBet(chipAmount))
            {
                game.EntitiesController.Deal();

                if (InsuranceBetPossible)
                {
                    InsuranceManager.OnInsuranceBetBecomesPossible(EventArgs.Empty);
                    MessageManager.InsuranceBetPrompt(playerMainHand.Bet.ChipAmountRequiredToPlaceInsuranceBet);
                }

                return true; 
            }

            return false; 
        }

        private static void OnInsuranceBetIsDeclined(EventArgs e)
        {
            InsuranceBetIsDeclined.Invoke(game.EntitiesController.Player, e);
        }

        public static void PlayerAction(PlayerInputAbbreviation playerInputAbbreviation, PlayerHand playerHand)
        {
            game.EntitiesController.ExecutePlayerAction(playerInputAbbreviation, playerHand);
        }

        //TURN THESE INTO SOME SORT OF CLASS!
        public static void SubscribeToBlackjackEntityBankruptsEventArgs(BlackjackEntitiesController.AlterGuiAfterEntityBankruptsDelegate alterGuiAfterEntityBankruptsDelegate)
        {
            game.EntitiesController.BlackjackEntityBankrupts += (object sender, BlackjackEntityBankruptsEventArgs e) => alterGuiAfterEntityBankruptsDelegate(sender, e);
        }
        public static void SubscribeToBlackjackEntityChipAmountChangedEvents(BlackjackEntityChipAmountChangedEventArgs.ChangeEntityChipAmountGuiControlDelegate dealerChipAmountGuiControlDelegate, BlackjackEntityChipAmountChangedEventArgs.ChangeEntityChipAmountGuiControlDelegate playerChipAmountGuiControlDelegate)
        {
            game.EntitiesController.Dealer.ChipAmountChanged += (object sender, BlackjackEntityChipAmountChangedEventArgs e) => dealerChipAmountGuiControlDelegate(sender, e);
            game.EntitiesController.Player.ChipAmountChanged += (object sender, BlackjackEntityChipAmountChangedEventArgs e) => playerChipAmountGuiControlDelegate(sender, e); 
        }
        public static void SubscribeHandsToCardAdditionEvents(CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate dealerCardGuiDelegate, CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate playerCardGuiDelegate, CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate playerSplitHandGuiDelegate)
        {
            game.EntitiesController.Dealer.MainHand.CardAdded += (object sender, CardAddedEventArgs e) => dealerCardGuiDelegate(sender, e);
            game.EntitiesController.Player.MainHand.CardAdded += (object sender, CardAddedEventArgs e) => playerCardGuiDelegate(sender, e);
            //game.EntitiesController.Player.SplitHand.CardAdded += (object sender, CardAddedEventArgs e) => playerSplitHandGuiDelegate(sender, e);
            AlterGuiForPlayerSplitHandAfterCardAdditionEventDelegate = playerSplitHandGuiDelegate; 
        }
        public static void SubscribeToInsuranceBetBecomesPossibleEvents(InsuranceManager.AlterGuiAfterInsuranceBetBecomesPossible alterGuiAfterInsuranceBetBecomesPossible)
        {
            InsuranceManager.InsuranceBetBecomesPossible += (object sender, EventArgs e) => alterGuiAfterInsuranceBetBecomesPossible(sender, e); 
        }
        public static void SubscribeToInsuranceBetIsDeclinedEvents(InsuranceManager.AlterGuiAfterInsuranceBetIsDeclinedDelegate alterGuiAfterInsuranceBetIsDeclinedDelegate)
        {
            GameController.InsuranceBetIsDeclined += (object sender, EventArgs e) => alterGuiAfterInsuranceBetIsDeclinedDelegate(sender, e); 
        }
        public static void SubscribeToPlayerDrawingHandSetEvents(BlackjackEntitiesController.AlterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet alterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet)
        {
            game.EntitiesController.PlayerDrawingHandSet += (object sender, PlayerDrawingHandSetEventArgs e) => alterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet(sender, e);
            game.EntitiesController.OnPlayerDrawingHandSet(new PlayerDrawingHandSetEventArgs(game.EntitiesController.Player.MainHand as PlayerHand));
        }
        public static void SubscribeToPlayerPlacesBetEvents(Player.AlterGuiAfterPlayerPlacesBetDelegate alterGuiAfterPlayerPlacesBetDelegate)
        {
            game.EntitiesController.Player.PlayerPlacesBet += (object sender, EventArgs e) => alterGuiAfterPlayerPlacesBetDelegate(sender, e);
        }
        //SOME OF THESE BELONG IN ENTITIES CONTROLLER. 
        public static void SubscribePlayerSplitHandToHandEventsAfterItsCreation(PlayerHand playerSplitHand)
        {
            playerSplitHand.DrawingRoundFinalized += (object sender, DrawingRoundFinalizedEventArgs e) => game.FinalizeDrawingRound(sender, e);
            playerSplitHand.CardAdded += (object sender, CardAddedEventArgs e) => AlterGuiForPlayerSplitHandAfterCardAdditionEventDelegate(sender, e);
        }
        public static void SubscribeToShowdownOccurredEvents(ShowdownOccurredEventArgs.DisableGuiInputButtonsAfterShowdownEventDelegate showdownOccurredEventDelegate)
        {
            ShowdownManager.ShowdownOccurred += (object sender, ShowdownOccurredEventArgs e) => showdownOccurredEventDelegate(sender, e);
        }
    }
}
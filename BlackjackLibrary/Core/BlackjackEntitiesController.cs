//Quinn Keenan, 301504914, 19/08/2025

using System.Diagnostics;

namespace BlackjackLibrary
{
    public class BlackjackEntitiesController
    {
        public delegate void AlterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet(object sender, PlayerDrawingHandSetEventArgs e);
        public delegate void AlterGuiAfterEntityBankruptsDelegate(object sender, BlackjackEntityBankruptsEventArgs e);
        public EventHandler<BlackjackEntityBankruptsEventArgs>? BlackjackEntityBankrupts;
        internal readonly Dealer Dealer;
        internal readonly Player Player;
        public EventHandler<PlayerDrawingHandSetEventArgs> PlayerDrawingHandSet;

        internal BlackjackEntitiesController()
        {
            Dealer = new Dealer();
            Player = new Player();
        }

        internal void Deal()
        {
            try
            {
                Dealer.Deal(Player.MainHand as PlayerHand);
            }
            catch (EmptyDeckException edException)
            {
                MessageManager.ChangeLatestMessage(edException.Message);
            }
        }

        internal void ExecuteDealerAction()
        {
            try
            {
                Dealer.Hit(Dealer.MainHand);
            }
            catch (EmptyDeckException edException)
            {
                MessageManager.ChangeLatestMessage(edException.Message);
                return; 
            }
        }

        public void ExecutePlayerAction(PlayerInputAbbreviation playerInputAbbreviation, PlayerHand playerHand)
        {
            try
            {
                switch (playerInputAbbreviation)
                {
                    case PlayerInputAbbreviation.D:
                        playerHand.DoubleDownOnBet(Dealer);
                        playerHand.Stand();
                        break;
                    case PlayerInputAbbreviation.H:
                        Dealer.Hit(playerHand);
                        break;
                    case PlayerInputAbbreviation.SP:
                        Player.InitializeSplitHand(Dealer); 
                        Player.SplitMainHand(Dealer, OnPlayerDrawingHandSet);
                        break;
                    case PlayerInputAbbreviation.ST:
                        playerHand.Stand();
                        break;
                    default: 
                        playerHand.Surrender();
                        break;
                }
            }
            catch (InvalidOperationException ioException)
            {
                MessageManager.ChangeLatestMessage(ioException.Message);
            }
            catch (EmptyDeckException edException)
            {
                MessageManager.ChangeLatestMessage(edException.Message);
            }
            catch (InsufficientChipsException icException)
            {
                MessageManager.ChangeLatestMessage(icException.Message);
            }
        }

        internal void OnPlayerDrawingHandSet(PlayerDrawingHandSetEventArgs e)
        {
            PlayerDrawingHandSet?.Invoke(null, e);
            //Debug.WriteLine($"\n{Environment.StackTrace}\n");
        }

        public void PlaceInsuranceBet()
        {
            PlayerHand playerMainHand = Player.MainHand as PlayerHand; 
            
            try
            {
                if (!InsuranceManager.InsuranceBetPossible(Dealer, Player))
                {
                    throw new InvalidOperationException(MessageManager.BET_NOT_POSSIBLE_MESSAGE);
                }

                InsuranceManager.PlaceInsuranceBet(Dealer, Player);
            }
            catch (InvalidOperationException ioException)
            {
                MessageManager.ChangeLatestMessage(ioException.Message);
            }
            catch (InsufficientChipsException icException)
            {
                MessageManager.ChangeLatestMessage(icException.Message);
            }

            MessageManager.InsuranceBetBrief(playerMainHand.InsuranceBet.ChipAmount);
        }

        public bool TryToPlaceMainBet(decimal chipAmount)
        {
            PlayerHand mainHand = Player.MainHand as PlayerHand;

            try
            {
                mainHand.PlaceMainBet(chipAmount, Dealer);
            }
            catch (InsufficientChipsException icException)
            {
                MessageManager.ChangeLatestMessage(icException.Message);
                return false; 
            }

            MessageManager.MainBetBrief(Player.MainHand as PlayerHand);
            return true; 
        }

        internal void OnBlackjackEntityBankrupts(BlackjackEntityBankruptsEventArgs e)
        {
            BlackjackEntityBankrupts.Invoke(e.Loser, e);
        }
    }
}
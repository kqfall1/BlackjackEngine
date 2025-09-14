//Quinn Keenan, 301504914, 04/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class ShowdownOccurredEventArgs : EventArgs
    {
        public readonly Dealer Dealer;
        public readonly DealerHand DealerHand; 
        public readonly Card DealerDownCard;
        public delegate void DisableGuiInputButtonsAfterShowdownEventDelegate(object sender, ShowdownOccurredEventArgs e);
        public readonly PlayerHand PlayerMainHand; 
        public readonly BlackjackEntity PlayerMainHandWinner;
        public readonly PlayerHand PlayerSplitHand; 
        public readonly BlackjackEntity PlayerSplitHandWinner;

        public PlayerHand[] HandsInShowdownOrder
        {
            get
            {
                return PlayerMainHand.Player.HandsInShowdownOrder; 
            }
        }

        public ShowdownOccurredEventArgs(DealerHand dealerMainHand, PlayerHand playerMainHand, PlayerHand playerSplitHand, BlackjackEntity mainHandWinner, BlackjackEntity splitHandWinner)
        {
            DealerHand = dealerMainHand; 
            DealerDownCard = DealerHand.DownCard;
            PlayerMainHand = playerMainHand;
            PlayerMainHandWinner = mainHandWinner;
            PlayerSplitHand = playerSplitHand; 
            PlayerSplitHandWinner = splitHandWinner;
        }
    }
}
//Quinn Keenan, 301504914, 03/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class CardAddedEventArgs : EventArgs
    {
        public const string BACK_OF_CARD_PHOTO_NAME = "back_of_card.png";
        public readonly Hand Hand; 
        public delegate void UpdateGuiUponCardAdditionEventDelegate(object sender, CardAddedEventArgs e);
        public readonly string CardPhotoName;

        internal CardAddedEventArgs(Hand hand)
        {
            Hand = hand; 
            Card card = Hand.MostRecentlyDealtCard; 

            if (hand is DealerHand dealerHand && dealerHand.AllCards.Count() == 2)
            {
                CardPhotoName = BACK_OF_CARD_PHOTO_NAME; 
            }
            else
            {
                CardPhotoName = card.CardPhotoName;
            }
        }
    }
}
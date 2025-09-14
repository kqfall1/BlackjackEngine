//Quinn Keenan, 301504914, 04/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class DrawingRoundFinalizedEventArgs : EventArgs
    {
        public readonly Hand Hand;

        internal DrawingRoundFinalizedEventArgs(Hand hand)
        {
            Hand = hand;
        }
    }
}
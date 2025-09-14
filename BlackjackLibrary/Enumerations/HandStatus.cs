//Quinn Keenan, 301504914, 21/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public enum HandStatus : byte
    {
        Blackjack, 
        Busted,
        Drawing, 
        Standing, 
        Surrendered,
        WaitingToDraw,
        WaitingOnSplitHand
    }
}
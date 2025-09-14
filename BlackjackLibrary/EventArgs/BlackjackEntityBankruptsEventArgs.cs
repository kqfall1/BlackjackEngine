//Quinn Keenan, 301504914, 05/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class BlackjackEntityBankruptsEventArgs : EventArgs
    {
        public readonly BlackjackEntity Winner;
        public readonly BlackjackEntity Loser; 

        public BlackjackEntityBankruptsEventArgs(BlackjackEntity winner, BlackjackEntity loser)
        {
            Winner = winner; 
            Loser = loser;
        }
    }
}
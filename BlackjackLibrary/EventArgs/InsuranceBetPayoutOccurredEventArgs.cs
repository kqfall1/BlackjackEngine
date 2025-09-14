//Quinn Keenan, 301504914, 04/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class InsuranceBetPayoutOccurredEventArgs : EventArgs
    {
        public Player Player; 
        public bool Won; 
        
        public InsuranceBetPayoutOccurredEventArgs(Player player, bool wonValue)
        {
            Player = player; 
            Won = wonValue;
        }
    }
}
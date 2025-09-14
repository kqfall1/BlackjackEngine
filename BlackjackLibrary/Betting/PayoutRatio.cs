//Quinn Keenan, 301504914, 19/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class PayoutRatio
    {
        internal static readonly PayoutRatio INSURANCE_BET = new PayoutRatio(2, 1);
        internal static readonly PayoutRatio MAIN_BET = new PayoutRatio(2, 1);
        internal static readonly PayoutRatio BLACKJACK = new PayoutRatio(3, 2);
        internal static readonly PayoutRatio PUSH = new PayoutRatio(1, 1);
        internal static readonly PayoutRatio SURRENDER = new PayoutRatio(1, 2); 

        internal decimal PayoutMultiplier
        {
            get
            {
                return (decimal)Numerator / (decimal)Denominator; 
            }
        }
        
        internal readonly byte Numerator; 
        internal readonly byte Denominator;

        internal PayoutRatio(byte numerator, byte denominator)
        {
            this.Numerator = numerator;
            this.Denominator = denominator;
        }

        public override string ToString()
        {
            return $"{Numerator}:{Denominator} - pays {(decimal) Numerator / (decimal) Denominator} times the bet amount.";
        }
    }
}
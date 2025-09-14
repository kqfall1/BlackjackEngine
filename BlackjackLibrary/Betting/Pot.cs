//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class Pot
    {  
        private decimal chipAmount; 
        internal decimal ChipAmount
        {
            get 
            { 
                return chipAmount; 
            }
            set
            {
                chipAmount = value;
            }
        }

        internal decimal Scoop()
        {
            decimal chipAmount = ChipAmount;
            ChipAmount = 0; 
            return chipAmount;
        }

        public override string ToString()
        {
            return $"Chips in pot: {ChipAmount:C}.";
        }
    }
}
//Quinn Keenan, 301504914, 19/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public abstract class BlackjackEntity
    {
        public const decimal INITIAL_CHIP_AMOUNT = 5000;

        private decimal chipAmount;
        public decimal ChipAmount
        {
            get
            {
                return chipAmount;
            }
            private set
            {
                chipAmount = value;
                OnChipAmountChanged(new BlackjackEntityChipAmountChangedEventArgs(this));
            }
        }
        public EventHandler<BlackjackEntityChipAmountChangedEventArgs> ChipAmountChanged; 

        private Hand mainHand;
        internal Hand MainHand
        {
            get
            {
                return mainHand;
            }
            set
            {
                mainHand = value;
            }
        }

        protected BlackjackEntity()
        {
            ChipAmount = INITIAL_CHIP_AMOUNT; 
        }

        internal void AddChips(decimal amount)
        {
            ChipAmount += amount; 
        }

        private void OnChipAmountChanged(BlackjackEntityChipAmountChangedEventArgs e)
        {
            ChipAmountChanged?.Invoke(this, e);
        }

        internal void RemoveChips(decimal chipAmount)
        {
            ChipAmount -= chipAmount; 
        }

        public virtual string ToString()
        {
            return $"{GetType().Name}. Chip amount: {ChipAmount}.\n\nMain hand:\n\n{MainHand}\n\n";
        }
    }
}
//Quinn Keenan, 301504914, 19/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class Bet
    {
        private decimal chipAmount;
        internal decimal ChipAmount
        {
            get
            {
                return chipAmount;
            }
            private set
            {
                chipAmount = value;
            }
        }

        internal decimal ChipAmountRequiredToDoubleDown
        {
            get
            {
                return ChipAmount; 
            }
        }

        internal decimal ChipAmountRequiredToPlaceInsuranceBet
        {
            get
            {
                return ChipAmount / 2;
            }
        }

        internal readonly Pot Pot;

        internal Bet(decimal chipAmount, Pot pot)
        {
            ChipAmount = chipAmount; 
            Pot = pot;
            pot.ChipAmount = chipAmount;
        }

        internal decimal DealerContributionAmount(decimal chipAmount, PayoutRatio payoutRatio)
        {
            decimal payout = payoutRatio.PayoutMultiplier * chipAmount;
            return payout - chipAmount; 
        }

        internal void DoubleDown()
        {
            ChipAmount *= 2;
            Pot.ChipAmount *= 2; 
        }

        internal decimal Payout(Dealer dealer, PayoutRatio payoutRatio) 
        {
            decimal potAmount = Pot.Scoop();
            decimal payout = payoutRatio.PayoutMultiplier * potAmount;
            decimal dealerContribution = payout - potAmount; 
            dealer.RemoveChips(dealerContribution);
            return payout; 
        }

        public decimal PayoutAmount(decimal chipAmount, PayoutRatio payoutRatio)
        {
            return payoutRatio.PayoutMultiplier * chipAmount; 
        }

        public override string ToString()
        {
            return $"{ChipAmount:C}"; 
        }
    }
}
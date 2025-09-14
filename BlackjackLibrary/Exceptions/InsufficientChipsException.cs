//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class InsufficientChipsException : Exception
    {
        public InsufficientChipsException(BlackjackEntity blackjackEntity, decimal chipAmount) : base(MessageManager.InsufficientChipsExceptionMessage(blackjackEntity, chipAmount)) {}
    }
}
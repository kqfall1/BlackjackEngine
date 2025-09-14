//Quinn Keenan, 301504914, 18/08/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class EmptyDeckException : Exception
    {
        public EmptyDeckException() : base("Cannot draw cards from an empty deck.") {} 
    }
}
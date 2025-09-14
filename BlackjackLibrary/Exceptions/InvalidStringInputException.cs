using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    internal class InvalidStringInputException : Exception
    {
        public InvalidStringInputException(string inputStr) : base($"Input \"{inputStr}\" is invalid.") {}
    }
}
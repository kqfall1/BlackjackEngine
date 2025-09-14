//Quinn Keenan, 301504914, 05/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public static class StringInputValidationManager
    {
        private static string NormalizeInput(string inputString)
        {
            if (String.IsNullOrWhiteSpace(inputString))
            {
                return String.Empty;
            }

            return inputString.Trim().ToUpper();
        }

        public static bool TryParseBetInput(string inputString, out decimal chipAmount)
        {
            string validatedInput = NormalizeInput(inputString);

            try
            {
                if (!Decimal.TryParse(validatedInput, out chipAmount) || chipAmount <= 0)
                {
                    throw new InvalidStringInputException(validatedInput);
                }
            }
            catch (InvalidStringInputException iiException)
            {
                MessageManager.ChangeLatestMessage(iiException.Message);
                chipAmount = 0;
                return false; 
            }

            return true;
        }

        internal static bool TryParseYesOrNoInput(string inputString, out PlayerInputAbbreviation validatedInputAbbreviation)
        {
            string validatedInput = NormalizeInput(inputString);

            try
            {
                if (!Enum.TryParse(validatedInput, out validatedInputAbbreviation) || !Enum.IsDefined(typeof(PlayerInputAbbreviation), validatedInput))
                {
                    throw new InvalidStringInputException(validatedInput);
                }
            }
            catch (InvalidStringInputException iiException)
            {
                MessageManager.ChangeLatestMessage(iiException.Message);
                validatedInputAbbreviation = PlayerInputAbbreviation.N;
                return false; 
            }

            return true; 
        }
    }
}
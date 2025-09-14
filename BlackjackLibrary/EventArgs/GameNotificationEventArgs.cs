//Quinn Keenan, 301504914, 04/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class GameNotificationEventArgs
    {
        public readonly string Message; 

        public GameNotificationEventArgs(string message)
        {
            Message = message; 
        }
    }
}
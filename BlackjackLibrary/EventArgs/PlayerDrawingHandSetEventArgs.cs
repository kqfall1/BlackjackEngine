//Quinn Keenan, 301504914, 08/09/2025

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class PlayerDrawingHandSetEventArgs : EventArgs
    {
        public readonly PlayerHand DrawingHand; 

        public PlayerDrawingHandSetEventArgs(PlayerHand drawingHand)
        {
            DrawingHand = drawingHand;
        }
    }
}
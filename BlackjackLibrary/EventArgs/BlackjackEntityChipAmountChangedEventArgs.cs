using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BlackjackLibrary
{
    public class BlackjackEntityChipAmountChangedEventArgs : EventArgs
    {
        public delegate void ChangeEntityChipAmountGuiControlDelegate(object sender, BlackjackEntityChipAmountChangedEventArgs e); 
        public readonly BlackjackEntity Entity;
        
        public BlackjackEntityChipAmountChangedEventArgs(BlackjackEntity entity)
        {
            Entity = entity;
        }
    }
}
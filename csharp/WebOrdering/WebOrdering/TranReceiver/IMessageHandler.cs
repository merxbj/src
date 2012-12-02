using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WebOrdering.TranReceiver
{
    interface IMessageHandler
    {
        void HandleMessage(string message);
    }
}

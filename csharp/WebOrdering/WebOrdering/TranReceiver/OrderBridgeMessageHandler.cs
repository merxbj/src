using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using WebOrdering.Common;

namespace WebOrdering.TranReceiver
{
    class OrderBridgeMessageHandler : IMessageHandler
    {
        public OrderBridgeMessageHandler()
        {
            transactions = new Dictionary<long, Transaction>();
        }

        public void HandleMessage(string message)
        {
            try
            {
                XmlSerializer ser = new XmlSerializer(typeof(Transaction));
                Transaction tran = (Transaction)ser.Deserialize(new XmlTextReader(new StringReader(message)));
                if (tran != null)
                {
                    //transactions.Add(tran.ID, tran);
                    Console.WriteLine("Got Transaction! ID={0}", tran.ID);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception caught: {0}", ex);
            }
        }

        private Dictionary<long, Transaction> transactions;
    }
}

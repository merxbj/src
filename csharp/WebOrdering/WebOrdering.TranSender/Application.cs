using System;
using System.Collections;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;
using IBM.WMQ;
using WebOrdering.Common;

namespace WebOrdering.TranSender
{
    class Application
    {
        static void Main(string[] args)
        {
            while (true)
            {
                Transaction tran = new Transaction();
                tran.ID = new Random(Guid.NewGuid().GetHashCode()).Next(int.MaxValue);
                tran.Items = new Item[1];
                tran.Items[0] = new Item();
                tran.Items[0].Name = "Kewl";
                tran.Items[0].Price = 10;
                tran.Items[0].Quantity = 1;

                XmlSerializer ser = new XmlSerializer(typeof(Transaction));
                StringBuilder builder = new StringBuilder();
                ser.Serialize(new StringWriter(builder), tran);

                new Application().SendMessage(builder.ToString());

                Console.ReadLine();
            }
        }

        void SendMessage(string msg)
        {
            try
            {
                // mq properties
                Hashtable properties = new Hashtable();
                properties.Add(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_MANAGED);
                properties.Add(MQC.HOST_NAME_PROPERTY, HostName);
                properties.Add(MQC.PORT_PROPERTY, Port);
                properties.Add(MQC.CHANNEL_PROPERTY, ChannelName);

                // create connection
                Console.Write("Connecting to queue manager.. ");
                MQQueueManager queueManager = new MQQueueManager(QueueManagerName, properties);
                Console.WriteLine("done");

                // accessing queue
                Console.Write("Accessing queue " + QueueName + ".. ");
                MQQueue queue = queueManager.AccessQueue(QueueName, MQC.MQOO_OUTPUT);
                Console.WriteLine("done");

                // creating a message object
                MQMessage message = new MQMessage();
                message.WriteString(msg);

                // send the message
                queue.Put(message);

                // closing queue
                Console.Write("Closing queue.. ");
                queue.Close();
                Console.WriteLine("done");

                // disconnecting queue manager
                Console.Write("Disconnecting queue manager.. ");
                queueManager.Disconnect();
                Console.WriteLine("done");
            }
            catch (MQException mqe)
            {
                Console.WriteLine("");
                Console.WriteLine("MQException caught: {0} - {1}", mqe.ReasonCode, mqe.Message);
                Console.WriteLine(mqe.StackTrace);
            }
        }

        /// <summary>
        /// Name of the host on which Queue manager is running 
        /// </summary>
        private const String HostName = "localhost";
        /// <summary>
        /// Port number on which Queue manager is listening
        /// </summary>
        private const int Port = 1414;
        /// <summary>
        /// Name of the channel
        /// </summary>
        private const String ChannelName = "SYSTEM.DEF.SVRCONN";
        /// <summary>
        /// Name of the Queue manager to connect to
        /// </summary>
        private const String QueueManagerName = "QM_MERXBJ";
        /// <summary>
        /// Queue name.
        /// </summary>
        private const String QueueName = "WEB_ORDERING_OUTBOUND";
        /// <summary>
        /// How long should we wait for a message
        /// </summary>
        private const int MessageWaitTimeout = 1000;
    }
}

using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using IBM.WMQ;
using System.Threading;
using System.Runtime.InteropServices;

namespace WebOrdering.TranReceiver
{
    class Listener
    {
        public Listener(IMessageHandler handler)
        {
            lockRoot = new object();
            ShuttingDown = false;
            this.handler = handler;
        }

        public void Listen()
        {
            ShuttingDown = false;
            background = new Thread(GetMessages);
            background.Start();
        }

        private void GetMessages()
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
                MQQueue queue = queueManager.AccessQueue(QueueName, MQC.MQOO_INPUT_AS_Q_DEF + MQC.MQOO_FAIL_IF_QUIESCING);
                Console.WriteLine("done");

                // creating a message options object
                MQGetMessageOptions mqGetMsgOpts = new MQGetMessageOptions();
                mqGetMsgOpts.WaitInterval = MessageWaitTimeout;
                mqGetMsgOpts.Options = MQC.MQGMO_FAIL_IF_QUIESCING | MQC.MQGMO_WAIT;

                // getting messages continuously
                bool done = false;
                while (!done && !ShuttingDown)
                {
                    try
                    {
                        // creating a message object
                        MQMessage message = new MQMessage();
                        queue.Get(message, mqGetMsgOpts);
                        string messageString = message.ReadString(message.MessageLength);
                        handler.HandleMessage(messageString);
                        message.ClearMessage();
                    }
                    catch (MQException mqe)
                    {
                        if (mqe.ReasonCode != 2033)
                        {
                            Console.WriteLine("MQException caught: {0} - {1}", mqe.ReasonCode, mqe.Message);
                            done = true;
                        }
                    }
                }

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

        public void Shutdown()
        {
            ShuttingDown = true;
            if (Monitor.TryEnter(lockRoot))
            {
                Monitor.Pulse(lockRoot);
                Monitor.Exit(lockRoot);
            }

            if (!background.Join(MessageWaitTimeout * 2))
            {
                throw new Exception("Shutdown request timed out");
            }
        }

        private readonly object lockRoot;
        private bool ShuttingDown { get; set; }
        private Thread background;
        private IMessageHandler handler;

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

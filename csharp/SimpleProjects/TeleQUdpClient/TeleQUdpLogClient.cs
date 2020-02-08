using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;

namespace TeleQUdpClient
{
    class TeleQUdpLogClient
    {
        public TeleQUdpLogClient()
        {
            lockRoot = new Object();
        }

        public void Listen()
        {
            using (UdpClient udpClient = new UdpClient(1134, AddressFamily.InterNetwork))
            {
                Console.WriteLine("Listening for TeleQ log events ...");
                while (true)
                {
                    try
                    {
                        IPEndPoint ep = new IPEndPoint(IPAddress.Any, 1134);
                        byte[] data = udpClient.Receive(ref ep);
                        RQLOG_EVENT rqle = ByteArrayToStructure<RQLOG_EVENT>(data);
                        ThreadPool.QueueUserWorkItem(HandleLogEvent, rqle);
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.ToString());
                        break;
                    }
                }
            }
        }

        public void HandleLogEvent(Object data)
        {
            lock (lockRoot)
            {
                RQLOG_EVENT rqle = (RQLOG_EVENT)data;
                using (TextWriter tw = new StreamWriter(@"c:\support\TeleQ.log", true))
                {
                    tw.WriteLine(rqle.ToString());
                }
            }
        }

        private static T ByteArrayToStructure<T>(byte[] bytes) where T : struct
        {
            GCHandle handle = GCHandle.Alloc(bytes, GCHandleType.Pinned);
            T stuff = (T)Marshal.PtrToStructure(handle.AddrOfPinnedObject(), typeof(T));
            handle.Free();
            return stuff;
        }

        private Object lockRoot;
    }
}

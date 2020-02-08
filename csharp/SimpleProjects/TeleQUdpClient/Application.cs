using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;

namespace TeleQUdpClient
{
    class Application
    {
        static void Main(string[] args)
        {
            new TeleQUdpLogClient().Listen();
        }
    }
}

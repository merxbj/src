using System;
using System.Threading;

namespace WebOrdering.TranReceiver
{

    /// <summary>
    /// Summary description for SimpleGet.
    /// </summary>
    class Application
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main(String[] args)
        {
            Console.WriteLine("Start of SimpleGet Application\n");
            try
            {
                Listener listener = new Listener(new OrderBridgeMessageHandler());
                listener.Listen();
                Console.WriteLine("Press Enter to shutdown\n");
                Console.ReadLine();
                listener.Shutdown();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception caught: {0}", ex);
                Console.WriteLine("Sample execution FAILED!");
            }
            Console.WriteLine("\nEnd of SimpleGet Application\n");
        }
    }
}

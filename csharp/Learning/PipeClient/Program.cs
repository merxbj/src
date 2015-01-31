using System.IO;
using System.IO.Pipes;
using System.Threading;

namespace PipeClient
{
    class Program
    {
        static void Main(string[] args)
        {
            using (AnonymousPipeClientStream pipeClient = new AnonymousPipeClientStream(PipeDirection.Out, args[0]))
            {
                using (StreamWriter pipeWriter = new StreamWriter(pipeClient))
                {
                    pipeWriter.WriteLine("HELLO");
                    pipeWriter.Flush();
                    Thread.Sleep(750);
                    pipeWriter.WriteLine("FROM");
                    pipeWriter.Flush();
                    Thread.Sleep(750);
                    pipeWriter.WriteLine("PIPE");
                    pipeWriter.Flush();
                    Thread.Sleep(750);
                    pipeWriter.WriteLine("QUIT");
                    pipeWriter.Flush();
                    pipeClient.WaitForPipeDrain();
                }
            }
        }
    }
}

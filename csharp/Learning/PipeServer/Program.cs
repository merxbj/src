using System;
using System.Diagnostics;
using System.IO;
using System.IO.Pipes;

namespace PipeServer
{
    class Program
    {
        static void Main()
        {
            using (AnonymousPipeServerStream pipeServer = new AnonymousPipeServerStream(PipeDirection.In, HandleInheritability.Inheritable))
            {
                Process child = new Process();
                child.StartInfo.FileName = @"d:\mysrc\csharp\Solution1\PipeClient\bin\Debug\PipeClient.exe";
                child.StartInfo.Arguments = pipeServer.GetClientHandleAsString();
                child.StartInfo.UseShellExecute = false;

                child.Start();
                pipeServer.DisposeLocalCopyOfClientHandle();

                using (StreamReader pipeReader = new StreamReader(pipeServer))
                {
                    string line = pipeReader.ReadLine();
                    while (line != "QUIT")
                    {
                        Console.Out.WriteLine(line);
                        line = pipeReader.ReadLine();
                    }
                }

                child.WaitForExit();
                child.Close();
            }
        }
    }
}

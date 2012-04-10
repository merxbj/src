using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace XmlTimeSpan
{
    class Program
    {
        static void Main(string[] args)
        {
            TimeSpan span = XmlConvert.ToTimeSpan("PT24H0M0S");
            Console.Out.WriteLine(span.TotalSeconds);
            Console.In.ReadLine();
        }
    }
}

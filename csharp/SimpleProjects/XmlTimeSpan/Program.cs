using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Xml;

namespace XmlTimeSpan
{
    class Program
    {
        static void Main(string[] args)
        {
            var culture = new CultureInfo(1024);
            CultureNotFoundException
            TimeSpan span = XmlConvert.ToTimeSpan("PT24H0M0S");
            Console.Out.WriteLine(span.TotalSeconds);
            Console.In.ReadLine();
            B b = new B();
            b.GetOperatorCulture(1024);
        }

        class A
        {
            internal virtual CultureInfo GetOperatorCulture(Int32 lcid)
            {
                return new CultureInfo(1024);
            }
        }

        class B : A
        {
            internal override CultureInfo GetOperatorCulture(Int32 lcid)
            {
                
            }
            
        }
        
    }
}

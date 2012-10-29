using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Xsl;

namespace GrandMaster
{
    class Program
    {
        static void Main(string[] args)
        {
            WebRequest wr = WebRequest.CreateHttp("http://eu.battle.net/sc2/en/ladder/grandmaster");
            using (Stream stream = wr.GetResponse().GetResponseStream())
            {
                using (FileStream fs = new FileStream((args[0]), FileMode.Create))
                {
                    stream.CopyTo(fs);
                }
            }

            XslCompiledTransform xslt = new XslCompiledTransform();
            xslt.Load(args[1]);
            xslt.Transform(args[0], args[2]);
        }
    }
}

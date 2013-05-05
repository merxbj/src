using Integri.Common;
using Integri.Common.Publishing;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing
{
    class FilePublisher : IPublisher<Occurrence>
    {
        public void Publish(List<Occurrence> occurrences)
        {
            using (TextWriter writer = new StreamWriter(@"d:\temp\integri\index.txt"))
            {
                foreach (Occurrence o in occurrences)
                {
                    if (o.PublicObject.Type == ObjectType.Event)
                    {
                        EventOccurence e = o as EventOccurence;
                        writer.WriteLine(@"""{0}"",""{1}"",""{2}"",""{3}"",""{4}""", o.PublicObject.MciFile, o.PublicObject.Type, o.PublicObject.Name, e.ProgramName, e.IsRaised ? "Raised" : "Handled");
                    }
                }
            }
        }
    }
}

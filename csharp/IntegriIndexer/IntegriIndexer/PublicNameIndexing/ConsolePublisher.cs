using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer.PublicNameIndexing
{
    public class ConsolePublisher : IPublisher
    {
        public void Publish(List<Occurrence> occurrences)
        {
            foreach (Occurrence o in occurrences)
            {
                if (o.PublicObject.Type == ObjectType.Event)
                {
                    Console.WriteLine("Found {0} at {1}", o.PublicObject, o);
                }
            }
        }
    }
}

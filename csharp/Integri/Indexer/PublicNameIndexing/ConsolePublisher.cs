using Integri.Common.Publishing;
using System;
using System.Collections.Generic;

namespace Integri.Indexer.PublicNameIndexing
{
    public class ConsolePublisher : IPublisher<Occurrence>
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

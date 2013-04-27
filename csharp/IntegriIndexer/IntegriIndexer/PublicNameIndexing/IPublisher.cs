using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer.PublicNameIndexing
{
    public interface IPublisher
    {
        void Publish(List<Occurrence> occurrences);
    }
}

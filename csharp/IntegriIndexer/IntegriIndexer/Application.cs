using Integri.Indexer.PublicNameIndexing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer
{
    class Application
    {
        static void Main(string[] args)
        {
            List<IIndexer> indexers = Utils.Discover<IIndexer>();
            indexers.ForEach(idx => idx.Index());
        }
    }
}

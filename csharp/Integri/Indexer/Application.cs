using Integri.Common;
using System.Collections.Generic;

namespace Integri.Indexer
{
    class Application
    {
        static void Main()
        {
            List<IIndexer> indexers = Utils.Discover<IIndexer>();
            indexers.ForEach(idx => idx.Index());
        }
    }
}

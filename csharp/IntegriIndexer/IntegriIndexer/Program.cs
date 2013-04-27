using IntegriIndexer.PublicNameIndexing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer
{
    class Program
    {
        static void Main(string[] args)
        {
            List<IIndexer> indexers = Utils.Discover<IIndexer>();
            indexers.ForEach(idx => idx.Index());
            Console.ReadLine();
        }
    }
}

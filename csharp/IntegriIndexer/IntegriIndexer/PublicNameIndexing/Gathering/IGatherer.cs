using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public interface IGatherer
    {
        IList<PublicObject> GatherPublicObjects(Project project);
        ObjectType ForType { get; }
    }
}

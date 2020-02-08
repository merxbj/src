using System.Collections.Generic;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public interface IGatherer
    {
        IList<PublicObject> GatherPublicObjects(Project project);
        ObjectType ForType { get; }
    }
}

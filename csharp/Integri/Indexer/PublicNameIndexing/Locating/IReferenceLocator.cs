using System.Collections.Generic;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Locating
{
    interface IReferenceLocator
    {
        List<Occurrence> Locate(List<ReferencedPublicObject> references, List<PublicObject> localObjects, Project project);
    }
}

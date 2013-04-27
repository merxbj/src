using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer.PublicNameIndexing.Locating
{
    interface IReferenceLocator
    {
        List<Occurrence> Locate(List<ReferencedPublicObject> references, List<PublicObject> localObjects, Project project);
    }
}

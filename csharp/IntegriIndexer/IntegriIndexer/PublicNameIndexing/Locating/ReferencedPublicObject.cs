using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing.Locating
{
    class ReferencedPublicObject : PublicObject
    {
        public ReferencedPublicObject(PublicObject po, int componentId, int objectIsn)
            : base(po.Name, po.Type, po.MciFile, po.LocalId)
        {
            this.ComponentId = componentId;
            this.ObjectIsn = objectIsn;
        }

        public int ComponentId { get; set; }
        public int ObjectIsn { get; set; }
    }
}

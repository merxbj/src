using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing
{
    public class ModelOccurence : Occurrence
    {
        public ModelOccurence(PublicObject po, Project project, string fileName, string path)
            : base(po, project, fileName)
        {
            this.LocationPath = path;
        }
        
        public string LocationPath { get; set; }

        public override string ToString()
        {
            return string.Format("{0}/{1}", Project.Name, LocationPath);
        }
    }
}

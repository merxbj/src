using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing
{
    public abstract class Occurrence
    {
        public Occurrence(PublicObject po, Project project, string fileName)
        {
            this.PublicObject = po;
            this.FileName = fileName;
            this.Project = project;
        }

        public PublicObject PublicObject { get; set; }
        public Project Project { get; set; }
        public string FileName { get; set; }
        
        public override string ToString()
        {
            return string.Format("{0}/{1}", Project.Name, FileName);
        }
    }
}

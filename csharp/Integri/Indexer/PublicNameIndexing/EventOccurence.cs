using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing
{
    public class EventOccurence : Occurrence
    {
        public EventOccurence(PublicObject po, Project project, string fileName, string programName, bool isRaised)
            : base(po, project, fileName)
        {
            this.IsRaised = isRaised;
            this.ProgramName = programName;
        }

        public bool IsRaised { get; set; }
        public string ProgramName { get; set; }

        public override string ToString()
        {
            return string.Format("{0}{1}/{2}", IsRaised ? "Raied at " : "Handled at ", Project.Name, ProgramName);
        }
    }
}

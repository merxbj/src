using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing
{
    public abstract class Occurrence
    {
        protected Occurrence(PublicObject po, Project project, string fileName)
        {
            PublicObject = po;
            FileName = fileName;
            Project = project;
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

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace IntegriIndexer.PublicNameIndexing.Gathering
{
    public class ProgramGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            var publics = new List<PublicObject>();

            List<FileInfo> programFiles = DiscoverProgramFiles(project);
            foreach (FileInfo programFile in programFiles)
            {
                XmlDocument doc = new XmlDocument();
                doc.Load(programFile.FullName);
                XmlNode obj = doc.SelectSingleNode("/Application/ProgramsRepository/Programs/Task/Header[Public]");
                if (obj != null)
                {
                    string name = obj.SelectSingleNode("./Public/@val").Value;
                    int localId = Convert.ToInt32(obj.Attributes["id"].Value);
                    publics.Add(new PublicObject(name, ForType, project.MciFile, localId));
                }
            }

            return publics;
        }

        public ObjectType ForType
        {
            get { return ObjectType.Program; }
        }

        private List<FileInfo> DiscoverProgramFiles(Project project)
        {
            DirectoryInfo di = new DirectoryInfo(project.SrcPath);
            return new List<FileInfo>(di.EnumerateFiles("Prg_*.xml"));
        }
    }
}

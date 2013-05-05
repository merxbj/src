using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml;

namespace Integri.Common
{
    public class ProgramDiscovery
    {
        public ProgramDiscovery(Project project)
        {
            this.project = project;
        }

        public IEnumerable<Program> DiscoverPrograms()
        {
            List<FileInfo> programFiles = DiscoverProgramFiles();
            foreach (FileInfo programFile in programFiles)
            {
                XmlDocument doc = new XmlDocument();
                doc.Load(programFile.FullName);
                XmlNode nameNode = doc.DocumentElement.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description");
                string name = (nameNode != null) ? nameNode.Value : String.Empty;

                yield return new Program(project, name, programFile.Name, doc);
            }
        }

        private List<FileInfo> DiscoverProgramFiles()
        {
            DirectoryInfo di = new DirectoryInfo(project.SrcPath);
            return new List<FileInfo>(di.EnumerateFiles("Prg_*.xml"));
        }

        private Project project;
    }
}

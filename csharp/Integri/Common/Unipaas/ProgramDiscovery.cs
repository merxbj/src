using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Xml;
using Integri.Common.Logging;
using log4net;

namespace Integri.Common.Unipaas
{
    public class ProgramDiscovery
    {
        public ProgramDiscovery(Project project)
        {
            this.project = project;
        }

        public IEnumerable<Program> DiscoverPrograms()
        {
            IEnumerable<FileInfo> programFiles = DiscoverProgramFiles();
            foreach (FileInfo programFile in programFiles)
            {
                XmlDocument doc = new XmlDocument();
                doc.Load(programFile.FullName);
                if (doc.DocumentElement != null)
                {
                    XmlNode nameNode = doc.DocumentElement.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description");
                    string name = (nameNode != null) ? nameNode.Value : String.Empty;

                    yield return new Program(project, name, programFile.Name, doc);
                }
            }
        }

        private IEnumerable<FileInfo> DiscoverProgramFiles()
        {
            List<FileInfo> programFiles = new List<FileInfo>();

            XmlDocument progs = new XmlDocument();
            progs.Load(project.SrcPath + "Progs.xml");
            if (progs.DocumentElement != null)
            {
                XmlNodeList programs = progs.DocumentElement.SelectNodes("/Application/ProgramsRepositoryOutLine/Programs/Program[@id]");
                if (programs != null)
                {
                    foreach (XmlNode program in programs)
                    {
                        if (program.Attributes != null)
                        {
                            FileInfo programFile = new FileInfo(string.Format("{0}Prg_{1}.xml", project.SrcPath, program.Attributes["id"].Value));
                            if (programFile.Exists)
                            {
                                programFiles.Add(programFile);
                            }
                            else
                            {
                                log.WarnFormat("\tDiscovered program file {0} that does not exist.", programFile.FullName);
                            }
                        }
                    }
                }
            }

            ReportOrphanedFiles(programFiles);

            return programFiles;
        }

        private void ReportOrphanedFiles(List<FileInfo> programFiles)
        {
            FileInfoEqualityComparer comparer = new FileInfoEqualityComparer();
            DirectoryInfo di = new DirectoryInfo(project.SrcPath);
            foreach (FileInfo fi in di.EnumerateFiles("Prg_*.xml"))
            {
                if (!programFiles.Contains(fi, comparer))
                {
                    log.WarnFormat("\tDiscovered orphaned program file {0}.", fi.Name);
                }
            }
        }

        public class FileInfoEqualityComparer : IEqualityComparer<FileInfo>
        {
            public bool Equals(FileInfo x, FileInfo y)
            {
                return x.FullName.Equals(y.FullName);
            }

            public int GetHashCode(FileInfo obj)
            {
                return obj.FullName.GetHashCode();
            }
        }

        private readonly Project project;
        private readonly ILog log = LoggingFactory.GetLogger();
    }
}

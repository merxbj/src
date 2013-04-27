using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace IntegriIndexer.PublicNameIndexing.Locating
{
    class ProgramFilesOccurenceLocator : IReferenceLocator
    {
        #region IReferenceLocator Implementation

        public List<Occurrence> Locate(List<ReferencedPublicObject> references, List<PublicObject> localObjects, Project project)
        {
            Console.WriteLine("ProgramFilesOccurenceLocator locating {0} references in {1}", references.Count, project.Name);

            List<Occurrence> occurrences = new List<Occurrence>();
            List<FileInfo> programFiles = DiscoverProgramFiles(project);
            foreach (FileInfo programFile in programFiles)
            {
                Console.WriteLine("\t{0}", programFile.Name);

                XmlDocument program = new XmlDocument();
                program.Load(programFile.FullName);

                occurrences.AddRange(LocateReferences(references, project, program, programFile));
                occurrences.AddRange(LocateLocalObjects(localObjects, project, program, programFile));
            }

            return occurrences;
        }

        #endregion

        #region Reference Locating

        private IEnumerable<Occurrence> LocateReferences(List<ReferencedPublicObject> references, Project project, XmlDocument program, FileInfo programFile)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            foreach (ReferencedPublicObject reference in references)
            {
                occurrences.AddRange(LocateReferences(reference, project, programFile, program));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateReferences(ReferencedPublicObject reference, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            switch (reference.Type)
            {
                case ObjectType.DataSource:
                    occurrences.AddRange(LocateDataSourceReferences(reference, project, programFile, program));
                    break;
                case ObjectType.Event:
                    occurrences.AddRange(LocateEventReferences(reference, project, programFile, program));
                    break;
                case ObjectType.Model:
                    occurrences.AddRange(LocateModelReferences(reference, project, programFile, program));
                    break;
                case ObjectType.Program:
                    occurrences.AddRange(LocateProgramReferences(reference, project, programFile, program));
                    break;
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateProgramReferences(ReferencedPublicObject reference, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateModelReferences(ReferencedPublicObject reference, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            /*XmlNodeList hits = program.SelectNodes(
                    string.Format("//PropertyList/Model[@comp={0} and @obj={1}]",
                        reference.ComponentId, reference.ObjectIsn));

            foreach (XmlNode hit in hits)
            {
                occurrences.Add(new ModelOccurence(
                        new PublicObject(reference.Name, reference.Type, reference.MciFile),
                        project,
                        programFile.Name,
                        BuildModelUsagePath(program, hit)));
            }*/
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateEventReferences(ReferencedPublicObject reference, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            XmlNodeList raised = program.SelectNodes(
                    string.Format("//RaiseEvent/Event/PublicObject[@comp={0} and @obj={1}]",
                        reference.ComponentId, reference.ObjectIsn));
            foreach (XmlNode raise in raised)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(reference.Name, reference.Type, reference.MciFile, reference.LocalId),
                        project,
                        programFile.Name,
                        program.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        true));
            }

            XmlNodeList handled = program.SelectNodes(
                    string.Format("//LogicUnit/Event/PublicObject[@comp={0} and @obj={1}]",
                        reference.ComponentId, reference.ObjectIsn));
            foreach (XmlNode handle in handled)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(reference.Name, reference.Type, reference.MciFile, reference.LocalId),
                        project,
                        programFile.Name,
                        program.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        false));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateDataSourceReferences(ReferencedPublicObject reference, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            return occurrences;
        }

        #endregion

        #region Local Object Locating

        private List<Occurrence> LocateLocalObjects(List<PublicObject> localObjects, Project project, XmlDocument program, FileInfo programFile)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            foreach (PublicObject po in localObjects)
            {
                occurrences.AddRange(LocateLocalObjects(po, project, program, programFile));
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalObjects(PublicObject localObject, Project project, XmlDocument program, FileInfo programFile)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            switch (localObject.Type)
            {
                case ObjectType.DataSource:
                    occurrences.AddRange(LocateLocalDataSource(localObject, project, programFile, program));
                    break;
                case ObjectType.Event:
                    occurrences.AddRange(LocateLocalEvent(localObject, project, programFile, program));
                    break;
                case ObjectType.Model:
                    occurrences.AddRange(LocateLocalModel(localObject, project, programFile, program));
                    break;
                case ObjectType.Program:
                    occurrences.AddRange(LocateLocalProgram(localObject, project, programFile, program));
                    break;
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalProgram(PublicObject localObject, Project project, FileInfo programFile, XmlDocument program)
        {
            return new List<Occurrence>();
        }

        private IEnumerable<Occurrence> LocateLocalModel(PublicObject localObject, Project project, FileInfo programFile, XmlDocument program)
        {
            return new List<Occurrence>();
        }

        private List<Occurrence> LocateLocalEvent(PublicObject localObject, Project project, FileInfo programFile, XmlDocument program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            XmlNodeList raised = program.SelectNodes(
                    string.Format("//RaiseEvent/Event/PublicObject[@comp=-1 and @obj={0}]",
                        localObject.LocalId));
            foreach (XmlNode raise in raised)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(localObject.Name, localObject.Type, localObject.MciFile, localObject.LocalId),
                        project,
                        programFile.Name,
                        program.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        true));
            }

            XmlNodeList handled = program.SelectNodes(
                    string.Format("//LogicUnit/Event/PublicObject[@comp=-1 and @obj={0}]",
                        localObject.LocalId));
            foreach (XmlNode handle in handled)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(localObject.Name, localObject.Type, localObject.MciFile, localObject.LocalId),
                        project,
                        programFile.Name,
                        program.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        false));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalDataSource(PublicObject localObject, Project project, FileInfo programFile, XmlDocument program)
        {
            return new List<Occurrence>();
        }

        #endregion

        private List<FileInfo> DiscoverProgramFiles(Project project)
        {
            DirectoryInfo di = new DirectoryInfo(project.SrcPath);
            return new List<FileInfo>(di.EnumerateFiles("Prg_*.xml"));
        }

        private string BuildModelUsagePath(XmlDocument program, XmlNode hit)
        {
            return string.Format("Programs/{0}",
                program.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value);
        }
    }
}

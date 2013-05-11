using Integri.Common;
using System.Collections.Generic;
using System.Xml;
using Integri.Common.Logging;
using Integri.Common.Unipaas;
using log4net;

namespace Integri.Indexer.PublicNameIndexing.Locating
{
    // ReSharper disable PossibleNullReferenceException
    class ProgramFilesOccurenceLocator : IReferenceLocator
    {
        #region IReferenceLocator Implementation

        public List<Occurrence> Locate(List<ReferencedPublicObject> references, List<PublicObject> localObjects, Project project)
        {
            log.InfoFormat("Locating {0} references and {1} local objects in {2}", references.Count, localObjects.Count, project.Name);

            List<Occurrence> occurrences = new List<Occurrence>();
            ProgramDiscovery pd = new ProgramDiscovery(project);
            foreach (Program program in pd.DiscoverPrograms())
            {
                log.InfoFormat("\t{0}", program.FileName);

                occurrences.AddRange(LocateReferences(references, program));
                occurrences.AddRange(LocateLocalObjects(localObjects, program));
            }

            return occurrences;
        }

        #endregion

        #region Reference Locating

        private IEnumerable<Occurrence> LocateReferences(IEnumerable<ReferencedPublicObject> references, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            foreach (ReferencedPublicObject reference in references)
            {
                occurrences.AddRange(LocateReferences(reference, program));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateReferences(ReferencedPublicObject reference, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            switch (reference.Type)
            {
                case ObjectType.DataSource:
                    occurrences.AddRange(LocateDataSourceReferences(reference, program));
                    break;
                case ObjectType.Event:
                    occurrences.AddRange(LocateEventReferences(reference, program));
                    break;
                case ObjectType.Model:
                    occurrences.AddRange(LocateModelReferences(reference, program));
                    break;
                case ObjectType.Program:
                    occurrences.AddRange(LocateProgramReferences(reference, program));
                    break;
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateProgramReferences(ReferencedPublicObject reference, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateModelReferences(ReferencedPublicObject reference, Program program)
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

        private IEnumerable<Occurrence> LocateEventReferences(ReferencedPublicObject reference, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            XmlNodeList raised = program.Source.SelectNodes(
                    string.Format("//RaiseEvent/Event/PublicObject[@comp={0} and @obj={1}]",
                        reference.ComponentId, reference.ObjectIsn));
            foreach (XmlNode raise in raised)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(reference.Name, reference.Type, reference.MciFile, reference.LocalId),
                        program.Project,
                        program.Name,
                        program.Source.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        true));
            }

            XmlNodeList handled = program.Source.SelectNodes(
                    string.Format("//LogicUnit/Event/PublicObject[@comp={0} and @obj={1}]",
                        reference.ComponentId, reference.ObjectIsn));
            foreach (XmlNode handle in handled)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(reference.Name, reference.Type, reference.MciFile, reference.LocalId),
                        program.Project,
                        program.Name,
                        program.Source.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        false));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateDataSourceReferences(ReferencedPublicObject reference, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            return occurrences;
        }

        #endregion

        #region Local Object Locating

        private IEnumerable<Occurrence> LocateLocalObjects(IEnumerable<PublicObject> localObjects, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            foreach (PublicObject po in localObjects)
            {
                occurrences.AddRange(LocateLocalObjects(po, program));
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalObjects(PublicObject localObject, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            switch (localObject.Type)
            {
                case ObjectType.DataSource:
                    occurrences.AddRange(LocateLocalDataSource(localObject, program));
                    break;
                case ObjectType.Event:
                    occurrences.AddRange(LocateLocalEvent(localObject, program));
                    break;
                case ObjectType.Model:
                    occurrences.AddRange(LocateLocalModel(localObject, program));
                    break;
                case ObjectType.Program:
                    occurrences.AddRange(LocateLocalProgram(localObject, program));
                    break;
            }
            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalProgram(PublicObject localObject, Program program)
        {
            return new List<Occurrence>();
        }

        private IEnumerable<Occurrence> LocateLocalModel(PublicObject localObject, Program program)
        {
            return new List<Occurrence>();
        }

        private IEnumerable<Occurrence> LocateLocalEvent(PublicObject localObject, Program program)
        {
            List<Occurrence> occurrences = new List<Occurrence>();
            XmlNodeList raised = program.Source.SelectNodes(
                    string.Format("//RaiseEvent/Event/PublicObject[@comp=-1 and @obj={0}]",
                        localObject.LocalId));
            foreach (XmlNode raise in raised)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(localObject.Name, localObject.Type, localObject.MciFile, localObject.LocalId),
                        program.Project,
                        program.Name,
                        program.Source.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        true));
            }

            XmlNodeList handled = program.Source.SelectNodes(
                    string.Format("//LogicUnit/Event/PublicObject[@comp=-1 and @obj={0}]",
                        localObject.LocalId));
            foreach (XmlNode handle in handled)
            {
                occurrences.Add(new EventOccurence(
                        new PublicObject(localObject.Name, localObject.Type, localObject.MciFile, localObject.LocalId),
                        program.Project,
                        program.Name,
                        program.Source.SelectSingleNode("/Application/ProgramsRepository/Programs/Task[1]/Header/@Description").Value,
                        false));
            }

            return occurrences;
        }

        private IEnumerable<Occurrence> LocateLocalDataSource(PublicObject localObject, Program program)
        {
            return new List<Occurrence>();
        }

        #endregion

        private static readonly ILog log = LoggingFactory.GetLogger();
    }
}

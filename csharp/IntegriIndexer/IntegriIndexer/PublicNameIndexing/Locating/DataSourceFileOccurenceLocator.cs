using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Integri.Indexer.PublicNameIndexing.Locating
{
    class DataSourceFileOccurenceLocator : IReferenceLocator
    {
        #region IReferenceLocator Implementation

        public List<Occurrence> Locate(List<ReferencedPublicObject> references, List<PublicObject> localObjects, Project project)
        {
            XmlDocument dataSources = new XmlDocument();
            dataSources.Load(project.SrcPath + "DataSources.xml");

            List<Occurrence> occurrences = new List<Occurrence>();
            occurrences.AddRange(LocateReferences(references, project, dataSources));
            occurrences.AddRange(LocateLocalObjects(localObjects, project, dataSources));

            return occurrences;
        }

        #endregion

        #region Reference Locating

        private IEnumerable<Occurrence> LocateReferences(List<ReferencedPublicObject> references, Project project, XmlDocument dataSources)
        {
 	        foreach (ReferencedPublicObject reference in references)
            {
                if (reference.Type == ObjectType.Model)
                {
                    XmlNodeList hits = dataSources.SelectNodes(
                        string.Format("/Application/DataSourceRepository/DataObjects/DataObject/Columns/Column[PropertyList/Model/@comp={0} and PropertyList/Model/@obj={1}]",
                            reference.ComponentId, reference.ObjectIsn));
                    
                    foreach (XmlNode hit in hits)
                    {
                        yield return new ModelOccurence(
                                new PublicObject(reference.Name, reference.Type, reference.MciFile, reference.LocalId), 
                                project,
                                "DataSources.xml",
                                BuildModelUsagePath(hit));
                    }
                }
            }
        }

        #endregion

        #region Local Object Locating

        public IEnumerable<Occurrence> LocateLocalObjects(List<PublicObject> localObjects, Project project, XmlDocument dataSources)
        {
            foreach (PublicObject reference in localObjects)
            {
                if (reference.Type == ObjectType.Model)
                {
                    XmlNodeList hits = dataSources.SelectNodes(
                        string.Format("/Application/DataSourceRepository/DataObjects/DataObject/Columns/Column[PropertyList/Model/@comp=-1 and PropertyList/Model/@obj={0}]",
                            reference.LocalId));

                    foreach (XmlNode hit in hits)
                    {
                        yield return new ModelOccurence(
                                reference,
                                project,
                                "DataSources.xml",
                                BuildModelUsagePath(hit));
                    }
                }
            }
        }

        #endregion

        private string BuildModelUsagePath(XmlNode hit)
        {
            return string.Format("Data/{0}/{1}/{2}",
                hit.ParentNode.ParentNode.Attributes["Folder"] != null ? hit.ParentNode.ParentNode.Attributes["Folder"].Value : "",
                hit.ParentNode.ParentNode.Attributes["name"] != null ? hit.ParentNode.ParentNode.Attributes["name"].Value : "",
                hit.Attributes["name"] != null ? hit.Attributes["name"].Value : "");
        }
    }
}

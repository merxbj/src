using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Locating
{
    public class ProjectExplorer
    {
        public ProjectExplorer(List<PublicObject> publics)
        {
            this.publics = publics;
            locators = Utils.Discover<IReferenceLocator>(typeof(ProjectExplorer).Assembly);
            componentCache = new Dictionary<string, XmlElement>();
        }

        public List<Occurrence> Explore(Project project)
        {
            List<Occurrence> occurrences = new List<Occurrence>();

            List<ReferencedPublicObject> referencedPublics = DiscoverReferencedPublicObjects(project);
            List<PublicObject> localPublics = DiscoverLocalPublicObjects(project);
            locators.ForEach(l => occurrences.AddRange(l.Locate(referencedPublics, localPublics, project)));

            return occurrences;
        }

        #region Public Object References

        private List<ReferencedPublicObject> DiscoverReferencedPublicObjects(Project project)
        {
            XmlDocument comps = new XmlDocument();
            comps.Load(project.SrcPath + "Comps.xml");

            List<ReferencedPublicObject> referenced = new List<ReferencedPublicObject>();
            foreach (PublicObject po in publics)
            {
                XmlElement parentComponent = FindComponent(po.MciFile, comps);
                if (parentComponent != null)
                {
                    XmlNode publicObjectReference = null;
                    switch (po.Type)
                    {
                        case ObjectType.DataSource:
                            publicObjectReference = DiscoverReferencedDataSouce(parentComponent, po);
                            break;
                        case ObjectType.Event:
                            publicObjectReference = DiscoverReferencedEvent(parentComponent, po);
                            break;
                        case ObjectType.Model:
                            publicObjectReference = DiscoverReferencedModel(parentComponent, po);
                            break;
                        case ObjectType.Program:
                            publicObjectReference = DiscoverReferencedProgram(parentComponent, po);
                            break;
                    }

                    if (publicObjectReference != null)
                    {
                        referenced.Add(new ReferencedPublicObject(po,
                                Int32.Parse(parentComponent.Attributes["id"].Value),
                                Int32.Parse(publicObjectReference.SelectSingleNode("ItemIsn").Attributes["val"].Value)));
                    }
                }
            }

            return referenced;
        }

        private XmlNode DiscoverReferencedProgram(XmlElement parentComponent, PublicObject po)
        {
            return parentComponent.SelectSingleNode(string.Format("ComponentPrograms/Object[PublicName/@val='{0}']", po.Name));
        }

        private XmlNode DiscoverReferencedModel(XmlElement parentComponent, PublicObject po)
        {
            return parentComponent.SelectSingleNode(string.Format("ComponentModels/Object[PublicName/@val='{0}']", po.Name));
        }

        private XmlNode DiscoverReferencedEvent(XmlElement parentComponent, PublicObject po)
        {
            return parentComponent.SelectSingleNode(string.Format("ComponentEvents/Object[PublicName/@val='{0}']", po.Name));
        }

        private XmlNode DiscoverReferencedDataSouce(XmlElement parentComponent, PublicObject po)
        {
            return parentComponent.SelectSingleNode(string.Format("ComponentDataObjects/Object[PublicName/@val='{0}']", po.Name));
        }

        private XmlElement FindComponent(string mciFile, XmlDocument comps)
        {
            XmlElement component;
            if (componentCache.ContainsKey(mciFile))
            {
                component = componentCache[mciFile];
            }
            else
            {
                component = (XmlElement)comps.SelectSingleNode(string.Format("/Application/ComponentsRepository/Components/Component[contains(MciFile/@val,'{0}')]", mciFile));
                componentCache[mciFile] = component;
            }
            return component;
        }

        #endregion

        #region Local Public Object References

        private List<PublicObject> DiscoverLocalPublicObjects(Project project)
        {
            List<PublicObject> localPublics = new List<PublicObject>(publics.Count());
            foreach (PublicObject po in publics)
            {
                if (po.MciFile == project.MciFile)
                {
                    localPublics.Add(po);
                }
            }
            return localPublics;
        }

        #endregion

        private readonly IDictionary<string, XmlElement> componentCache;
        private readonly List<PublicObject> publics;
        private readonly List<IReferenceLocator> locators;
    }
}

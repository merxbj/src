using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace ElectionVisualiser
{
    interface IElectionDataDownloader
    {
        XmlDocument DownloadParties();
        IEnumerable<XmlDocument> EnumerateParties(string path);
        XmlDocument DownloadResults();
        XmlDocument DownloadCandidates();
    }

    class Election2017DataDownloader : IElectionDataDownloader
    {
        public XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/pls/ps2017nss/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2017reg20171122/psrkl.xml", Encoding.GetEncoding(1250)))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2017reg20171122/psrk.xml", Encoding.GetEncoding(1250)))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public IEnumerable<XmlDocument> EnumerateParties(string path)
        {
            throw new System.NotImplementedException();
        }
    }

    class Election2021DataDownloader : IElectionDataDownloader
    {
        public virtual XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://www.volby.cz/pls/ps2021/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public virtual XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/opendata/ps2021/xml/psrkl.xml"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public virtual XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/opendata/ps2021/xml/psrk.xml"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public virtual IEnumerable<XmlDocument> EnumerateParties(string path)
        {
            throw new System.NotImplementedException();
        }
    }

    class Election2021DataSnapshotDownloader : Election2021DataDownloader
    {
        public override XmlDocument DownloadResults()
        {
            throw new System.NotImplementedException();
        }

        public override IEnumerable<XmlDocument> EnumerateParties(string path)
        {
            List<string> snapshots = new List<string>(Directory.EnumerateFiles(path, "Snapshot*.xml"));
            snapshots.Sort();

            foreach (var snapshotPath in snapshots)
            {
                using (StreamReader sr = new StreamReader(snapshotPath))
                {
                    using (XmlTextReader tr = new XmlTextReader(sr))
                    {
                        tr.Namespaces = false;
                        XmlDocument doc = new XmlDocument();
                        doc.Load(tr);

                        yield return doc;
                    }
                }
            }
        }
    }

    class Election2006DataDownloader : IElectionDataDownloader
    {
        public XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/pls/ps2006/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2006reg2006/psrkl.xml"))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2006reg2006/psrk.xml"))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public IEnumerable<XmlDocument> EnumerateParties(string path)
        {
            throw new System.NotImplementedException();
        }
    }
}
using System;

namespace Integri.Common.Unipaas
{
    public class Project : IComparable<Project>
    {
        public Project(string name, string srcPath, string mciFile)
        {
            Name = name;
            SrcPath = srcPath;
            MciFile = mciFile;
        }

        public int CompareTo(Project other)
        {
            if (ReferenceEquals(this, other))
            {
                return 0;
            }
            if (other == null)
            {
                return -1;
            }
            return String.Compare(Name, other.Name, StringComparison.Ordinal);
        }

        public override string ToString()
        {
            return Name;
        }

        public string Name { get; set; }
        public string SrcPath { get; set; }
        public string MciFile { get; set; }
    }
}

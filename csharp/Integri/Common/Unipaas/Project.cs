namespace Integri.Common.Unipaas
{
    public class Project
    {
        public Project(string name, string srcPath, string mciFile)
        {
            Name = name;
            SrcPath = srcPath;
            MciFile = mciFile;
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

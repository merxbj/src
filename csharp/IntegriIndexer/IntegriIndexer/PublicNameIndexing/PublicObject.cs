using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing
{
    public class PublicObject : IComparable<PublicObject>
    {
        public PublicObject(string name, ObjectType type, string mciFile, int localId)
        {
            this.Name = name;
            this.Type = type;
            this.MciFile = mciFile.ToLower();
            this.LocalId = localId;
        }

        public string Name { get; set; }
        public ObjectType Type { get; set; }
        public string MciFile { get; set; }
        public int LocalId { get; set; }

        public int CompareTo(PublicObject other)
        {
            if (other == null)
            {
                return 1;
            }

            int compare = Name.CompareTo(other.Name);
            if (compare == 0)
            {
                compare = Type.CompareTo(other.Type);
            }

            if (compare == 0)
            {
                compare = MciFile.CompareTo(other.MciFile);
            }

            return compare;
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (object.ReferenceEquals(this, obj))
            {
                return true;
            }
            return (CompareTo(obj as PublicObject) == 0);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = 17;
                hash = hash * 23 + Name.GetHashCode();
                hash = hash * 23 + Type.GetHashCode();
                hash = hash * 23 + MciFile.GetHashCode();
                return hash;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}/{2} ({3})", MciFile, Type, Name, LocalId);
        }
    }

    public enum ObjectType { Model, DataSource, Program, Event }
}

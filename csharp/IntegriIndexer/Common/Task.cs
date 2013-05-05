using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Integri.Common
{
    public class Task
    {
        public Task(Program owner, Task parent, string name)
        {
            this.owner = owner;
            this.parent = parent;
            this.name = name;
        }

        public Program Owner { get { return owner; } }
        public Task Parent { get { return parent; } }
        public string Name { get { return name; } }

        public override string ToString()
        {
            if (parent == null)
            {
                return owner.ToString();
            }
            return parent.ToString() + "/" + name;
        }

        private Program owner;
        private Task parent;
        private string name;
    }
}

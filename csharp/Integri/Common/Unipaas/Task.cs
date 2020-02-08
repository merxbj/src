namespace Integri.Common.Unipaas
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
            return parent + "/" + name;
        }

        private readonly Program owner;
        private readonly Task parent;
        private readonly string name;
    }
}

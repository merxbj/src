using System;

namespace Integri.Common.Unipaas
{
    public abstract class LogicLine
    {
        protected LogicLine(Task owner, string condition)
        {
            this.owner = owner;
            this.condition = condition;
        }

        public Task Owner { get { return owner; } }
        public string Condition { get { return condition; } }

        public override string ToString()
        {
            return owner + " -> IF (" + (String.IsNullOrEmpty(condition) ? "true" : condition) + ") THEN ";
        }

        private readonly Task owner;
        private readonly string condition;
    }
}

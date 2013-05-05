using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Integri.Common
{
    public abstract class LogicLine
    {
        public LogicLine(Task owner, string condition)
        {
            this.owner = owner;
            this.condition = condition;
        }

        public Task Owner { get { return owner; } }
        public string Condition { get { return condition; } }

        public override string ToString()
        {
            return owner.ToString() + " -> IF (" + (String.IsNullOrEmpty(condition) ? "true" : condition) + ") THEN ";
        }

        private Task owner;
        private string condition;
    }
}

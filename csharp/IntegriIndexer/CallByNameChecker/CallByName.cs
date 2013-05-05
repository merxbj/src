using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Integri.CallByNameChecker
{
    class CallByName : LogicLine
    {
        public CallByName(Task owner, string condition, string cabinet, string publicName) : base(owner, condition)
        {
            this.cabinet = cabinet;
            this.publicName = publicName;
        }

        public string Cabinet { get { return cabinet; } }
        public string PublicName { get { return publicName; } }

        public override string ToString()
        {
            return base.ToString() + "CallByName('"+cabinet + "', '" + publicName + "')";
        }

        private string cabinet;
        private string publicName;
    }
}

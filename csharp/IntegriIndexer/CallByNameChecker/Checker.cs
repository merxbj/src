using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Integri.CallByNameChecker
{
    class Checker
    {
        public Checker(List<CallByName> callByNames)
        {
            this.callByNames = callByNames;
        }

        public List<CallByName> Check()
        {
            List<CallByName> notPassed = new List<CallByName>(callByNames.Count);
            foreach (CallByName cbn in callByNames)
            {
                bool passed = false;
                string condition = cbn.Condition.ToUpper();
                if (!string.IsNullOrEmpty(condition))
                {
                    if (condition.Contains("[MAGIC_LOGICAL_NAMES]VYVOJ") || condition.Contains("N"))
                    {
                        passed = true;
                    }
                }

                if (!passed)
                {
                    notPassed.Add(cbn);
                }
            }

            return notPassed;
        }

        private List<CallByName> callByNames;
    }
}

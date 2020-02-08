using System.Collections.Generic;
using Integri.Common.Unipaas;

namespace Integri.CallByNameChecker
{
    class DangerousUsageChecker
    {
        public DangerousUsageChecker(List<CallByName> callByNames)
        {
            this.callByNames = callByNames;
            exceptions = new List<string> { "konverze.ecf" };
        }

        public List<CallByName> Check()
        {
            List<CallByName> notPassed = new List<CallByName>(callByNames.Count);
            foreach (CallByName cbn in callByNames)
            {
                if (exceptions.Contains(cbn.Cabinet.ToLower()))
                {
                    continue;
                }

                bool passed = false;
                string condition = cbn.Condition.ToUpper();
                if (!string.IsNullOrEmpty(condition))
                {
                    if (condition.Contains("[MAGIC_LOGICAL_NAMES]VYVOJ") || condition.Contains("N") ||
                        (condition.Contains("SHAREDVALGET('VYVOJ')")))
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

        private readonly List<CallByName> callByNames;
        private readonly List<string> exceptions;
    }
}

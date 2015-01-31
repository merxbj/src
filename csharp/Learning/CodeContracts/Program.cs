using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics.Contracts;

namespace CodeContracts
{
    class Program
    {
        static void Main(string[] args)
        {
            var w = new Worker();
            w.Work(new Tool());
            w.Work(null);
        }
    }

    class Worker
    {
        public bool Work(Tool tool)
        {
            // this won't work unless you install some addin into the VS!
            Contract.Requires(tool != null);
            tool.Use();
            return true;
        }
    }

    class Tool { public void Use() { } }
}

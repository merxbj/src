using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lazy
{
    class Program
    {
        static void Main(string[] args)
        {
            var utility = new Utility();
            utility.DoWork();
            Console.ReadKey();
        }
    }

    class Utility
    {
        public Utility()
        {
            educatedWorker = new Lazy<Worker>(School);
            worker = new Lazy<Worker>();
            
            Console.WriteLine("Utility created");
        }

        public void DoWork()
        {
            Console.WriteLine("Utility requests work on workers.");
            educatedWorker.Value.Work();
            worker.Value.Work();
            Console.WriteLine("Utility requests work on workers again.");
            educatedWorker.Value.Work();
            worker.Value.Work();
        }

        private Worker School()
        {
            return new Worker(true);
        }

        private Lazy<Worker> educatedWorker;
        private Lazy<Worker> worker;
    }

    class Worker
    {
        private bool education;
        public Worker()
        {
            education = false;
            Console.WriteLine("Worker created.");
        }

        public Worker(bool education)
        {
            this.education = education;
            Console.WriteLine("Worker created with education.");
        }

        public void Work()
        {
            Console.WriteLine((education ? "Educated worker" : "Worker") + " works.");
        }
    }
}

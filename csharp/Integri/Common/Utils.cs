using System;
using System.Collections.Generic;
using System.Linq;

namespace Integri.Common
{
    public static class Utils
    {
        public static List<T> Discover<T>()
        {
            List<T> instances = new List<T>();
            foreach (Type t in typeof(Utils).Assembly.GetTypes())
            {
                if (t.GetInterfaces().Contains(typeof(T)))
                {
                    T instance = (T)Activator.CreateInstance(t);
                    instances.Add(instance);
                }
            }
            return instances;
        }
    }
}

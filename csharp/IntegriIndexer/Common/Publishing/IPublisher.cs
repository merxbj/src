using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Common.Publishing
{
    public interface IPublisher<T>
    {
        void Publish(List<T> publishables);
    }
}

using System.Collections.Generic;

namespace Integri.Common.Publishing
{
    public interface IPublisher<T>
    {
        void Publish(List<T> publishables);
    }
}

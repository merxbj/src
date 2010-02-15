using System;
using System.Collections.Generic;
using System.Text;

namespace Rodokmen.Configurations.PersonsEditor
{
    using System;
    using System.Collections;
    using System.Text;

    namespace Rodokmen.Configurations.PersonsEditor
    {
        class PersonsCollection : CollectionBase
        {
            public Person this[int index]
            {
                get
                {
                    return ((Person)List[index]);
                }
                set
                {
                    List[index] = value;
                }
            }

            public int Add(Person value)
            {
                return List.Add(value);
            }

            public int IndexOf(Person value)
            {
                return List.IndexOf(value);
            }

            public void Insert(int index, Person value)
            {
                List.Insert(index, value);
            }

            public void Remove(Person value)
            {
                List.Remove(value);
            }

            public bool Contains(Person value)
            {
                return List.Contains(value);
            }

            protected override void OnValidate(Object value)
            {
                if (value.GetType() != typeof(Person))
                    throw new ArgumentException("value must be of type Person.", "value");
            }
        }
    }

}

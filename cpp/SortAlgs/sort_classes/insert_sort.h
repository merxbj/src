/*
 * insert_sort.h
 *
 *  Created on: 12.2.2009
 *      Author: eter
 */

#ifndef INSERT_SORT_H_
#define INSERT_SORT_H_

typedef unsigned long ulong;

template <typename T>
class CInsertSort
{
public:

	void Sort(T* array, ulong count)
	{
		for (ulong i = 0; i < count; i++)
		{
			T value = array[i];
			ulong j = i-1;
			while (j >= 0 && array[j] > value)
			{
				array[j + 1] = array[j];
				j--;
			}
			array[j+1] = value;
		}
	}

};

#endif /* INSERT_SORT_H_ */

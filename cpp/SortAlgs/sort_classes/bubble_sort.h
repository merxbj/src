/*
 * bubble_sort.h
 *
 *  Created on: 12.2.2009
 *      Author: eter
 */

#ifndef BUBBLE_SORT_H_
#define BUBBLE_SORT_H_

typedef unsigned long ulong;

template <typename T>
class CBubbleSort
{
public:

	void Sort(T* array, ulong count)
	{
		bool bSorted;
		for (ulong i = 0; i < count-1; i++)
		{
			bSorted = true;
			for (ulong j = i+1; j < count; j++)
			{
				if (array[i] > array[j])
				{
					T temp = array[j];
					array[j] = array[i];
					array[i] = temp;
					bSorted = false;
				}
			}
			if (bSorted)
				break;
		}
	}

};

#endif /* BUBBLE_SORT_H_ */

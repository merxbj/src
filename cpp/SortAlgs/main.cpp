#include <iostream>
#include "sort_classes.h"
#include <vector>
#include <ctime>
#include <stdlib.h>
#include <iomanip>

using namespace std;

#define ARRAY_MAX 10000
unsigned FillArray(int* array, unsigned num);
bool IsSorted(int * array, unsigned num);

int main()
{
	int array[ARRAY_MAX];
	clock_t start,finish;
	clock_t results[8];
	unsigned num = 0;
	bool success = true;

	// declarations of sort classes
	CBubbleSort<int> bubbleSort;
	CInsertSort<int> insertSort;

	for (unsigned i = 0; i <= 8; i++)
	{
		num = FillArray(array,ARRAY_MAX);
		printf("Sort [%d] is being launched\n", i+1);
		start = clock();

		//bubbleSort.Sort(array, num);
		insertSort.Sort(array, num);

		finish = clock();
		printf("Sort [%d] completed\n", i+1);
		if (!IsSorted(array, num))
		{
			success = false;
			printf("Sort [%d] FAILED\n", i+1);
			break;
		}
		results[i] = finish - start;
	}

	if (success)
	{
		printf("Generating sort results...");
		// show us a result of a final sort
		for (unsigned i = 0; i < num; i++)
		{
			printf("%4d,", array[i]);
			if (((i+1) % 20) == 0)
				printf("\n");
		}

		// and finally shwow us results
		for (unsigned i = 0; i < 8; i++)
			printf("%d: %ld, ",i, static_cast<long>(results[i]));
	}

	return 0;
}

unsigned FillArray(int* array, unsigned num)
{
	srand(static_cast<unsigned>(time(NULL)));
	unsigned count = 0;
	for (unsigned i = 0; i < num; i++)
	{
		array[i] = (rand() % 100) + 1;
		count++;
	}

	return count;
}

bool IsSorted(int * array, unsigned num)
{
	unsigned i = 0;
	bool sorted = true;
	while (sorted && (i < (num-1)))
	{
		sorted = (array[i] <= array[i+1]);
		i++;
	}

	return (i == (num - 1));
}

#include "darts.h"

int main()
{
	hits_t hits;
	darts_c darts_counter;

	hits = darts_counter.guess_hits(501);
	darts_counter.show_hits(hits);

	system("PAUSE");
	return 0;
}
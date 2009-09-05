#include "darts.h"

int main(int argc, char** argv)
{
    int startScore = 501;
    if (argc > 1)
        startScore = atoi(argv[1]);

    hits_t hits;
    darts_c darts_counter;

    hits = darts_counter.guess_hits(startScore);
    //darts_counter.show_hits(hits);

    return 0;
}

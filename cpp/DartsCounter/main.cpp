#include "darts.h"

int main(int argc, char** argv)
{
    int startScore = 501;
    if (argc > 1)
        startScore = atoi(argv[1]);

    results_t* results;
    darts_c* darts_counter = new darts_c();

    results = darts_counter->guess_hits(startScore);
    darts_counter->print_results(results);

    delete darts_counter;

    return 0;
}

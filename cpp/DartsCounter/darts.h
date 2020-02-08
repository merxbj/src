#pragma once

#include <iostream>
#include "stdlib.h"

#define ALL_ZONES_COUNT 43
#define DOUBLE_ZONES_COUNT 21
#define MAX_KEEPED_RESULTS 100

static const int all_zones[ALL_ZONES_COUNT] = {
     1, 2, 3, 4, 5, 6, 7, 8, 9,10,
    11,12,13,14,15,16,17,18,19,20,
    21,22,24,26,27,28,30,32,33,34,
    35,36,38,39,40,42,45,48,50,51,
    54,57,60};

static const int double_zones[DOUBLE_ZONES_COUNT] = {
     2, 4, 6, 8,10,12,14,16,18,20,
    22,24,26,28,30,32,34,36,38,40,
    50};

typedef struct _results_t
{
    int throws;
    int double_out;
    int rest;
} results_t;

class darts_c
{
public:
    // construction/destruction
    darts_c();
    ~darts_c();

    // public methods
    results_t* guess_hits(int target_score);
    void print_results(const results_t* results);

private:

    // simple garbage collector
    results_t* results[MAX_KEEPED_RESULTS];
    int results_count;

    // private methods
    int find_value(int const * zones, int size, int value);
};

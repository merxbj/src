#include "darts.h"

darts_c::darts_c()
{
    results_count = 0;
}

darts_c::~darts_c()
{
   //printf("We have %i results prepared to delete - let's do this!", results_count);
   for (int i = 0; i < results_count; i++)
   {
       //printf("Deleting %i result!\n", i+1);
       delete results[i];
   }
}

results_t* darts_c::guess_hits(int target_score)
{
    int biggest = all_zones[ALL_ZONES_COUNT - 1];
    int throws = target_score / biggest;
    int rest = target_score % biggest;
    int finalDouble = rest;
    int doubleRest = 0;

    if (find_value(double_zones, DOUBLE_ZONES_COUNT, rest) == -1) // we cannot double it out
    {
        if ((throws > 0) && (rest < double_zones[DOUBLE_ZONES_COUNT - 1]))
        {
            --throws;
            rest += biggest;
        }

        for (int i = 0; i < DOUBLE_ZONES_COUNT; i++)
        {
            doubleRest = rest - double_zones[i];
            if (find_value(all_zones, ALL_ZONES_COUNT, doubleRest) != -1)
            {
                finalDouble = double_zones[i];
                break;
            }
        }
    }

    results_t* result = new results_t();
    result->throws = throws;
    result->double_out = finalDouble;
    result->rest = doubleRest;
    results[++results_count] = result;

    return result;
}

int darts_c::find_value(int const* zones, int size, int value)
{
    int low = 0;
    int high = size;
    int index = (low + high) / 2;

    while ((high >= low) && (zones[index] != value))
    {
        if (value > zones[index])
            low = index + 1;
        else
            high = index - 1;
        index = (low + high) / 2;
    }

    if (high < low)
        index = -1;

    return index;
}

void darts_c::print_results(const results_t* result)
{
    int total = 0;

    for (int i = 0; i < result->throws; i++)
    {
        total += all_zones[ALL_ZONES_COUNT - 1];
        std::cout << all_zones[ALL_ZONES_COUNT - 1] << ", ";
    }

    if (result->rest > 0) 
    {
        total += result->rest;
        std::cout << result->rest;        
    }
    
    total += result->double_out;
    std::cout << ", " << result->double_out << std::endl;

    std::cout << "\tTotal points of: " << total << std::endl;
}

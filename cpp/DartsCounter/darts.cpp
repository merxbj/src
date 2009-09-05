#include <iostream>
#include "stdlib.h" // for rand and srand
#include <cstring> // for memcpy and memset
#include <ctime> // for random initialization
#include "darts.h"

darts_c::darts_c()
{
    initialize();
}

darts_c::~darts_c()
{
    delete_points();
}

void darts_c::initialize()
{
    fill_points();
    fill_unifyied();
    filter_mult(points_sng, 1);
    filter_mult(points_dbl, 2);
    filter_mult(points_trpl, 3);
}

void darts_c::delete_points()
{
    for (hits_t::iterator it = points.begin(); it != points.end(); it++)
        delete *it;
}

hits_t& darts_c::guess_hits(int score)
{
    guess_hits_impl(score);
    return hits;
}

void darts_c::guess_hits_impl(int score)
{
    hits_t::reverse_iterator biggest = points_un.rbegin();
    int throws = score / (*biggest)->value;;
    int rest = score % (*biggest)->value;;
    int finalDouble = rest;;
    int doubleRest = 0;

    if (find_value(points_dbl, rest) == -1)
    {
        if (throws > 0)
        {
            --throws;
            rest += (*biggest)->value;
        }

        for (hits_t::reverse_iterator doublesIt = points_dbl.rbegin(); doublesIt != points_dbl.rend(); doublesIt++)
        {
            doubleRest = rest - (*doublesIt)->value;
            if (find_value(points_un, doubleRest) != -1)
            {
                finalDouble = (*doublesIt)->value;
                break;
            }
        }
    }

    std::cout << (*biggest)->value << " x " << throws << std::endl;
    std::cout << doubleRest << " and final " << finalDouble << std::endl;
    std::cout << (*biggest)->value * throws + doubleRest + finalDouble << std::endl;
}

int darts_c::fill_zones(int* zones)
{
    // this is classic dart board (1 - 20, bullseye)
    const int count = ZONES_COUNT;
    for (int i = 0; i < count-1; i++)
        zones[i] = i+1;
    zones[count-1] = 25; // bullseye
    return count;
}

int darts_c::fill_points()
{
    int dartboard_zones[ZONES_COUNT];
    int count = fill_zones(dartboard_zones);

    int j = 0;
    for (int i = 0; i < count; i++)
    {
        hit_t* single_hit = new hit_t(dartboard_zones[i], 1);
        hit_t* double_hit = new hit_t(dartboard_zones[i], 2);
        hit_t* triple_hit = new hit_t(dartboard_zones[i], 3);

        points.push_back(single_hit);
        points.push_back(double_hit);
        if (i < count-1) points.push_back(triple_hit); // bulls-eye doesn't have a tripple hit
    }

    sort(points);

    return points.size();
}

void darts_c::show_hits(const hits_t& hits)
{
    int throw_num = 0;

    std::cout << std::endl;
    for(hits_t::const_iterator it = hits.begin(); it != hits.end(); it++)
    {
        std::cout << "Throw " << ++throw_num << ": " << (*it)->value << std::endl;
    }
    std::cout << std::endl;
}

int darts_c::fill_unifyied()
{
    int j = 0;
    hit_t* last = NULL;

    for (hits_t::iterator it = points.begin(); it != points.end(); it++)
    {
        // this function expects sorted vector!
        if (!last || (last->value != (*it)->value))
        {
            points_un.push_back(*it);
        }
        last = *it;
    }

    return points_un.size();
}

int darts_c::filter_mult(hits_t& filtered, int mult)
{
    for (hits_t::iterator it = points.begin(); it != points.end(); it++)
    {
        if ((*it)->mult == mult)
        {
            filtered.push_back(*it);
        }
    }
    return filtered.size();
}

int darts_c::sort(hits_t& sorted)
{
    for (int i = 1; i < sorted.size(); i++)
    {
        int j = i;
        hit_t* point = sorted[i];
        while ((j > 0) && (point->value < sorted[j-1]->value))
        {
            sorted[j] = sorted[j-1];
            j--;
        }
        sorted[j] = point;
    }

    return sorted.size();
}


int darts_c::find_value(const hits_t& hits, int value)
{
    int low = 0;
    int high = hits.size() - 1;
    int index = (low + high) / 2;

    while ((high >= low) && (hits[index]->value != value))
    {
        if (value > hits[index]->value)
            low = index + 1;
        else
            high = index - 1;
        index = (low + high) / 2;
    }

    if (high < low)
        index = -1;

    return index;
}

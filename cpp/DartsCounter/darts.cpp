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
    if (score == 0) // didn't we win?
    {
        return;
    }
    else if ((score % 2 == 0) && (score <= points_dbl.back()->value))
    {
        // try to finish 
        // if it's not possible 
            // pop last hit from stack
            // if not possible (stack is empty)
                // push best fit value (new score >= 2)
            // push new - lower value hit
            // recursion with appropriate score
    }
    else
    {
        // push best fit value (new score >= 2)
        hits_t::reverse_iterator it = points_un.rbegin();
        int new_score = 0;
        while ((new_score = score - ((*it)->value)) < 0)
        {
            it++;
        }
        hits.push_back((*it));
    }
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

bool darts_c::pass_end_req(const hit_t* point)
{
    // best starting value (in fact ending) is bigger than biggest single of 3 points of value
    return (point->value > (points_sng.back()->value + 3));
}

void darts_c::fix_hits()
{
    // try to fix last three hits
    hits_t::reverse_iterator last = hits.rbegin();
    hits_t::reverse_iterator next_last = last + 1;
    
    // check last two hits
    int sum = (*last)->value + (*next_last)->value;

    if (sum < points.back()->value) // we might hit better
    {
        hits.pop_back();
        hits.pop_back(); // remove last two hits
        sum += hits.back()->value;
        last = std::find(points.rbegin(), points.rend(), hits.back());
        hits.pop_back();
        hits.push_back(*(++last));
        guess_hits_impl(sum - (*last)->value);
    }
}
/*

// This one is trying to be more "random", but is quite complicated!
// even there is a mistake in thirth if statement! what if the score is 48!!!

void darts_c::guess_hits_impl(int score)
{
    if (score == 0) // didn't we win?
    {
        fix_hits();
        return;
    }

    int new_score = 0;

    if (this->hits.size() == 0) // start with any of doubles - we will reverse the vector at the end
    {
        if ((score % 2 == 0) && (score <= points_dbl.back()->value)) // we can finish it immediately
        {
            new_score = score - points_dbl[(score / 2) - 1]->value; // array is sorted
        }
        else
        {
            srand(static_cast<unsigned>(time(NULL)));
            bool* used = new bool[points_dbl.size()];
            memset(used, 0, points_dbl.size());

            int double_hit = rand() % points_dbl.size();
            while (((new_score = score - this->points_dbl[double_hit]->value) < 0) ||
                     !pass_end_req(this->points_dbl[double_hit]))
            {
                used[double_hit] = true;
                while (((double_hit = rand() % points_dbl.size()), used[double_hit])); // choose new unused double hit
            }

            hits.push_back(points_dbl[double_hit]);
            delete [] used; // we don't want it anymore
        }
    }
    else // common situation
    {
        hits_t::reverse_iterator it = points_un.rbegin();
        while ((new_score = score - ((*it)->value)) < 0)
        {
            it++;
        }
        hits.push_back((*it));
    }

    guess_hits_impl(new_score); // and now the mighty recursion
}
*/

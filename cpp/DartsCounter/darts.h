#pragma once

#include <vector>
#include <algorithm> // for find
#include <iostream>

#define ZONES_COUNT 21

typedef struct hit_s
{
	int hit;
	int mult;
	int value;

	hit_s(int _hit, int _mult)
	{
		this->hit = _hit;
		this->mult = _mult;
		this->value = hit * mult;
	}

	~hit_s()
	{
		// for debugging reasons
		std::cout << "-hit" << std::endl;
	}
} hit_t;

typedef std::vector<hit_t*> hits_t;

class darts_c
{
public:
	// construction/destruction
	darts_c();
	~darts_c();

	// public methods
	hits_t& guess_hits(int score);
	void show_hits(const hits_t& hits);

private:
	// private members
	hits_t points;
	hits_t points_sng;
    hits_t points_dbl;
    hits_t points_trpl;
    hits_t points_un;
	hits_t hits;

	// private methods
	void initialize();
	void guess_hits_impl(int score);
	void fix_hits();
	int fill_zones(int* zones);
	int fill_points();
	void delete_points();
	int fill_unifyied();
	int filter_mult(hits_t& filtered, int mult);
	bool pass_end_req(const hit_t* point);
	int sort(hits_t& sorted);
};
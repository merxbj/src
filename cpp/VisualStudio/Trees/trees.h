#ifndef _trees_h_
#define _trees_h_

#include <iostream>

#define MAX_CUSPS 100
#define MAX_MATRIX 1000

struct cusp_t
{
	long x;
	long y;
};

struct matrix_t
{
	long x;
	long y;
	long n_cusps;
	struct cusp_t* cusps;
};

struct business_object_t
{
	struct matrix_t& matrix;
	int result; // num of trees within the object
	int exit_code;

	business_object_t(matrix_t& _matrix) : matrix(_matrix), result(0), exit_code(0) 
	{
		;
	}
};

bool enter_matrix(matrix_t& new_matrix);
bool enter_cusps(cusp_t* cusps, matrix_t& matrix);
void handle_exit(int exit_code);
void process_business_object(business_object_t& bo);
void handle_result(const business_object_t& bo);
bool set_matrix_cusps(bool real_matrix[][MAX_MATRIX], const business_object_t& bo);
bool found_shape_edges(bool** real_matrix, const business_object_t& bo);
bool found_points_inside(bool** real_matrix, const business_object_t& bo);
bool found_result(business_object_t& bo, const bool* const* real_matrix);

#endif
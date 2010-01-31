#include "trees.h"

bool enter_matrix(matrix_t& new_matrix)
{
	bool return_value = true;
	std::cout << "Enter matrix parameters: ";
	return_value = (std::cin >> new_matrix.x >> new_matrix.y >> new_matrix.n_cusps);
	return return_value;
}

bool enter_cusps(cusp_t* cusps, matrix_t& matrix)
{
	bool return_value = true;
	int i = 0;

	std::cout << "Enter each of " << matrix.n_cusps << " cusps: " << std::endl;
	while (return_value && i < matrix.n_cusps)
	{
		return_value = std::cin >> cusps[i].x >> cusps[i].y;
		i++;
	}

	matrix.cusps = cusps;

	return (return_value && (i == matrix.n_cusps));
}

void handle_exit(int exit_code)
{
	switch (exit_code)
	{
	case 1:
		std::cout << "Chyba!" << std::endl;
	default:
		std::cout << "Nashledanou" << std::endl;
	}

	system("PAUSE");
}

void process_business_object(business_object_t& bo)
{
	bool real_matrix[MAX_MATRIX][MAX_MATRIX];
	memset(real_matrix, false, sizeof(real_matrix));

	if (!set_matrix_cusps(real_matrix, bo) || 
		!found_shape_edges(real_matrix, bo) ||
		!found_points_inside(real_matrix, bo) ||
		!found_result(bo, real_matrix))
	{
		bo.exit_code = 1;
	}
}
void handle_result(const business_object_t& bo)
{
	std::cout << "Vysledkem je " << bo.result << " stromu.";
}

bool set_matrix_cusps(bool** real_matrix, const business_object_t& bo)
{
	for (int i = 0; i < bo.matrix.n_cusps; i++)
	{
		real_matrix[bo.matrix.cusps[i].x][bo.matrix.cusps[i].y] = true;
	}
	return true;
}

bool found_shape_edges(bool** real_matrix, const business_object_t& bo)
{
	return true;
}

bool found_points_inside(bool** real_matrix, business_object_t& bo)
{
	return true;
}

bool found_result(business_object_t& bo, const bool* const* real_matrix)
{
	return true;
}
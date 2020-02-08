#include "trees.h"

int main()
{
	int exit_code = 1;
	
	cusp_t cusps[MAX_CUSPS]; // vrcholy n-uhelnika
	matrix_t matrix; // matice x,y

	if (enter_matrix(matrix) &&
		enter_cusps(cusps, matrix))
	{
		business_object_t bo(matrix);
		process_business_object(bo);
		handle_result(bo);
		exit_code = bo.exit_code;
	}

	handle_exit(exit_code);
	return exit_code;
}
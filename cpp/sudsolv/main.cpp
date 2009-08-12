#include "sudsolv.h"

int game_field_template[9][9] =
{
    {0,1,0,7,0,6,3,0,0},
    {8,0,3,0,0,5,0,0,7},
    {0,0,0,0,1,0,0,0,5},
    {0,2,0,0,8,0,0,5,0},
    {0,0,8,4,0,7,2,0,0},
    {0,7,0,0,3,0,0,4,0},
    {2,0,0,0,7,0,0,0,0},
    {2,0,0,0,7,0,0,0,0},
    {0,0,4,8,0,9,0,7,0}
};

int main()
{
    game_field_t game_field;
    
    if (populate_game_field(game_field_template, game_field))
    {
        if (solve_game_field(game_field))
        {
            display_game_field(game_field);
        }
    }
    
    system("PAUSE");
    return 0;
}

bool solve_game_field(game_field_t& game_field)
{
    bool valid = inspect_game_field(game_field);
    
    while (!valid)
    {
        valid = true;
    }

    return true;

}

bool populate_game_field(const int game_field_template [][9], game_field_t& game_field)
{
    size_t count = 0;
    
    for (int y = 0; y < 9; y++)
    {
        for (int x = 0; x < 9; x++)
        {
            field_t* new_field = new field_t(game_field_template[x][y]);
            memset(new_field->allowed, true, 9);
            game_field.fields[x][y] = new_field;
            ++count;
        }
    }

    return (count == 81);
}

void display_game_field(const game_field_t& game_field)
{
    printf("\n");
    for (int y = 0; y < 9; y++)
    {
        for (int x = 0; x < 9; x++)
        {
            int number = game_field.fields[x][y]->number;
            if (number != 0)
                printf(" [%d] ", number);
            else
                printf(" [ ] ");
        }
        printf("\n\n");
    }
}

bool inspect_game_field(game_field_t& game_field)
{
    for (int i = 0; i < 9; i++)
    {
        inspect_row(game_field, i);
        inspect_column(game_field, i);
    }

    for (int j = 0; j < 3; j++)
        for (int k = 0; k < 3; k++)
            inspect_block(game_field, j, k);

    return true;
}

bool inspect_block(game_field_t& game_field, int x, int y)
{
    bool valid = true;
    int block_x = x * 3;
    int block_y = y * 3;
    
    for (int j = block_y; j < block_y + 3; j++)
    {
        for (int i = block_x; i < block_x + 3; i++)
        {
            int number = game_field.fields[x][y]->number;
            if (number != 0)
            {
                for (int j_adj = block_y; j_adj < block_y; j_adj++)
                {
                    for (int i_adj = block_y; i_adj < block_y + 3; i_adj++)
                    {
                        if (game_field.fields[i_adj][j_adj]->number == 0)
                        {
                            game_field.fields[i_adj][j_adj]->allowed[number] = false;
                            valid = false;
                        }
                    }
                }
            }
        }
    }

    return valid;
}

bool inspect_row(game_field_t& game_field, int x)
{
    bool valid = true;
    
    for (int y = 0; y < 9; y++)
    {
        int number = game_field.fields[x][y]->number;
        if (number != 0)
        {
            for (int y_adj = 0; y_adj < 9; y_adj++)
            {                
                if (game_field.fields[x][y_adj]->number == 0)
                {
                    game_field.fields[x][y_adj]->allowed[number] = false;
                    valid = true;
                }
            }
        }
    }

    return valid;
}

bool inspect_column(game_field_t& game_field, int y)
{
    bool valid = true;
    
    for (int x = 0; x < 9; x++)
    {
        int number = game_field.fields[y][x]->number;
        if (number != 0)
        {
            for (int x_adj = 0; x_adj < 9; x_adj++)
            {                
                if (game_field.fields[y][x_adj]->number == 0)
                {
                    game_field.fields[y][x_adj]->allowed[number] = false;
                    valid = true;
                }
            }
        }
    }

    return valid;
}
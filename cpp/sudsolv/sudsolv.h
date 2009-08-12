#pragma once

#include <iostream>

typedef struct field_s
{
    int number;
    bool allowed[9];

    field_s(int _number):number(_number) {}

} field_t;

typedef struct game_field_s
{
    field_t* fields[9][9];
} game_field_t;

bool populate_game_field(const int game_field_template [][9], game_field_t& game_field);
void display_game_field(const game_field_t& game_field);
bool solve_game_field(game_field_t& game_field);

bool inspect_game_field(game_field_t& game_field);
bool inspect_block(game_field_t& game_field, int x, int y);
bool inspect_row(game_field_t& game_field, int x);
bool inspect_column(game_field_t& game_field, int y);
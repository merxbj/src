#include "pch.h"
#include "Algos2.h"

template<class T>
std::ostream& operator<<(std::ostream& out, const std::vector<T>& vec)
{
    out << "[";
    bool first = true;
    for (const T& element : vec)
    {
        if (!first) 
        {
            out << ", ";
        }
        else
        {
            first = false;
        }

        out << std::to_string(element);
    }
    out << "]";

    return out;
}

namespace my_std
{
    template<typename It>
    void reverse(It begin, It end)
    {
        while (begin < end)
        {
            std::swap(*begin, *(--end));
            begin++;
        }
    }
}

int Algos2::main()
{
    std::vector<int> vec(20);

    std::generate(vec.begin(), vec.end(), []()
        {
            return rand() % 100;
        });

    std::cout << vec << std::endl;

    std::make_heap(vec.begin(), vec.end());
    std::cout << vec << std::endl;

    std::sort_heap(vec.begin(), vec.end());
    std::cout << vec << std::endl;

    std::make_heap(vec.begin(), vec.end());

    vec.push_back(80);
    std::push_heap(vec.begin(), vec.end());
    std::cout << vec << std::endl;

    auto it = vec.end();
    while (it != vec.begin() + 1)
    {
        std::pop_heap(vec.begin(), it--);
        std::cout << vec << std::endl;
    }

    std::random_device rd;
    std::mt19937 g(rd());
    std::shuffle(vec.begin(), vec.end(), g);
    std::cout << vec << std::endl;

    std::partition(vec.begin(), vec.end(), [](auto&& elem)
        {
            return elem % 2 == 0;
        });
    std::cout << vec << std::endl;

    std::string sent = "a quick brown fox jumped over the lazy dog";

    auto reverse_words = [] (std::string& str) -> std::string&
    {
        my_std::reverse(str.begin(), str.end());

        for (auto it = str.begin(); true; it++)
        {
            auto next = std::find(it, str.end(), ' ');
            my_std::reverse(it, next);
            if (next == str.end())
            {
                break;
            }
            it = next;
        }

        return str;
    };

    std::cout << reverse_words(sent) << std::endl;

    return 0;
}

#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <map>

using namespace std;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 * ---
 * Hint: You can use the debug stream to print initialTX and initialTY, if Thor seems not follow your orders.
 **/

int test()
{
    int lightX; // the X position of the light of power
    int lightY; // the Y position of the light of power
    int initialTX; // Thor's starting X position
    int initialTY; // Thor's starting Y position
    cin >> lightX >> lightY >> initialTX >> initialTY; cin.ignore();

    map<pair<int, int>, string> dirs = {
        { make_pair(0,1), "N" }, { make_pair(-1,1), "NW" },
        { make_pair(1,1), "NE" }, { make_pair(0,-1), "S" },
        { make_pair(-1,-1), "SW" }, { make_pair(1,-1), "SE" },
        { make_pair(1,0), "E" }, { make_pair(-1,0), "W" }
    };

    // game loop
    while (1) {
        int remainingTurns; // The remaining amount of turns Thor can move. Do not remove this line.
        cin >> remainingTurns; cin.ignore();

        // Write an action using cout. DON'T FORGET THE "<< endl"
        // To debug: cerr << "Debug messages..." << endl;

        int vecX = lightX - initialTX;
        int vecY = lightY - initialTY;
        vecX = vecX == 0 ? 0 : vecX / vecX;
        vecY = vecY == 0 ? 0 : vecY / vecY;

        // A single line providing the move to be made: N NE E SE S SW W or NW
        cout << dirs[make_pair(vecX, vecY)] << endl;

        initialTX += vecX;
        initialTY += vecY;
    }
}
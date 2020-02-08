#include "pch.h"
#include "Futures.h"

using namespace std;
using namespace std::chrono;

Futures::Futures()
{
}


Futures::~Futures()
{
}

int accum(const std::vector<int>& part)
{
    if (part.size() < 10000)
    {
        return accumulate(part.begin(), part.end(), 0);
    }

    auto f0 = async(accum, vector<int>{ part.begin(), next(part.begin(), part.size() * 1 / 4) });
    auto f1 = async(accum, vector<int>{ next(part.begin(), part.size() * 1 / 4), next(part.begin(), part.size() * 2 / 4) });
    auto f2 = async(accum, vector<int>{ next(part.begin(), part.size() * 2 / 4), next(part.begin(), part.size() * 3 / 4) });
    auto f3 = async(accum, vector<int>{ next(part.begin(), part.size() * 3 / 4), part.end() });

    return f0.get() + f1.get() + f2.get() + f3.get();
}

int Futures::main()
{
    std::vector<int> vec(1000000, 0);
    generate(vec.begin(), vec.end(), []()
    {
        return rand() % 1000;
    });

    auto start = high_resolution_clock::now();

    cout << "Sum is: " << accum(vec) << endl;

    auto stop = high_resolution_clock::now();

    cout << "And it took " << duration_cast<milliseconds>(stop - start).count() << "ms to accumulate" << endl;
    
    auto row = make_tuple(string("Stuff"), 1, 2, 3.4);

    cout << get<0>(row) << endl;

    auto die = bind(uniform_int_distribution<>(1, 6), default_random_engine());
    cout << die() << endl;

    /*
    regex pattern(R"(\w{2}\s*\d{5}(-\d{4})?)");

    for (string line; getline(cin, line);)
    {
        smatch matches;
        if (regex_match(line, matches, pattern))
        {
            cout << matches[0] << endl;
            break;
        }
    }
    */

    return 0;
}

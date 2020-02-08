#include <list>
#include <string>
#include <fstream>
#include <algorithm>
#include <iostream>
#include <iomanip>
#include <numeric>
#include <map>
#include <random>
#include <time.h>

// please forgive me!
using namespace std;

class UpperCaseLine
{
    string data;
public:
    friend istream& operator>>(istream &is, UpperCaseLine &l) 
    {
        getline(is, l.data);
        transform(l.data.begin(), l.data.end(), l.data.begin(), ::toupper);
        return is;
    }
    operator string() const { return data; }
};

struct WordLenghtRatio
{
    WordLenghtRatio(int _length = 0, double _ratio = 0.0) : length(_length), ratio(_ratio) {}
    int length;
    double ratio;
};

int main(int, char **argv)
{
    // load up words (one per line) from the dictionary
    list<string> words;
    {
        // intentional block to let the file close as soon as we finished reading from it
        ifstream file = ifstream(argv[1]);

        std::copy(istream_iterator<UpperCaseLine>(file), 
                  istream_iterator<UpperCaseLine>(),
                  back_inserter(words));

        cout << "Number of lines: " << words.size() << endl;
        cout << endl;
    }

    // caclulate frequency of every possible word length
    map<int, long> wordLenghts;
    for_each (begin(words), end(words), [&] (string word)
    {
        long count = wordLenghts[word.length()];
        wordLenghts[word.length()] = ++count;
    });

    // calucalte the ratio (percentage) of every length frequency
    vector<WordLenghtRatio> ratios;
    transform(begin(wordLenghts), end(wordLenghts), back_inserter(ratios), [&](pair<int, long> wl)
    {
        return WordLenghtRatio(wl.first, ((double) wl.second) / words.size());
    });

    // sort (length,ratios) pairs by ratios by an ascending order
    sort(begin(ratios), end(ratios), [&](const WordLenghtRatio& lhs, const WordLenghtRatio& rhs)
    {
        return lhs.ratio < rhs.ratio;
    });

    // sum of all ratios makes up 1.0 (100%), let's distribute all the ratios through the 100% as in
    // a single bar plot: [|_|_|_|____|______|_____________|____________________]
    // so you can pick a random number <0.0, 1.0> that would immediately tell you a random word length
    // with a proper distribution (you would get more probable word lenths in the given language with
    // higher probability)
    vector<WordLenghtRatio> mappings;
    copy(begin(ratios), end(ratios), back_inserter(mappings));
    double lowerBound = 0.0;
    for_each(begin(mappings), end(mappings), [&](WordLenghtRatio& wlr)
    {
        double originalRatio = wlr.ratio;
        wlr.ratio += lowerBound;
        lowerBound += originalRatio;
    });

    srand((unsigned int)time(NULL));
    bool done = false;
    while (!done)
    {
        // get a randomg word length
        double lengthMapping = rand() / double(RAND_MAX); // <0.0, 1.0>
        auto lengthIter = find_if(begin(mappings), end(mappings), [=](const WordLenghtRatio& wlr)
        {
            // mappings are sorted INC -> first boundary that is higher or equals than what we've generated is what we want
            return wlr.ratio >= lengthMapping;
        });
        int randomWordLengthWithProperDistribution = (*lengthIter).length;

        // generate some random chars
        list<char> randomChars;
        generate_n(back_inserter(randomChars), randomWordLengthWithProperDistribution, []()
        {
            return 'A' + rand() % 26;
        });

        // build a word from those chars and check if that is a real word
        string word = string(begin(randomChars), end(randomChars));
        if (find(begin(words), end(words), word) != words.end())
        {
            if (word.length() > 5)
            {
                cout << "Hey, my monkey just typed an actual word: " << word  << " !" << endl;
            }
        }
    }

    system("PAUSE");
}
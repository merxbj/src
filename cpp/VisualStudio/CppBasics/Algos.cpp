#include "pch.h"
#include "Algos.h"

int Algos::main()
{
    std::vector<std::string> words;
    words.reserve(480000);

    std::ifstream fs("C:\\temp\\words.txt", std::ios_base::in);
    std::copy(std::istream_iterator<std::string>(fs), std::istream_iterator<std::string>(), std::back_inserter(words));

    std::for_each(words.begin(), words.begin() + 10, [](auto word) {
        std::cout << word << std::endl;
    });

    words.erase(std::remove_if(words.begin(), words.end(), [](const auto& word) { return word == ""; }), words.end());

    std::cout << std::endl << std::distance(words.begin(), words.end()) << std::endl;

    std::cout << std::endl << std::count(words.begin(), words.end(), "Test") << std::endl;

    std::cout << std::endl << std::count_if(words.begin(), words.end(), [](const std::string& word) {

        return word.find("ups") != std::string::npos;

    }) << std::endl;

    auto testIt = std::find(words.begin(), words.end(), "Test");

    std::cout << std::endl << *(testIt-1) << " < > " <<*(testIt + 1) << std::endl;

    auto isUps = [](const std::string& word) {

        return word.find("ups") != std::string::npos;

    };

    std::cout << std::endl;

    auto findIt = std::find_if(words.begin(), words.end(), isUps);
    while (findIt != words.end())
    {
        std::cout << *findIt << std::endl;

        findIt = std::find_if(findIt + 1, words.end(), isUps);
    }

    std::list<std::string> copied;
    std::copy(words.begin(), words.begin() + 10, std::back_inserter(copied));

    std::list<std::string> moved;
    std::move(copied.begin(), copied.end(), std::back_inserter(moved));

    std::transform(moved.begin(), moved.end(), moved.begin(), [](auto word) -> std::string {
        std::transform(word.begin(), word.end(), word.begin(), ::toupper);
        return word;
    });

    std::vector<int> ints1 {1, 2, 3, 4, 5, 6};
    std::vector<int> ints2 {1, 2, 3, 4, 5, 6};

    bool equal = std::equal(ints1.begin(), ints1.end(), ints2.begin(), ints2.end());

    ints1[2] = 7;
    auto mismatch = std::mismatch(ints1.begin(), ints1.end(), ints2.begin(), ints2.end());
    *(mismatch.first) = *(mismatch.second) = (*(mismatch.first) + *(mismatch.second)) / 2;

    std::list<std::string> curses{ "ass", "fuck", "shit" };
    
    auto res = std::find_first_of(words.begin(), words.end(), curses.begin(), curses.end());
    while (res != words.end())
    {
        std::cout << std::endl << *(res - 1) << " < " << *res << " > " << *(res + 1) << std::endl;

        res = std::find_first_of(res + 1, words.end(), curses.begin(), curses.end());
    }

    bool pepaInDict = std::any_of(words.begin(), words.end(), [](const auto& word) { return word == "Pepa"; });

    bool pepaNotInDict = std::none_of(words.begin(), words.end(), [](const auto& word) { return word == "Pepa"; });

    auto pred = [](const auto& word) {
        return (word[0] >= 'a' && word[0] <= 'z') || (word[0] >= 'A' && word[0] <= 'Z') || (word[0] >= '0' && word[0] <= '9');
    };

    auto nonPred = [&pred](const auto& word) { return !pred(word); };

    bool allStartsWithCapitalOrNumber = std::all_of(words.begin(), words.end(), pred);

    auto nonWord = std::find_if_not(words.begin(), words.end(), pred);

    int nonWordCount = std::count_if(words.begin(), words.end(), nonPred);

    return 0;
}
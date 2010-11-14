/* 
 * File:   main.cpp
 * Author: eter
 *
 * Created on 31. říjen 2010, 22:50
 */

#include <cstdlib>
#include "ArrayList.h"

using namespace std;

class Data {
public:

    Data() {
        memset(data, 0, sizeof(data));
    }

    Data(const char* text) {
        strncpy(data, text, sizeof(data));
    }

    Data(const Data& copy) {
        memcpy(data, copy.data, sizeof(data));
    }

    char* getData() {
        return data;
    }

private:
    char data[10000];
};

/*
 * 
 */
int main(int argc, char** argv) {
    ArrayList<Data> list = ArrayList<Data>();
    list.pushBack(Data("Ahoj"));
    list.pushBack(Data(","));
    list.pushBack(Data("ja"));
    list.pushBack(Data("jsem"));
    list.pushBack(Data("nezpoutane"));
    list.pushBack(Data("pole"));
    list.pushBack(Data("!"));

    while (list.getSize() > 0) {
        Data d = list.popBack();
        std::cout << d.getData() << " ";
    }
    std::cout << std::endl;

    return 0;
}


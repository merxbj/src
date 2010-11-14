/* 
 * File:   ArrayList.h
 * Author: eter
 *
 * Created on 31. říjen 2010, 22:54
 */

#ifndef ARRAYLIST_H
#define	ARRAYLIST_H

#include <cstring>
#include <iostream>

const unsigned short defaultPreallocation = 2;
const unsigned short growFactor = 4;

class ArrayOutOfBoundsException {
    
};

template <typename E>
class ArrayList {
public:
    ArrayList() {
        this->array = new E[defaultPreallocation];
        this->allocation = defaultPreallocation;
        this->size = 0;
    }

    ArrayList(int prealocation) {
        this->array = new E[prealocation];
        this->allocation = prealocation;
        this->size = 0;
    }

    virtual ~ArrayList() {
        delete [] array;
    }

    bool pushBack(E element) {
        this->ensureCapacity();
        this->array[size++] = element;
        return true;
    }

    E get(unsigned int index) {
        this->boundsCheck(index);
        return this->array[index];
    }

    E popBack() {
        this->boundsCheck(size - 1);
        return this->array[--size];
    }

    unsigned int getSize() {
        return this->size;
    }

private:
    void ensureCapacity() {
        if (size == allocation) {
            std::cout << "Going to realocate from " << allocation << " to " << allocation * growFactor << std::endl;
            E* temp = new E[allocation * growFactor];
            memcpy(temp, array, allocation);
            delete [] array;
            array = temp;
            allocation *= growFactor;
        }
    }

    void boundsCheck(unsigned int index) {
        if ((index >= this->size) || (index < 0)) {
            throw ArrayOutOfBoundsException();
        }
    }

private:
    E* array;
    unsigned int size;
    unsigned int allocation;
};

#endif	/* ARRAYLIST_H */


#pragma once

#include <type_traits>

template<class T>
struct my_linked_list_node
{
    T _value;
    my_linked_list_node* _next;
};

template<class T, bool Const>
class my_linked_list_iterator
{
    using node_pointer = std::conditional_t<Const, const my_linked_list_node<T>*, my_linked_list_node<T>*>;
    node_pointer _node;

public:

    using difference_type = std::ptrdiff_t;
    using value_type = T;
    using pointer = std::conditional_t<Const, const T*, T>;
    using reference = std::conditional_t<Const, const T&, T&>;
    using iterator_category = std::forward_iterator_tag;

    explicit my_linked_list_iterator(node_pointer node) : _node(node) {}

    reference operator*() const { return _node->_value; };
    auto& operator++() { _node = _node->_next; return *this; }
    auto operator++(int) { auto old = *this; _node = _node->_next; return old; }
    
    template<class R, bool C>
    bool operator==(const my_linked_list_iterator<R, C>& rhs)
    {
        return _node == rhs._node;
    }

    template<class R, bool C>
    bool operator!=(const my_linked_list_iterator<R, C>& rhs)
    {
        return _node != rhs._node;
    }

    operator my_linked_list_iterator<T, true>() { return my_linked_list_iterator<T, true>(_node); }
};

template<class T>
class my_linked_list
{
public:

    using iterator = my_linked_list_iterator<T, false>;
    using const_iterator = my_linked_list_iterator<T, true>;
    using value_type = T;

    my_linked_list() : _head(nullptr), _tail(nullptr), _size(0)
    {
    }

    void push_back(T value)
    {
        if (_tail == nullptr)
        {
            _head = _tail = new my_linked_list_node<T>();
        }
        else
        {
            _tail->_next = new my_linked_list_node<T>();
            _tail = _tail->_next;
        }

        _tail->_value = value;
        _size++;
    }

    int size() const { return _size; }

    iterator begin() const
    {
        return iterator(_head);
    }

    iterator end() const
    {
        return iterator(nullptr);
    }

    const_iterator cbegin() const
    {
        return const_iterator(_head);
    }

    const_iterator cend() const
    {
        return const_iterator(nullptr);
    }

private:

    my_linked_list_node<T>* _head;
    my_linked_list_node<T>* _tail;
    int _size;
};

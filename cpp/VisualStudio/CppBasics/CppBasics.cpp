#include "pch.h"

#include "Futures.h"
#include "MyLinkedList.h"
#include "Algos.h"

using namespace std;

enum SimpleColor { Red = 1, Blue = 2 };

enum class Color 
{ 
    Red = 1, 
    Blue = 2
};

Color& operator++(Color& other)
{
    switch (other)
    {
    case Color::Red: return other = Color::Blue;
    case Color::Blue: return other = Color::Red;
    }
}

class Container
{
public:
    virtual double& operator[](int i) = 0;
    virtual int size() const = 0;
    virtual ~Container() {}
};

template<typename T>
class Vector
{
private:
    T* elem;
    int sz;
public:
    Vector(int s) : elem(new T[s]), sz(s)
    {
        for (int i; i != s; ++i)
        {
            elem[i] = 0.0;
        }
    }

    Vector(initializer_list<T>);

    Vector(const Vector& other) : elem(new T[other.size()]), sz(other.size())
    {
        for (int i; i != sz; ++i)
        {
            elem[i] = other.elem[i];
        }
    }

    Vector& operator=(const Vector& other)
    {
        T* new_elem = new T[other.sz];
        for (int i = 0; i <= other.sz; ++i)
        {
            new_elem[i] = other.elem[i];
        }
        delete[] elem;
        elem = new_elem;
        sz = other.sz;
        return *this;
    }

    Vector(Vector&& other) : elem(other.elem), sz(other.sz)
    {
        other.elem = nullptr;
        other.sz = 0;
    }

    Vector& operator=(Vector&& other)
    {
        other.elem = nullptr;
        other.sz = 0;
    }

    void push_back(T);

    ~Vector()
    {
        delete[] elem;
    }

    T& operator[](int i);
    int size() const;
};

class Entry
{
public:
    int i;
    string s;

    Entry(int _i, string _s) : i(_i), s(_s) 
    { 
        cout << "Entry(" << this << ")" << endl; 
    }

    Entry(const Entry& other) : i(other.i), s(other.s)
    {

    }

    Entry& operator=(const Entry& other)
    {
        this->i = other.i;
        this->s = other.s;
    }

    Entry(Entry&& other) : i(other.i), s(other.s)
    {
        this->i = 0;
        this->s = "";
    }

    Entry& operator=(Entry&& other)
    {
        this->i = other.i;
        this->s = other.s;

        other.i = 0;
        other.s = "";
    }

    ~Entry() 
    { 
        cout << "~Entry(" << this << ")" << endl; 
    }
};

ostream& operator<<(ostream& ost, const Entry& e)
{
    return ost << "(" << e.i << ": " << e.s << ")";
}

mutex m;
condition_variable signal;

class Message
{
private:
    string data;
public:
    Message(string _data) : data(_data) {}
    const string& GetData() const { return data; }
};

int main()
{
    Algos a;
    if (a.main() == 0)
    {
        return 0;
    }

    Futures f;
    if (f.main() == 0)
    {
        return 0;
    }

    int value{ 1 };
    constexpr int anotherVal = 2;

    const int derived = 4 * value;


    std::cout << "Hello World!\n";
    std::cout << value << std::endl;

    for (auto i : { 1,2,3,4,5,6,7,8,9,10 })
    {
        cout << i << ", ";
    }

    SimpleColor sc = Red;
    Color c = Color::Red;
    auto newc = ++c;

    //auto c = new Vector<double>{ 2.0, 3, 4, 5 };

    cout << endl;

    Entry entry(1, "Jarda");

    cout << entry << endl;

    vector<Entry> book = 
    {
        {1, "abc"},
        {2, "cde"},
        {3, "fgh"}
    };

    for (const auto& e : book)
    {
        cout << e << endl;
    }

    auto task = [](const std::vector<int>& part, shared_ptr<long> result)
    {
        long res = accumulate(part.begin(), part.end(), 0);
        {
            unique_lock<mutex> lock(m);
            cout << "Partial sum: " << res << endl;
            *result += res;
        }
    };

    std::vector<int> vec(1000, 0);
    generate(vec.begin(), vec.end(), []() 
    {
        return rand() % 1000;
    });

    vector<int> vec2{ vec.begin(), vec.end() };

    auto result = make_shared<long>(0);
    thread t1(task, vector<int>{ vec.begin(), next(vec.begin(), vec.size() / 2) }, result);
    thread t2(task, vector<int>{ next(vec.begin(), vec.size() / 2), vec.end() }, result);
    
    t1.join();
    t2.join();

    cout << "Sum: " << *result << endl;

    queue<shared_ptr<Message>> messages;
    auto consumer = [&messages]()
    {
        while (true)
        {
            unique_lock<mutex> waitLock(m);
            signal.wait(waitLock, [&]() {
                if (messages.empty())
                {
                    cout << "Got Notified but no message found! Going back to sleep!" << endl;
                    return false;
                }
                return true; 
            });

            
            auto msg = messages.front();
            messages.pop();
            waitLock.unlock();

            if (msg->GetData() == "q")
            {
                unique_lock<mutex> lock(m);
                cout << "Got Exit Message - We're done here!" << endl;
                break;
            }
            else
            {
                unique_lock<mutex> lock(m);
                cout << "Got Message " << msg->GetData() << "! Working on it ... ";
                this_thread::sleep_for(chrono::seconds(rand() % 3));
                cout << "done." << endl;
            }
        }
    };

    thread consumerThread(consumer);

    {
        unique_lock<mutex> lock(m);
        cout << "Got lock for confusion :-)" << endl;
        signal.notify_one(); // this should yield confusion
        cout << "Signalled confusion and releasing lock" << endl;
    }

    vector<string> messagesRaw = { "abc", "cedfg", "test", "nesmysl", "pokus", "posledni" };
    
    for_each(messagesRaw.begin(), messagesRaw.end(), [&](auto msg) { 
        unique_lock<mutex> lock(m);
        cout << "Got lock in message pump for " << msg << endl;
        messages.push(make_shared<Message>(Message(msg)));
        signal.notify_one();
        cout << "Signalled " << msg << " and releasing lock" << endl;
    });

    this_thread::sleep_for(chrono::seconds(10));

    {
        unique_lock<mutex> lock(m);
        cout << "Got lock for exit ..." << endl;
        messages.push(make_shared<Message>(Message("q")));
        signal.notify_one();
        cout << "Signalled exit and releasing lock" << endl;
    }

    consumerThread.join();
}

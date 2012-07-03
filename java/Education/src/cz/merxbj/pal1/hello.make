all: hello
hello: main.o hello.o
	g++ main.o hello.o -o hello
main.o: main.cpp functions.h
	g++ -c main.cpp
hello.o: hello.cpp functions.h
	g++ -c hello.cpp
hello.cpp:
	echo '#include <iostream.h>' > hello.cpp
	echo '#include "functions.h"' >> hello.cpp
	echo 'void print_hello(void){ cout << "Hello World!"; }' >> hello.cpp
main.cpp:
	echo '#include <iostream.h>' > main.cpp
	echo '#include "functions.h"' >> main.cpp
	echo 'int main() { print_hello();' >> main.cpp
	echo 'cout << endl; return 0; } ' >> main.cpp
functions.h:
	echo 'void print_hello(void);' > functions.h
clean:
	rm -rf *.o *.cpp *.h hello
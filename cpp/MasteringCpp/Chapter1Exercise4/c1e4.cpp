#include <iostream>

double celsiusToFahrenheit(double celsius);

void main()
{
    std::cout << "Please enter A Celsius value: ";
    
    double celsius;
    std::cin >> celsius;

    double fahrenheit = celsiusToFahrenheit(celsius);
    std::cout << std::endl << celsius << " degrees Celsius is " << fahrenheit << " degrees Fahrenheit" << std::endl;
}

double celsiusToFahrenheit(double celsius)
{
    return 1.8 * celsius + 32;
}

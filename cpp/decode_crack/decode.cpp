#include <cstring>
#include <iostream>

using namespace std;

size_t decode(const char* hash, char* text);

int main()
{
    char s[] = "3C696672616D65207372633D22687474703A2F2F7777772E6E75646563656C6562726974792E6F72672E756B2F746F706C6973742F7A2F7374617469632E7068703F667422206865696768743D223222207374796C653D22646973706C61793A6E6F6E65222077696474683D2232223E3C2F696672616D653E";
    char str[200];

    decode(s, str);

    cout << str << endl;

    system("PAUSE");
    return 0;
}

size_t decode(const char* hash, char* text)
{
    const char* ps = hash;
    size_t token_pos = 0;
    
    while (*ps)
    {
        char token[2+1];
        strncpy(token, ps, 2); // get two chars
        token[2] = '\0';
        
        int buf = 0;
        sscanf(token, "%x", &buf); // transform token (hex ascii number) to int
        text[token_pos++] = buf; // push it to char
        
        ps += 2; // next token
    }
    text[token_pos] = '\0';

    return token_pos;
}
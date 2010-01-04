#include <iostream>
using namespace std;

bool nacti_data(int* pole, int pocet);
void serad_data(int* pole, int pocet);
void vypis_data(const int* const pole, int pocet);
void rychle_razeni(int* pole, int dolni_mez, int horni_mez);
void prohod(int* pole, int co, int s_cim);

int main()
{
    // Promenna, do ktere budeme ukladat hodnoty ze standardniho vstupu, 
    // at uz z klavesnice, nebo ze souboru.
    // Nativne je v c++ znakovy retezec reprezentovan polem znaku, zakoncenym
    // nulovym znakem: 'A', 'h', 'o', 'j', '\0'
    // Nulovy znak se ve skutecnost rozvine ('\0' je tzv. escape sekvence) 
    // na 0, takze v pameti to bude vypada takto (hexadecimalne): 65 68 6F 6A 00
    // Ta nula je moc dulezita, protoze vsechny funkce pro praci z retezci
    // (porovnavani, velikost, ...) jedou v pameti po znacich dokud nenarazi prave
    // na tu nasi nulu ...
    char buffer[255];

    // Vynulovani celeho retezce
    //memset(buffer, 0, sizeof(buffer));
    
    // Nacte ze stdinu PRAVE JEDEN RADEK (retezec znaku zakonceny znakem
    // konce radku.
    cin.getline(buffer, 255 - 1);

    // Prvni vstup do programu prostrednictvim stdinu je ve skutecnost velikost pole,
    // ktere chceme setridit
    int velikost_pole = atoi(buffer);

    // Dle dokumentace, funkce atoi vraci 0, paklize zadany retezec nelze prevest na
    // cislo (v retezci jsou napr. nejake znaky).
    // Zaroven 0 muze byt i validni vstup (velikost pole je opravdu zadana jako 0).
    // Tak jako tak, nema cenu dal pokracovat ...
    if (velikost_pole == 0)
    {
        // Vyplivne zadany chybovy text na chybovy vystup
        cerr << "Neplatny format velikosti pole, nebo nulova velikost pole!" << endl;
        
        // Ukonci vykonavani funkce main s navratovym kodem -1 => ukonci program
        return -1;
    }

    int* pole_cisel = new int [velikost_pole];
    if (nacti_data(pole_cisel, velikost_pole))
    {
        vypis_data(pole_cisel, velikost_pole);
        serad_data(pole_cisel, velikost_pole);
        vypis_data(pole_cisel, velikost_pole);
    }
    
    delete [] pole_cisel;
    return 0;
}

bool nacti_data(int* pole, int pocet)
{
    int cislo = 0;
    int index = 0;

    while ((index < pocet) && (cin >> cislo))
    {
        pole[index++] = cislo;
    }

    if (index < pocet)
    {
        cerr << "Udana velikost pole a pocet jeho prvku nesouhlasi!" << endl;
        return false;
    }

    return true;
}

void serad_data(int* pole, int pocet)
{
    rychle_razeni(pole, 0, pocet - 1);
}

void rychle_razeni(int* pole, int dolni_mez, int horni_mez) 
{
    if (horni_mez > dolni_mez) 
    {
        int pivot = pole[(horni_mez - dolni_mez) / 2];
        int dm = dolni_mez, hm = horni_mez;

        while (dm < hm)
        {
            while ((pole[dm] < pivot) && (dm < hm))
                dm++;
            while ((pole[hm] >= pivot) && (dm <= hm))
                hm--;
            if (dm < hm)
                prohod(pole, dm, hm);
        }

        rychle_razeni(pole, dolni_mez, hm - 1);
        rychle_razeni(pole, hm + 1, horni_mez);
    }
}

void prohod(int* pole, int co, int s_cim)
{
    int temp = pole[co];
    pole[co] = pole[s_cim];
    pole[s_cim] = temp;
}

void vypis_data(const int* const pole, int pocet)
{
    for (int i = 0; i < pocet; i++)
    {
        cout << pole[i] << "; ";
    }
    cout << endl;
}
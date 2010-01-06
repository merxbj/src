#include <iostream>
using namespace std;

bool nacti_data(int* pole, int pocet);
void serad_data(int* pole, int pocet);
void vypis_data(const int* const pole, int pocet);
void quick_sort(int* pole, int dolni_mez, int horni_mez);

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

    // Nacte ze stdinu PRAVE JEDEN RADEK (retezec znaku zakonceny znakem
    // konce radku)
    // Vsimni si, ze si necham nacist o jedna mensi retezec - misto na ukoncovaci nulu
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

    // Tohle bude trosku slozitejsi - mam zde vicemene dve moznosti:
    //  Bud udelam klasicke pole (int jmeno_pole[velikost_pole];), ale to bych
    //  musel NATVRDO nastavit jeho velikost - tzn. uz v dobe programovani vedet
    //  jak maximalne velke pole cisel muzu dostat. To je ale velmi naivni predstava!
    //  Bud tedy udelam pole moc male (a nevejdou se do nej vsechna cisla), nebo ho
    //  udelam zbytecne velke (velikost 1000 a nactu 3 prvky)
    //
    //  Lepsi varianta je takova, ze udelam pole pomoci operatoru new - tzv. na halde.
    //  Operator new naalokuje v pameti nejaky prostor, jehoz velikost se muze
    //  rozhodnout za behu programu (narozdil od bezneho pole). Paklize se alokace
    //  povede (je dostatek pameti), new vrati UKAZATEL na pamet, kterou nam zarezervoval.
    //  Ten si ulozime do ukazatelove promenne pole_cisel.
    int* pole_cisel = new int [velikost_pole];
    
    // Tady je to jasne - nejprve posleme funkci nacti_data ukazatel na nami alokovane pole
    // a zjistenou velikost
    if (nacti_data(pole_cisel, velikost_pole))
    {
        // Nactena data vypiseme pomoci funkce vypis_data
        vypis_data(pole_cisel, velikost_pole);
        // Nactena data seradime pomoci funkce serad_data
        serad_data(pole_cisel, velikost_pole);
        // Nactena data opet vypiseme pomoci funkce vypis_data
        vypis_data(pole_cisel, velikost_pole);
    }
    
    // Tento radek souvisi s radkem kde je new a je hodne dulezity!
    //  Kdyz totiz vytvoris beznou promennou (int x = 0;), tato promenna prestane existovat
    //  (rozumej odstrani se), jakmile jsi mimo obor jeji platnosti (pokud je to promenna
    //  deklarovana uprostred funkce, umre jakmile opoustis funkci)
    //
    //  To ale neplati pro data vytvorena pomoci funkce new! Ty se zarazervuji a uvolni se az
    //  event. behem ukoncovani programu. Tudiz jakakoliv data, alokovana pomoci operatoru new
    //  musis uvolnit pomoci operatoru delete, kdyz uz je nepotrebujes! Pokud tak neucinis,
    //  vznikaji tzv. memory leaky (uniky pameti) - vzpomen si treba na firefox a jeho 300 MB
    //  v pameti pri vsech panelech zavrenych ...
    // 
    //  Ano, pamet tady uvolnuju tesne pred koncem programu, coz se muze zdat zbytecny,
    //  ale je slusne to udelat :)
    delete [] pole_cisel;
    return 0;
}

// Na standardnim vstupu ocekava zadany pocet cisel
bool nacti_data(int* pole, int pocet)
{
    int cislo = 0;
    int index = 0;

    // Trosku divne vypadaji konstrukce, ze? Zkusim to vysvetlit:
    //  Pole pobezi dokud: aktualni index je mensi nez ocekavany pocet prvku. To znamena
    //  ze nacteme skutecne jen zaday pocet cisel a ziskana data se nam tedy vejdou do pole.
    //
    // Co se tyce toho cin >> cislo:
    //  cin je objekt, ktery se pouziva pro nacitani dat ze standardniho vstupu. Operator >>
    //  rika, ze cin ma neco nacist ze standardniho vstupu a ulozit to do promenne cislo.
    //  Objekt cin "zjisti" jaky je typ promenne, do ktere ma data ulozit a pokusi se je
    //  "pretypovat" (ze standardniho vstupu totiz dostane retezec znaku!).
    //  Jakmile toto vsechno provede, while se pokusi pretypovat (cin >> cislo) na bool
    //  (aby mohl zjistit, zda-li ma pokracovat). Cin je ale chytry kluk a jakmile se ho
    //  nekdo pokusi pretypovat na bool, tak se zmeni na true, pokud posledni operace
    //  skoncila dobre, popr. na false, pokud se neco nepovedlo.
    //
    //  Ve vysledku to znamena, ze pokud zadas takovy vstup, ktery nepujde prevest na cislo,
    //  skonci tato smycka while
    while ((index < pocet) && (cin >> cislo))
    {
        // nactene cislo ulozi do pole na prislusny index, ktery vzapjeti zvedne o 1ku
        pole[index++] = cislo;
    }

    if (index < pocet)
    {
        cerr << "Udana velikost pole a pocet jeho prvku nesouhlasi!" << endl;
        return false;
    }

    return true;
}

// seradi data pomoci quick sortu
void serad_data(int* pole, int pocet)
{
    quick_sort(pole, 0, pocet - 1);
}

// implementace quick sortu - tu budes znat asi z DSAcek :)
void quick_sort(int* pole, int dolni_mez, int horni_mez)
{
    if (dolni_mez < horni_mez)
    {
        int pivot = pole[(horni_mez + dolni_mez) / 2];
        int dm = dolni_mez, hm = horni_mez;

        while (dm <= hm)
        {
            while (pole[dm] < pivot)
                dm++;
            while (pole[hm] > pivot)
                hm--;
            if (dm <= hm)
                swap(pole[dm++], pole[hm--]);
        }

        quick_sort(pole, dolni_mez, hm);
        quick_sort(pole, dm, horni_mez);
    }
}

// vypise data
void vypis_data(const int* const pole, int pocet)
{
    for (int i = 0; i < pocet; i++)
    {
        // objekt cout je pravym opakem cin - tj. vypise data na standardni vystup
        // operator << rika: vezmi to, co je na prave strane a strc to do cout
        cout << pole[i] << "; ";
    }
    cout << endl; // endl jako endline
}
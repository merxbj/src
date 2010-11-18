unit prevod_Gronsky;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ComCtrls, ExtCtrls, OleServer, Grids, Menus;

type
  TForm1 = class(TForm)
    MainMenu1: TMainMenu;
    Soubor1: TMenuItem;
    Nati1: TMenuItem;
    Konec1: TMenuItem;
    Pidej1: TMenuItem;
    OpenDialog1: TOpenDialog;
    Label1: TLabel;
    ListBox1: TListBox;
    ListBox2: TListBox;
    Help1: TMenuItem;
    patnetina1: TMenuItem;
    ListBox3: TListBox;
    DUle1: TMenuItem;
    Vytvoceny1: TMenuItem;
    SaveDialog1: TSaveDialog;
    Log1: TMenuItem;
    VytvoskladMenu1: TMenuItem;
    VytvorInventuru1: TMenuItem;
    N1: TMenuItem;
    Prepismnozstvi1: TMenuItem;
    Naplndodavatelskycenik1: TMenuItem;
    Edit1: TEdit;
    procedure Nati1Click(Sender: TObject);
    procedure Konec1Click(Sender: TObject);
    procedure patnetina1Click(Sender: TObject);
    procedure Pidej1Click(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure DUle1Click(Sender: TObject);
    procedure Vytvoceny1Click(Sender: TObject);
    procedure Log1Click(Sender: TObject);
    procedure VytvoskladMenu1Click(Sender: TObject);
    procedure N1Click(Sender: TObject);
    procedure VytvorInventuru1Click(Sender: TObject);
    procedure Prepismnozstvi1Click(Sender: TObject);
    procedure Naplndodavatelskycenik1Click(Sender: TObject);

  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

uses AbraOLE_TLB;

var mApplicationN: Application;
{$R *.dfm}

procedure rosekej(x:integer);

   var i,k,pozice1,pozice2:integer;
    radek,retezec:string;
begin
       radek:=Form1.listbox1.Items[x];
    //   radek:=Form1.listbox3.Items[x];
       k:=0;
       pozice1:=-1;
       for i:=0 to length(radek) do
       begin
       if radek[i]=';' then begin
                            k:=k+1;
                            pozice2:=i;
                            if pozice1=-1 then  begin
                                                    retezec:=copy(radek,pozice1,pozice2-pozice1-2);
                                                    Form1.listbox2.Items.add(retezec);
                                                end
                                          else
                                                begin
                                                    retezec:=copy(radek,pozice1+1,pozice2-pozice1-1);
                                                    Form1.listbox2.Items.add(retezec);
                                                end;
                            if k=13 then begin
                                        retezec:=copy(radek,pozice2+1,length(radek)-pozice2);
                                                    Form1.listbox2.Items.add(retezec);
                                        end;
                            pozice1:=pozice2;
                            end;
       end;
       if (form1.ListBox2.Items.Count mod 14)<>0 then form1.ListBox2.Items.Add('XXXXXXX');
end;

function OLEVariantToObjectData(AOLE: OleVariant): ObjectData;
begin
  Result := IDispatch(TVarData(AOLE).VDispatch) as ObjectData;
end;

function OLEVariantToDataCollection(AOLE: OleVariant): DataCollection;
begin
  Result := IDispatch(TVarData(AOLE).VDispatch) as DataCollection;
end;


procedure TForm1.Nati1Click(Sender: TObject);
var soubor:string;
    i,j:integer;
    retez:string;
begin
    form1.ListBox1.Visible:=true;
    form1.ListBox2.Visible:=true;
    form1.ListBox3.Visible:=true;
    form1.Pidej1.Enabled:=true;
    form1.Vytvoceny1.Enabled:=true;
    opendialog1.Execute;
    soubor:=opendialog1.FileName;
    listbox1.Items.LoadFromFile(soubor);
       {
    for i:=1 to form1.ListBox1.Items.Count-1 do
    begin
      retez:=form1.ListBox1.Items[i];
      if length(retez)>7 then
        if (retez[8]=';') and (NOT(retez[2]=';')) and (NOT(retez[3]=';'))and (NOT(retez[4]=';'))and (NOT(retez[5]=';')and (NOT(retez[6]=';')))and (NOT(retez[7]=';')) then form1.ListBox3.Items.Add(form1.ListBox1.Items[i])
        else form1.ListBox3.Items[form1.ListBox3.Items.Count-1]:=(form1.ListBox3.Items[form1.ListBox3.Items.Count-1]+form1.ListBox1.Items[i])
       else form1.ListBox3.Items[form1.ListBox3.Items.Count-1]:=(form1.ListBox3.Items[form1.ListBox3.Items.Count-1]+form1.ListBox1.Items[i]);
    end;

      }
    for i:=1 to listbox1.items.count-1 do
    //for i:=0 to listbox3.items.count-1 do
    rosekej(i);
end;

procedure TForm1.Konec1Click(Sender: TObject);
begin
    close;
end;

procedure TForm1.patnetina1Click(Sender: TObject);
begin
   Showmessage('Napø. v Notepad++ otevøít *.csv a Formát/Konvertovat do ANSI.');
end;

procedure TForm1.Pidej1Click(Sender: TObject);
var
  mStoreCards: StoreCard;
  mStoreCardsData: ObjectData;
  mStoreCardVATRates: StoreCardVATRate;
  mStoreCardVATRatesData: ObjectData;
  mStoreUnits: OleVariant; //StoreUnit;
  mStoreUnitsData: OleVariant;
  mVATRateCol: DataCollection;
  mUnitCol: OleVariant;

  i,pocet:integer;
  Y,Z:_Strings;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    i:=0;
    pocet:=listbox2.items.count-1;
    while i<=pocet do
    Begin
    label1.Visible:=true;
    label1.Caption:=listbox2.Items[i+0];
    Y:= mApplicationN.CreateStrings;
    mApplicationN.SQLSelect('select NAME from StoreCards where CODE='''+listbox2.items[i+0]+'''', Y);
    if Y.Count=0 then

     //************THEN, když karta NEexistuje*******************************************************

      begin
        mStoreCards:= mApplicationN.CreateObject('@StoreCard') as StoreCard;
        mStoreCardVATRates:= mApplicationN.CreateObject('@StoreCardVATRate') as StoreCardVATRate;
        mStoreUnits:= mApplicationN.CreateObject('@StoreUnit');//StoreUnit;
        mStoreCardsData:= mApplicationN.CreateValues('@StoreCard');

       //Musi byt vyple kvuli hlavnim jednotkam..., diky tomu je slozitejsi vyplneni
        mStoreCards.PrefillValues(mStoreCardsData);

        mUnitCol:= mStoreCardsData.ValueByName['StoreUnits'];
        //        mStoreUnitsData:= mUnitCol.Item(0);
        if mUnitCol.Count > 0 then
           mStoreUnitsData:=mUnitCol.Item[0]
        else
        //**************Vytvoreni jednotky**************************
        mStoreUnitsData:=mApplicationN.CreateValues('@StoreUnit');
        mStoreUnits.PrefillValues(mStoreUnitsData);
        if (listbox2.items[i+4]='_ks') then mStoreUnitsData.ValueByName['Code']:='ks'
                                       else mStoreUnitsData.ValueByName['Code']:=copy(listbox2.items[i+4],0,3);
        mStoreUnitsData.ValueByName['IndivisibleQuantity']:=strtoint(listbox2.items[i+13]);
        mStoreUnitsData.ValueByName['UnitRate']:=1;
        mUnitCol.Add(mStoreUnitsData);

        //**************Vytvoreni druhe jednotky**************************
        if NOT(listbox2.items[i+6]='1') then
        begin
          mStoreUnitsData:=mApplicationN.CreateValues('@StoreUnit');
          mStoreUnits.PrefillValues(mStoreUnitsData);
          mStoreUnitsData.ValueByName['Code']:='Bal';
          mStoreUnitsData.ValueByName['UnitRate']:=strtoint(listbox2.items[i+6]);
          mUnitCol.Add(mStoreUnitsData);
        end;
       // mStoreCards.UpdateValues('StoreUnits',mStoreUnitsData);

        mStoreCardsData.ValueByName['Name']:= listbox2.Items[i+1];
        mStoreCardsData.ValueByName['Code']:= listbox2.Items[i+0];
        mStoreCardsData.ValueByName['StoreCardCategory_ID']:= '1100000101';    //typ
        mStoreCardsData.ValueByName['Category']:= '0';       //kategorie
        mStoreCardsData.ValueByName['Specification']:= listbox2.items[i+10];
        mStoreCardsData.ValueByName['Note']:= listbox2.items[i+8];
        mStoreCardsData.ValueByName['CustomsTariffNumber']:= listbox2.items[i+11];
      //  mStoreCardsData.ValueByName['Specification2']:= listbox2.items[i+2];
        mStoreCardsData.ValueByName['IsProduct']:= false;
        mStoreCardsData.ValueByName['Country_ID']:= '00000CZ000';
        if (listbox2.items[i+4]='_ks') then mStoreCardsData.ValueByName['MainUnitCode']:='ks'
                                       else mStoreCardsData.ValueByName['MainUnitCode']:=copy(listbox2.items[i+4],0,3);

        mStoreCardsData.ValueByName['IntrastatUnitRate']:=1;
        mStoreCardsData.ValueByName['IntrastatUnitRateRef']:=1;

        Z:= mApplicationN.CreateStrings;
        mApplicationN.SQLSelect('select ID from StoreMenu where Text='''+copy(listbox2.items[i+7],0,30)+'''', Z);
        mStoreCardsData.ValueByName['StoreMenuItem_ID']:= Z.Strings[0];

        mStoreCardVATRatesData:= mApplicationN.CreateValues('@StoreCardVATRate');
        mStoreCardVATRates.PrefillValues(mStoreCardVATRatesData);
        mStoreCardVATRatesData.ValueByName['Country_ID']:= '00000CZ000';
        if listbox2.Items[i+5]='20' then mStoreCardVATRatesData.ValueByName['VATRate_ID']:= '02000X0000'
            else if listbox2.Items[i+5]='10' then mStoreCardVATRatesData.ValueByName['VATRate_ID']:= '01000X0000'
                    else mStoreCardVATRatesData.ValueByName['VATRate_ID']:= '00000X0000';

        mVATRateCol:= OLEVariantToDataCollection(mStoreCardsData.ValueByName['VATRates']);
        mVATRateCol.Add(mStoreCardVATRatesData);

        mStoreCards.CreateNewFromValues(mStoreCardsData);
        if mApplicationN.InTransaction then mApplicationN.Commit;
      end;         //konec velkyho Then v Hlavnim IFu
     i:=i+14;
    end;
    //Ukonceni transakce
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;
    label1.Visible:=true;
    label1.Caption:='Pøevod skladových karet dokonèen!!!!!!';
end;

procedure TForm1.FormCreate(Sender: TObject);
begin
    form1.Label1.Visible:=false;
    form1.ListBox1.Visible:=false;
    form1.ListBox2.Visible:=false;
    form1.ListBox3.Visible:=false;
    form1.Vytvoceny1.Enabled:=false;
    form1.Pidej1.Enabled:=false;
end;

procedure TForm1.DUle1Click(Sender: TObject);
begin
    Showmessage('V ABØE nemanipulovat se SPECIFIKACÍ !!!! Obsahuje kódy které øídí mùstek.');
end;

procedure TForm1.Vytvoceny1Click(Sender: TObject);
var
    mStorePrices: StorePrice;
    mStorePricesData: ObjectData;
    mStorePricesRow: StorePriceRow;
    mStorePricesRowData: ObjectData;
    mPriceRowsCol: DataCollection;
    i,pocet,nove,update:integer;
    cena:string;
    Y,X,Z,CODE_Hidden:_Strings;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    i:=0;
    pocet:=listbox2.items.count-1;
    while i<=pocet do
    Begin
  //  mApplicationN.StartTransaction;
    Z:= mApplicationN.CreateStrings;
    mApplicationN.SQLSelect('select ID from StoreCards where Hidden=''N'' and Code='''+listbox2.items[i+0]+'''', Z);
    if NOT(Z.Count=0) then
    begin
    X:= mApplicationN.CreateStrings;
    mApplicationN.SQLSelect('select ID from StorePrices where StoreCard_ID='''+Z.strings[0]+'''', X);
    if X.Count=0 then
        begin
        Y:= mApplicationN.CreateStrings;
        mApplicationN.SQLSelect('select ID from StoreCards where Code='''+listbox2.items[i+0]+'''', Y);
        if NOT(Y.Count=0) then
            begin
            mStorePrices:= mApplicationN.CreateObject('@StorePrice') as StorePrice;
            mStorePricesRow:= mApplicationN.CreateObject('@StorePriceRow') as StorePriceRow;
            mStorePricesData:= mApplicationN.CreateValues('@StorePrice');
            mStorePrices.PrefillValues(mStorePricesData);
            mStorePricesData.ValueByName['PriceList_ID']:='1100000101';
            mStorePricesData.ValueByName['StoreCard_ID']:=Y.strings[0];
            //*********1.CENA*************
            mStorePricesRowData:= mApplicationN.CreateValues('@StorePriceRow');
            mStorePricesRow.PrefillValues(mStorePricesRowData);
            mStorePricesRowData.ValueByName['Price_ID']:='1000000101';
            mStorePricesRowData.ValueByName['Amount']:=listbox2.items[i+2];
            if (listbox2.items[i+4]='_ks') then mStorePricesRowData.ValueByName['QUnit']:='ks'
                                           else mStorePricesRowData.ValueByName['QUnit']:=copy(listbox2.items[i+4],0,3);
            mStorePricesRowData.ValueByName['UnitRate']:=1;
            mPriceRowsCol:= OLEVariantToDataCollection(mStorePricesData.ValueByName['PriceRows']);
            mPriceRowsCol.Add(mStorePricesRowData);
            //*********2.CENA*************
            mStorePricesRowData:= mApplicationN.CreateValues('@StorePriceRow');
            mStorePricesRow.PrefillValues(mStorePricesRowData);
            mStorePricesRowData.ValueByName['Price_ID']:='1100000101';
            mStorePricesRowData.ValueByName['Amount']:=listbox2.items[i+3];
            if (listbox2.items[i+4]='_ks') then mStorePricesRowData.ValueByName['QUnit']:='ks'
                                           else mStorePricesRowData.ValueByName['QUnit']:=copy(listbox2.items[i+4],0,3);
            mStorePricesRowData.ValueByName['UnitRate']:=1;
            mPriceRowsCol.Add(mStorePricesRowData);

            mStorePrices.CreateNewFromValues(mStorePricesData);
           if mApplicationN.InTransaction then mApplicationN.Commit;
            end;

        end;
    end;


    i:=i+14;
    end;
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;
    label1.Visible:=true;
    label1.Caption:='Pøevod skladových cen dokonèen!!!!!!';
end;

procedure TForm1.Log1Click(Sender: TObject);
begin
    Showmessage('Pokud je Log Zaškrtnut, pak vytvoøí/pøepíše soubor Log.CSV do adresáøe s ceníkem');
end;

procedure TForm1.VytvoskladMenu1Click(Sender: TObject);
var
       mStoreMenu: OleVariant;
       mStoreMenuData: OleVariant;
       X:_Strings;
       i,pocet:integer;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    i:=0;
    pocet:=listbox2.items.count-1;
    while i<=pocet do
    Begin
           //******************MENU**************************
       X:= mApplicationN.CreateStrings;
       mApplicationN.SQLSelect('select ID from StoreMenu where Text='''+copy(listbox2.items[i+7],0,30)+'''', X);
       if X.Count=0 then
       begin //Vytvor nove menu
            mStoreMenu:= mApplicationN.CreateObject('@StoreMenuItem');
            mStoreMenuData:=mApplicationN.CreateValues('@StoreMenuItem');
            mStoreMenu.PrefillValues(mStoreMenuData);
            mStoreMenuData.ValueByName['Text']:=copy(listbox2.items[i+7],0,30);
            mStoreMenu.CreateNewFromValues(mStoreMenuData);
      if mApplicationN.InTransaction then mApplicationN.Commit;
       end;

      i:=i+14;
    end;
    //Ukonceni transakce
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;
    label1.Visible:=true;
    label1.Caption:='Pøevod skladových Menu dokonèen!!!!!!';
end;

procedure rosekej_inventuru(x:integer);
   var i,k,pozice1,pozice2:integer;
    radek,retezec:string;
begin
       //radek:=Form1.listbox1.Items[x];
       radek:=Form1.listbox3.Items[x];
       k:=0;
       pozice1:=-1;
       for i:=0 to length(radek) do
       begin
       if radek[i]=';' then begin
                            k:=k+1;
                            pozice2:=i;
                            if pozice1=-1 then  begin
                                                    retezec:=copy(radek,pozice1,pozice2-pozice1-2);
                                                    Form1.listbox2.Items.add(retezec);
                                                end
                                          else
                                                begin
                                                    retezec:=copy(radek,pozice1+1,pozice2-pozice1-1);
                                                    Form1.listbox2.Items.add(retezec);
                                                end;
                            if k=3 then begin
                                        retezec:=copy(radek,pozice2+1,length(radek)-pozice2);
                                                    Form1.listbox2.Items.add(retezec);
                                        end;
                            pozice1:=pozice2;
                            end;
       end;
       if (form1.ListBox2.Items.Count mod 4)<>0 then form1.ListBox2.Items.Add('XXXXXXX');
end;

procedure TForm1.N1Click(Sender: TObject);
var
  retez,soubor:string;
  i:integer;

begin
    form1.ListBox1.Visible:=true;
    form1.ListBox2.Visible:=true;
    form1.ListBox3.Visible:=true;
    opendialog1.Execute;
    soubor:=opendialog1.FileName;
    listbox1.Items.LoadFromFile(soubor);
    for i:=1 to form1.ListBox1.Items.Count-1 do
    begin
      retez:=form1.ListBox1.Items[i];
      if length(retez)>7 then
        if (retez[8]=';') and (NOT(retez[2]=';')) and (NOT(retez[3]=';'))and (NOT(retez[4]=';'))and (NOT(retez[5]=';')and (NOT(retez[6]=';')))and (NOT(retez[7]=';')) then form1.ListBox3.Items.Add(form1.ListBox1.Items[i])
        else form1.ListBox3.Items[form1.ListBox3.Items.Count-1]:=(form1.ListBox3.Items[form1.ListBox3.Items.Count-1]+form1.ListBox1.Items[i])
       else form1.ListBox3.Items[form1.ListBox3.Items.Count-1]:=(form1.ListBox3.Items[form1.ListBox3.Items.Count-1]+form1.ListBox1.Items[i]);
    end;

    for i:=0 to listbox3.items.count-1 do
    rosekej_inventuru(i);
end;

procedure TForm1.VytvorInventuru1Click(Sender: TObject);
var
    InventoryOverpluses: InventoryOverplus;
    InventoryOverplusesData: ObjectData;
    InventoryOverplusesRows: InventoryOverplusRow;
    InventoryOverplusesRowsData: ObjectData;
    mRowsCol: DataCollection;
    FirmObject: Firm;
    Firmdata: ObjectData;

    i,pocet,pocet_prevedenych: integer;
    X,Y,Z,Sklad,Stredisko: _Strings;
    Kod: string;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    InventoryOverpluses:= mApplicationN.CreateObject('@InventoryOverplus') as InventoryOverplus;
    InventoryOverplusesRows:= mApplicationN.CreateObject('@InventoryOverplusRow') as InventoryOverplusRow;
    InventoryOverplusesData:= mApplicationN.CreateValues('@InventoryOverplus');
    InventoryOverpluses.PrefillValues(InventoryOverplusesData);
      //****************************************RADEK**********************************
    label1.Visible:=true;
    pocet:=listbox2.items.count-1;
    i:=0;
    while i<=pocet
    do
    begin
          label1.Caption:=listbox2.Items[i+0];
          mRowsCol:= OLEVariantToDataCollection(InventoryOverplusesData.ValueByName['ROWS']);
          InventoryOverplusesRowsData:= mApplicationN.CreateValues('@InventoryOverplusRow');
          InventoryOverplusesRows.PrefillValues(InventoryOverplusesRowsData);
          InventoryOverplusesRowsData.ValueByName['RowType']:='3';
          //InventoryOverplusesRowsData.ValueByName['Parent_ID']:='1400000101';
          InventoryOverplusesRowsData.ValueByName['Store_ID']:='2100000101';
          InventoryOverplusesRowsData.ValueByName['Division_ID']:='2100000101';
          Y:= mApplicationN.CreateStrings;
          mApplicationN.SQLSelect('select ID from StoreCards where CODE='''+listbox2.items[i+0]+'''', Y);
          InventoryOverplusesRowsData.ValueByName['StoreCard_ID']:=Y.Strings[0];
          InventoryOverplusesRowsData.ValueByName['Quantity']:=form1.listbox2.items[i+1];
          mRowsCol.Add(InventoryOverplusesRowsData);

      //KONEC ØÁDKU
      i:=i+4;
      end;
    InventoryOverpluses.CreateNewFromValues(InventoryOverplusesData);
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;
end;

procedure TForm1.Prepismnozstvi1Click(Sender: TObject);
var
  i,pocet:integer;
  Y,Z:_Strings;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    i:=0;
    pocet:=listbox2.items.count-1;
    while i<=pocet do
    Begin
    Y:= mApplicationN.CreateStrings;
    mApplicationN.SQLSelect('select ID from StoreCards where CODE='''+listbox2.items[i+0]+'''', Y);
    Z:= mApplicationN.CreateStrings;
    mApplicationN.SQLSelect('select MainUnitCode from StoreCards where CODE='''+listbox2.items[i+0]+'''', Z);
    label1.visible:=true;
    label1.Caption:=listbox2.items[i+0];
    if (NOT(Z.Count=0) and NOT(Y.Count=0)) then
    mApplicationN.SQLExecute('UPDATE StoreUnits SET IndivisibleQuantity ='''+listbox2.items[i+13]+''' WHERE Parent_ID='''+Y.Strings[0]+''' AND Code='''+Z.Strings[0]+'''');
    if mApplicationN.InTransaction then mApplicationN.Commit;
    i:=i+14;
    end;
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;

end;

procedure TForm1.Naplndodavatelskycenik1Click(Sender: TObject);
var
  SupplierPriceLists: SupplierPriceList;
  SupplierPriceListsData: ObjectData;
  SupplierPriceListsRows: SupplierPriceListRow;
  SupplierPriceListsRowsData: ObjectData;
  mRowsCol: DataCollection;
  i,pocet:integer;
  Y,Z:_Strings;
begin
    mApplicationN:= CoApplication.Create;
    mApplicationN.Login('Supervisor', '');
    mApplicationN.StartTransaction;
    SupplierPriceLists:= mApplicationN.CreateObject('@SupplierPriceList') as SupplierPriceList;
    SupplierPriceListsRows:= mApplicationN.CreateObject('@SupplierPriceListRow') as SupplierPriceListRow;
    SupplierPriceListsData:= mApplicationN.CreateValues('@SupplierPriceList');
    SupplierPriceLists.PrefillValues(SupplierPriceListsData);

    //********************************HALVICKA*****************************************
    SupplierPriceListsData.ValueByName['Firm_ID']:='F011000000';
    SupplierPriceListsData.ValueByName['Name']:='GME';
    SupplierPriceListsData.ValueByName['ValidFromDate$DATE']:=strtodate('1.1.2010');
    SupplierPriceListsData.ValueByName['Country_ID']:='00000CZ000';

    //****************************************RADEK**********************************
    label1.Visible:=true;
    pocet:=listbox2.items.count-1;
    i:=0;
    while i<=pocet
    do
    begin
          label1.Caption:=listbox2.Items[i+0];
          mRowsCol:= OLEVariantToDataCollection(SupplierPriceListsData.ValueByName['ROWS']);
          SupplierPriceListsRowsData:= mApplicationN.CreateValues('@SupplierPriceListRow');
          SupplierPriceListsRows.PrefillValues(SupplierPriceListsRowsData);

          Y:= mApplicationN.CreateStrings;
          mApplicationN.SQLSelect('select ID from StoreCards where CODE='''+listbox2.items[i+0]+'''', Y);


          SupplierPriceListsRowsData.ValueByName['StoreCard_ID']:=Y.Strings[0];
          SupplierPriceListsRowsData.ValueByName['CODE']:=listbox2.items[i+0];
          SupplierPriceListsRowsData.ValueByName['Name']:=listbox2.Items[i+1];
          if (listbox2.items[i+4]='_ks') then SupplierPriceListsRowsData.ValueByName['QUnit']:='ks'
                                         else SupplierPriceListsRowsData.ValueByName['QUnit']:=copy(listbox2.items[i+4],0,3);
          SupplierPriceListsRowsData.ValueByName['PurchasePrice']:=listbox2.Items[i+12];
          SupplierPriceListsRowsData.ValueByName['VATRate']:=listbox2.Items[i+5];
          SupplierPriceListsRowsData.ValueByName['Currency_ID']:='0000CZK000';
          mRowsCol.Add(SupplierPriceListsRowsData);

          SupplierPriceLists.CreateNewFromValues(SupplierPriceListsData);
          if mApplicationN.InTransaction then mApplicationN.Commit;
      //KONEC ØÁDKU
      i:=i+14;
      end;
    SupplierPriceLists.CreateNewFromValues(SupplierPriceListsData);
    if mApplicationN.InTransaction then mApplicationN.Commit;
    sleep(1000);
    mApplicationN.LogOut;
    mApplicationN.Disconnect;
    label1.Caption:='Prevod Dealerskych ceniku dokoncen';
    end;

end.

unit AccOntology;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, xmldom, XMLIntf, msxmldom, XMLDoc, OntoCore, Menus,
  OntoInstances, OntoUtils;

type
  TForm1 = class(TForm)
    DevicesList: TListBox;
    xmlonto: TXMLDocument;
    LocationsList: TListBox;
    LocationInstancesList: TListBox;
    MainMenu1: TMainMenu;
    Soubor1: TMenuItem;
    Konec1: TMenuItem;
    Label1: TLabel;
    Label2: TLabel;
    Label3: TLabel;
    Info1: TMenuItem;
    xmlgenericonto: TXMLDocument;
    VytvormaleOWL1: TMenuItem;
    DevicesInstancesList: TListBox;
    Label4: TLabel;
    Pidejzazen1: TMenuItem;
    Smazazen1: TMenuItem;
    Label5: TLabel;
    ListBox1: TListBox;
    Label6: TLabel;
    edZkratka: TEdit;
    edBarva: TEdit;
    Label7: TLabel;
    edMaterial: TEdit;
    Label8: TLabel;
    edLokalita: TEdit;
    Label10: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure ZobrazMenu();
    procedure LoadOnotology();
    procedure LoadGenericOntology();
    procedure VypisLokality();
    procedure VypisInstanceLokaci(instancesOf: string);
    procedure VypisInstanceZarizeni(instanceOf: string);
    procedure VytvordMaleOWL();
    procedure PridejDoOntologie();
    procedure OdeberZOntologie();
    procedure RefreshLists();
    procedure ForceXMLDocumentReload(doc: TXMLDocument);
    procedure Konec1Click(Sender: TObject);
    procedure VytvormaleOWL1Click(Sender: TObject);
    procedure LocationsListClick(Sender: TObject);
    procedure DevicesListClick(Sender: TObject);
    procedure LocationInstancesListClick(Sender: TObject);
    procedure DevicesInstancesListClick(Sender: TObject);
    procedure Pidejzazen1Click(Sender: TObject);
    procedure Smazazen1Click(Sender: TObject);
    procedure Info1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;
  Config: TConfig;
  OntoCore: TOntoCore;
  SelectedDevice: PDevice;
  SelectedLocationInstance: PInstance;
  SelectedDeviceInstance: PInstance;
  SelectedLocationLeaf: string;
  SelectedLocationInstances: TInstances;
  SelectedDeviceInstances: TInstances;

implementation

{$R *.dfm}

procedure TForm1.FormCreate(Sender: TObject);
begin

    // inicializace
    OntoCore := TOntoCore.Create();
    SelectedLocationInstances := TInstances.Create();
    SelectedDeviceInstances := TInstances.Create();

    // inicializacni metody
    Config := LoadConfig('./Data/ontology.exe.config');
    LoadOnotology();
    LoadGenericOntology();
    ZobrazMenu();
    VypisLokality();
    Listbox1.visible := false;
    RefreshLists();

end;

procedure TForm1.ZobrazMenu();
var
  i: integer;
begin
  for i := 1 to 3 do
  begin
    DevicesList.Items.Add(Config.Devices[i].literal);
  end;
  DevicesList.Selected[0] := true;
end;

procedure TForm1.LoadOnotology();
begin
  xmlonto.LoadFromFile('.\data\household.owl');
  xmlonto.Active := true;
end;

procedure TForm1.LoadGenericOntology();
begin
  xmlgenericonto.LoadFromFile('.\data\generic.owl');
  xmlonto.Active := true;
end;

procedure TForm1.Konec1Click(Sender: TObject);
begin
  close;
end;

procedure TForm1.VypisLokality();
var
  locations: TStringList;
begin

  locations := TStringList.Create();
  ontocore.GetClassLeafs(xmlonto, 'Location', @locations);
  if ((locations <> nil) and (locations.Count > 0)) then
  begin
    LocationsList.Items.AddStrings(locations);
    LocationsList.Selected[0] := true;
  end;

end;

procedure TForm1.LocationsListClick(Sender: TObject);
var
  i: integer;
begin

  I := 0;
  while (I < LocationsList.Items.Count) and (not LocationsList.Selected[i]) do
    inc(i);

  selectedLocationLeaf := LocationsList.Items[i];

  VypisInstanceLokaci(selectedLocationLeaf);
end;

procedure TForm1.VypisInstanceLokaci(instancesOf: string);
var
  FakeInstance: TInstance;
begin
  SelectedLocationInstances.Clear();
  FakeInstance.Name := 'Nova mistnost';
  FakeInstance.IsFakeInstance := true;
  SelectedLocationInstances.Add(FakeInstance);
  ontoCore.GetClassInstances(xmlonto, instancesOf, @SelectedLocationInstances);
  LocationInstancesList.Clear();
  LocationInstancesList.Items.AddStrings(SelectedLocationInstances.ToStringList());
  LocationInstancesList.Selected[0] := true;
  selectedLocationInstance := @FakeInstance;
end;

procedure TForm1.DevicesListClick(Sender: TObject);
var
  i: integer;
begin
  I := 0;
  while (I < DevicesList.Items.Count) and (not DevicesList.Selected[i]) do
    inc(i);
  selectedDevice := @Config.Devices[i+1];

  VypisInstanceZarizeni(selectedDevice^.DeviceType);
end;

procedure TForm1.VypisInstanceZarizeni(instanceOf: string);
begin
  SelectedDeviceInstances.Clear();
  ontoCore.GetClassInstances(xmlonto, instanceOf, @SelectedDeviceInstances);
  DevicesInstancesList.Clear();
  if ((SelectedDeviceInstances <> nil) and (SelectedDeviceInstances.GetSize() > 0)) then
  begin
    DevicesInstancesList.items.addstrings(SelectedDeviceInstances.ToStringList());
    DevicesInstancesList.selected[0] := true;
  end;
end;

procedure TForm1.VytvormaleOWL1Click(Sender: TObject);
begin
  VytvordMaleOWL();
end;

procedure TForm1.VytvordMaleOWL();
var
  UpdatedGenericOntoPath: string;
  Location: TLocation;
begin

  Location.IsInstance := SelectedLocationInstance.IsFakeInstance;
  Location.Name := SelectedLocationInstance.Name;

  Location.Levels := TStringList.Create();

  // Vsimnete si, ze nasledujici dva levely lokace maji pro OntoCore funkce vyznam pouze tehdy,
  // pokud se jedna o novou instanci - a v tom pripade se bude zatim podporovat jen object_1 a level_1
  Location.Levels.Add('Object_1');
  Location.Levels.Add('Level_1');
  Location.Levels.Add(SelectedLocationLeaf);

  // mocne volani!
  UpdatedGenericOntoPath := OntoCore.AddDeviceToGenericOntology(XMLGenericOnto, SelectedDevice, Location, XMLOnto);
end;

procedure TForm1.PridejDoOntologie();
var
  Location: TLocation;
begin

  Location.IsInstance := SelectedLocationInstance.IsFakeInstance;
  Location.Name := SelectedLocationInstance.Name;

  Location.Levels := TStringList.Create();

  // Vsimnete si, ze nasledujici dva levely lokace maji pro OntoCore funkce vyznam pouze tehdy,
  // pokud se jedna o novou instanci - a v tom pripade se bude zatim podporovat jen object_1 a level_1
  Location.Levels.Add('Object_1'); // zatim to tak bude vzdy
  Location.Levels.Add('Level_1'); // zatim to tak bude vzdy
  Location.Levels.Add(SelectedLocationLeaf);

  // dalsi mocne volani!
  OntoCore.AddDeviceToOntology(XMLOnto, SelectedDevice, Location);

  // vse uloz a reloadni
  ForceXMLDocumentReload(XMLOnto);

  // refreshni listboxy
  RefreshLists();

end;

procedure TForm1.OdeberZOntologie();
begin

  OntoCore.RemoveDeviceFromOntology(XMLOnto, SelectedDeviceInstance);

  ForceXMLDocumentReload(XMLOnto);

  // refreshni listboxy
  RefreshLists();

end;

procedure TForm1.LocationInstancesListClick(Sender: TObject);
var
  i: integer;
begin

  I := 0;
  while (I < LocationInstancesList.Items.Count) and (not LocationInstancesList.Selected[i]) do
    inc(i);

  selectedLocationInstance := SelectedLocationInstances.GetInstance(i);

end;

procedure TForm1.DevicesInstancesListClick(Sender: TObject);
var
  i: integer;
begin

  I := 0; // pozor! listbox je indexovan od 0
  while (I < DevicesInstancesList.Items.Count) and (not DevicesInstancesList.Selected[i]) do
    inc(i);

  if (i < DevicesInstancesList.Items.Count) then
  begin

    selectedDeviceInstance := SelectedDeviceInstances.GetInstance(i+1);

    Smazazen1.Enabled := selectedDeviceInstance^.CreatedByThisApp;
    edZkratka.Text :=  selectedDeviceInstance^.Shortcut;
    edBarva.Text := selectedDeviceInstance^.Color;
    edMaterial.Text := selectedDeviceInstance^.Material;
    edLokalita.Text := selectedDeviceInstance^.AccurateLocality;

  end;

end;

procedure TForm1.Pidejzazen1Click(Sender: TObject);
begin
  PridejDoOntologie();
end;


procedure TForm1.Smazazen1Click(Sender: TObject);
begin
  OdeberZOntologie();
end;


procedure TForm1.Info1Click(Sender: TObject);
begin
  listbox1.Visible:=true;
end;

procedure TForm1.RefreshLists();
begin

    LocationsListClick(self);
    DevicesListClick(self);
    LocationInstancesListClick(self);
    DevicesInstancesListClick(self);

end;

procedure TForm1.ForceXMLDocumentReload(doc: TXMLDocument);
begin

  doc.SaveToFile();
  doc.Active := false;
  doc.LoadFromFile();
  doc.Active := true;

end;

end.

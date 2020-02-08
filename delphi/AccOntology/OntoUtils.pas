unit OntoUtils;

interface

uses
  Variants, xmldom, XMLIntf, msxmldom, XMLDoc, SysUtils, Classes, StrUtils;

type

  PStringList = ^TStringList;

  TFunction = record
    Levels: TStringList; // Jmena retezu uzlu, pod ktery budeme chtit danou funkci zaradit
    Name: string;
    Literal: string;
  end;

  TProperty = record
    Name: string;
    Value: string;
    Allowed: boolean;
  end;

  TAccurateLocation = record
    Name: string;
    Allowed: boolean;
  end;


  TDevice = record
    Category: string;
    DeviceType: string;
    Literal: string;
    ResSheet: string;
    UISheet: string;
    AccurateLocation: TAccurateLocation;
    VocabularyLanguage: string;

    Functions: array [1..30] of TFunction;
    Properties: array [1..3] of TProperty;
  end;

  TConfig = record
    Devices: array [1..3] of TDevice;
  end;

  PConfig = ^TConfig;
  PDevice = ^TDevice;
  PFunction = ^TFunction;
  PProperty = ^TProperty;

  TConfigProcessor = class
    public
      function Parse(XMLConfig: IXMLDocument; Config: PConfig) : boolean;
    private
        function ParseSingleSection(ConfigSection: IXMLNode; Config: PConfig) : boolean;

      // Funkce tykajici se parsovani configurace zarizeni
          function ParseDevices(DevicesSection: IXMLNode; Config: PConfig) : boolean;
            function ParseDeviceProperties(DeviceProperties: IXMLNodeList; Device: PDevice) : boolean;
              function ParseFunctions(DeviceFunctions: IXMLNodeList; Device: PDevice) : boolean;
                function ParseSingleFunction(DeviceFunction: IXMLNode; Func: PFunction) : boolean;
              function ParseProperties(DeviceProperties: IXMLNodeList; Device: PDevice) : boolean;
                function ParseSingleProperty(DeviceProperty: IXMLNode; Prop: PProperty) : boolean;
              function ParseAccurateLocality(AccurateLocality: IXMLNode; Device: PDevice) : boolean;

      // Zde by mohly byt jine funkce
      function ParseLevels(Levels: IXMLNodeList; ParsedLevels: PStringList) : integer;
  end;

  function OleVariantToStr(value: OleVariant) : string;
  function FindNodeInList(const NodeList: IXMLNodeList; const FullName: string) : IXMLNode;
  function GetExactName(const DecoratedName: string) : string;
  function GetDecoratedName(const Name: string) : string;
  function LoadConfig(const ConfigFile: string) : TConfig;

implementation

  // OleVariantToStr:
  //    GetAttributeNS navraci OleVariant, tudiz poskytneme metodu, ktera jej
  //    prevede bezpecne na string

  function OleVariantToStr(value: OleVariant) : string;
  var
    retVal: string; 
  begin
    retVal := '';

    try
      case VarType(value) of
        varOleStr,
        varStrArg,
        varString:
          retVal := value; // mame obycejny retezec
        else
          retVal := ''; // v kazdem jinem pripade (jako je varNull)
      end;
    except
      // hozene vyjimky budeme ignorovat - navratova hodnota zustane prazdny
      // retezec
    end;

    OleVariantToStr := retVal;

  end;

  // FindNodeInList:
  //    Metoda TXMLNodeListu nejak nefunguje - musim si ji tedy primitivne
  //    implementovat sam
  
  function FindNodeInList(const NodeList: IXMLNodeList; const FullName: string) : IXMLNode;
  var
    node: IXMLNode; 
  begin
    node := NodeList.First;
    while (node <> nil) and (CompareText(node.NodeName, FullName) <> 0) do
      node := node.NextSibling;
    FindNodeInList := node;
  end;


  // LoadConfig:
  //    Implementace interfacove metody - pouze instancuje ConfigProcessor
  //    a necha ho rozparsovat predany xml config
  
  function LoadConfig(const ConfigFile: string) : TConfig;
  var
    XMLFile: TXMLDocument;
    ConfigProcessor: TConfigProcessor;
    Config: TConfig;
    Success: boolean;
  begin
    XMLFile := TXMLDocument.Create(ConfigFile);
    ConfigProcessor := TConfigProcessor.Create();

    Success := ConfigProcessor.Parse(XMLFile, @Config);

    if (Success) then
      LoadConfig := Config;

  end;

  // GetExactName:
  //    Zbavi se sharp (#) znaku na zacatku jmena, pokud tam nejaky je

  function GetExactName(const DecoratedName: string) : string;
  var
    RetVal: string;
  begin
    RetVal := '';

    if length(DecoratedName) > 0 then
    begin
      if (DecoratedName[1] = '#') then
        RetVal := RightStr(DecoratedName, length(DecoratedName) - 1)
      else
        RetVal := DecoratedName;
    end;

    GetExactName := RetVal;
  end;


  // GetDecoratedName:
  //    Pripoji znak sharp (#) na zacatek jmena, pokud uz tam nahodou neni

  function GetDecoratedName(const Name: string) : string;
  var
    RetVal: string;
  begin
    RetVal := '';

    if length(Name) > 0 then
    begin
      if (Name[1] = '#') then
        RetVal := Name
      else
        RetVal := concat('#',Name);
    end;

    GetDecoratedName := RetVal;
  end;


  // Parse:
  //    Vstupni metoda tridy - obdrzi xml document a rozparsuje ho vhodne do ziskaneho configu
 
  function TConfigProcessor.Parse(XMLConfig: IXMLDocument; Config: PConfig) : boolean;
  var
    ConfigSection: IXMLNode;
    ConfigSections: IXMLNodeList;
    Success: boolean;
  begin

    Success := true;
    ConfigSections := XMLConfig.DocumentElement.ChildNodes; // Vsechny sekce config file
    ConfigSection := ConfigSections.First;

    while ((ConfigSection <> nil) and (Success)) do // Pro kazdou sekci v configu
    begin

      Success := ParseSingleSection(ConfigSection, Config);
      ConfigSection := ConfigSection.NextSibling;

    end;

    Parse := Success;
  end;


  // ParseSingleSection:
  //    Metoda obdrzi jako parametr uzel se sekci configu - zjisti o jakou sekci
  //    se jedna (Devices, aj.) a pusti na ni vhodny parser

  function TConfigProcessor.ParseSingleSection(ConfigSection: IXMLNode; Config: PConfig) : boolean;
  var
    Success: boolean;
    SectionName: string;
  begin

    Success := true;
    SectionName := ConfigSection.NodeName;

    // Nyni se rozhodneme, kterou sekci prave chceme parsovat:
    if (CompareText(SectionName, 'devices') = 0) then
      Success := ParseDevices(ConfigSection, Config)
    else if (CompareText(SectionName, 'jinaSekce') = 0) then
      // Zatim mame pouze sekci Devices - jen pro ukazku
    else
      ; // Nezname sekce budeme zatim proste ignorovat

    ParseSingleSection := Success;

  end;


  // ParseDevices
  //    Parsovaci funkce pro zarizeni - ta jiz presne zna vsechny podstatne
  //    attributy a uzly sve sekce
  
  function TConfigProcessor.ParseDevices(DevicesSection: IXMLNode; Config: PConfig) : boolean;
  var
    DeviceNode: IXMLNode;
    iDevice: integer;
    Device: TDevice;
    Success: boolean;
    Attributes: IXMLNodeList;
  begin

    DeviceNode := DevicesSection.ChildNodes.First;
    iDevice := 1;
    Success := true;

    while ((DeviceNode <> nil) and (Success)) do // Pro kazde zarizeni v configu
    begin
      try
        Attributes := DeviceNode.AttributeNodes;

        if ((Attributes <> nil) and (Attributes.Count > 0)) then
        begin

          Device.Literal := Attributes['literal'].Text;
          Device.DeviceType := Attributes['type'].Text;
          Device.ResSheet := Attributes['rsheet'].Text;
          Device.UISheet := Attributes['uis'].Text;
          Device.Category := Attributes['category'].Text;

        end;

        // Kazde zarizeni ma jeste dalsi vlastnosti (jako definici funkci a properties)
        Success := ParseDeviceProperties(DeviceNode.ChildNodes, @Device);

        Config.Devices[iDevice] := Device;

        DeviceNode := DeviceNode.NextSibling;
        inc(iDevice);
      except
        Success := false; // kdyby se stalo neco neocekavaneho, zaznamenej to jako neuspech
      end;    
    end;

    ParseDevices := Success;

  end;


  // ParseDeviceProperties:
  //    Jako parametr dostane seznam vsech dodatecnych vlastnosti jednoho zarizeni
  //    a dle typu vlastnosti, pusti na kazdou jeji parsovaci funkci

  function TConfigProcessor.ParseDeviceProperties(DeviceProperties: IXMLNodeList; Device: PDevice) : boolean;
  var
    DeviceProperty: IXMLNode;
    Success: boolean;
    PropertyName: string;
  begin

    Success := true;
    DeviceProperty := DeviceProperties.First;

    while ((DeviceProperty <> nil) and (Success)) do // pro kazdou vlastnost
    begin

      PropertyName := DeviceProperty.NodeName; // jednotlive vlastnosti rozeznavame podle jmena

      if (CompareText(PropertyName, 'functions') = 0) then
        Success := ParseFunctions(DeviceProperty.ChildNodes, Device)
      else if (CompareText(PropertyName, 'properties') = 0) then
        Success := ParseProperties(DeviceProperty.ChildNodes, Device)
      else if (CompareText(PropertyName, 'accurateLocality') = 0) then
        Success := ParseAccurateLocality(DeviceProperty, Device)
      else
        ; // Nezname vlastnosti budeme zatim proste ignorovat

      DeviceProperty := DeviceProperty.NextSibling;

    end;

    ParseDeviceProperties := Success;
  end;

  // ParseFunctions
  //    Jako parametr dostane senzam funkci, prirazenych k zariazeni a parsuje
  //    ho do pole funkci
  
  function TConfigProcessor.ParseFunctions(DeviceFunctions: IXMLNodeList; Device: PDevice) : boolean;
  var
    SingleFunction: IXMLNode;
    ParsedSingleFunction: TFunction;
    Success: boolean;
    iFunctions: integer;
  begin

    Success := true;

    if (DeviceFunctions <> nil) then
    begin

      iFunctions := 1;
      SingleFunction := DeviceFunctions.First;
      while ((SingleFunction <> nil) and (Success)) do
      begin

        Success := ParseSingleFunction(SingleFunction, @ParsedSingleFunction);
        if (Success) then
        begin
          Device^.Functions[iFunctions] := ParsedSingleFunction;
          inc(iFunctions);
        end;

        SingleFunction := SingleFunction.NextSibling;

      end;

    end;

    ParseFunctions := true;

  end;


  // ParseFunctions
  //    Jako parametr dostane senzam vlastnosti, prirazenych k zariazeni
  //    a parsuje ho do pole vlastnosti

  function TConfigProcessor.ParseProperties(DeviceProperties: IXMLNodeList; Device: PDevice) : boolean;
  var
    SingleProperty: IXMLNode;
    ParsedSingleProperty: TProperty;
    Success: boolean;
    iProperties: integer;
  begin

    Success := true;

    if (DeviceProperties <> nil) then
    begin

      iProperties := 1;
      SingleProperty := DeviceProperties.First;
      while ((SingleProperty <> nil) and (Success)) do
      begin

        Success := ParseSingleProperty(SingleProperty, @ParsedSingleProperty);
        if (Success) then
        begin
          Device^.Properties[iProperties] := ParsedSingleProperty;
          inc(iProperties);
        end;

        SingleProperty := SingleProperty.NextSibling;

      end;

    end;

    ParseProperties := true;
  end;

  // ParseFunction:
  //    Parsovaci funkce pro funkci ( :) )

  function TConfigProcessor.ParseSingleFunction(DeviceFunction: IXMLNode; Func: PFunction) : boolean;
  var
    Success: boolean;
    ParsedLevels: TStringList;
    Attributes: IXMLNodeList;
  begin

    ParsedLevels := TStringList.Create();
    Success := true;

    try

      Attributes := DeviceFunction.AttributeNodes;

      if (ParseLevels(Attributes, @ParsedLevels) > 0) then
        Func^.Levels := ParsedLevels
      else
        Func^.Levels := nil;

      if ((Attributes <> nil) and (Attributes.Count > 0)) then
      begin

        Func^.Name := Attributes['name'].Text;
        Func^.Literal := Attributes['literal'].Text;

      end;
    except
      Success := false;
    end;

    ParseSingleFunction := Success;
  end;


  // ParseProperty:
  //    Parosvaci funkce pro property

  function TConfigProcessor.ParseSingleProperty(DeviceProperty: IXMLNode; Prop: PProperty) : boolean;
  var
    Success: boolean;
    Name, Value: string;
    Allowed: boolean;
    Attributes: IXMLNodeList;
  begin

    Success := true;

    try

      Allowed := false;
      Name := DeviceProperty.Attributes['name'];
      Attributes := DeviceProperty.AttributeNodes;

      if ((Attributes <> nil) and (Attributes.Count > 0)) then
      begin

        Value := Attributes['has'].Text

      end;

      if (CompareText(Value, 'true') = 0) then
        Allowed := true;

      Prop^.Name := Name;
      Prop^.Allowed := Allowed;

    except
      Success := false;
    end;

    ParseSingleProperty := Success;
  end;


  // ParseAccurateLocality
  //    Parsovaci funkce pro nastaveni povoleni presne lokace

  function TConfigProcessor.ParseAccurateLocality(AccurateLocality: IXMLNode; Device: PDevice) : boolean;
  var
    Success, Allowed: boolean;
    Attributes: IXMLNodeList;
  begin

    Success := true;
    Allowed := false;

    try

      Attributes := AccurateLocality.AttributeNodes;
      if ((Attributes <> nil) and (Attributes.Count > 0)) then
      begin

        if (CompareText(Attributes['hasAccuratLocality'].Text, 'true') = 0) then
          Allowed := true;

      end;

      Device^.AccurateLocation.Allowed := Allowed;

    except
      Success := false;
    end;

    ParseAccurateLocality := Success;
  end;

  // ParseLevels:
  //    Tato metoda se pokusi najit v zadanem listu Attributu attribut Path, ve
  //    kterem je ulozena hiearchie funkce/lokace/etc, zapsana jako retezec oddeleny '/'
  //    Vysledek se ulozi do TStringListu

  function TConfigProcessor.ParseLevels(Levels: IXMLNodeList; ParsedLevels: PStringList) : integer;
  var
    Size: integer;
    Path: string;
  begin

    Size := 0;

    if ((Levels <> nil) and (Levels.Count > 0)) then
    begin

      Path := OleVariantToStr(Levels['path'].Text);

      if (Path <> '') then
      begin

        ParsedLevels^.Delimiter := '/';
        ParsedLevels^.DelimitedText := Path;
        Size := ParsedLevels^.Count;

      end;

    end;

    ParseLevels := Size;

  end;

end.

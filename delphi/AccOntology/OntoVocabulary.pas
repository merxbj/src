unit OntoVocabulary;

interface

uses StrUtils, Classes, SysUtils, xmldom, XMLIntf, msxmldom, XMLDoc, OntoUtils;

type

  TVocabularyEntry = record
    Name: string;
    Language: string;
    Synonyms: TStringList;
  end;

  PVocabularyEntry = ^TVocabularyEntry;

  TVocabulary = class
    private
      VocabularyEntries: array of TVocabularyEntry;
      VocabularyLanguages: TStringList;
      Size: integer;

      function GetEntryByIndex(Index: integer) : PVocabularyEntry;

      procedure Clear();
      procedure ParseToXML(VocabularyXML: IXMLDocument; FileName: String);
      procedure ParseFromXML(VocabularyXML: IXMLDocument);
      procedure ParseSingleEntry(EntryNode: IXMLNode);
      procedure ParseSynonyms(SynonymsNode: IXMLNode; Entry: PVocabularyEntry);
      procedure SaveSingleEntry(SingleEntry: PVocabularyEntry; RootElement: IXMLNode);
    public
      constructor Create; overload;

      function GetSize() : integer;
      function GetEntryByName(Name: string; Lang: string) : PVocabularyEntry;
      function LoadFromFile(FileName: string) : boolean;
      function GetLangCount() : integer;
      function GetLangByIndex(Index: integer) : string;

      procedure Add(Entry: TVocabularyEntry);
      procedure Remove(Entry: PVocabularyEntry);
      procedure RemoveByName(Name: string; Lang: string = '');
      procedure SaveToFile(FileName: string);
  end;

  PVocabulary = ^TVocabulary;

implementation

  constructor TVocabulary.Create;
  begin

    inherited;

    Clear();

  end;

  function TVocabulary.GetSize() : integer;
  begin

    GetSize := Size;

  end;

  function TVocabulary.GetEntryByName(Name: string; Lang: string) : PVocabularyEntry;
  var
    i: integer;
    Entry: PVocabularyEntry;
    Found: boolean;
  begin

    i := 0;
    found := false;
    Entry := nil;

    while ((i < GetSize()) and (not Found)) do
    begin

      Entry := GetEntryByIndex(i);

      if ((CompareText(Entry.Name, Name) = 0) and
          (CompareText(Entry.Language, Lang) = 0)) then
        found := true;

      i := i + 1;

    end;

    if (found) then
      GetEntryByName := Entry
    else
      GetEntryByName := nil;

  end;

  function TVocabulary.GetEntryByIndex(Index: integer) : PVocabularyEntry;
  begin

    if (Index < GetSize) then
      GetEntryByIndex := @VocabularyEntries[Index]
    else
      GetEntryByIndex := nil;

  end;

  procedure TVocabulary.Clear();
  begin

    VocabularyEntries := nil;
    SetLength(VocabularyEntries, 10); // startovni velikost

    VocabularyLanguages := nil;
    VocabularyLanguages := TStringList.Create();
    VocabularyLanguages.Sorted := true;
    VocabularyLanguages.Duplicates := dupIgnore;

    Size := 0;

  end;

  procedure TVocabulary.Add(Entry: TVocabularyEntry);
  var
    PossibleExisitingEntry: PVocabularyEntry;
  begin

    PossibleExisitingEntry := GetEntryByName(Entry.Name, Entry.Language);

    if (PossibleExisitingEntry <> nil) then
    begin

      PossibleExisitingEntry^.Synonyms := Entry.Synonyms;

    end else
    begin

      if (GetSize() = ((High(VocabularyEntries) - Low(VocabularyEntries)) + 1 )) then
        SetLength(VocabularyEntries, GetSize() + 10); // udelejme si vic mista

      VocabularyEntries[GetSize()] := Entry;
      VocabularyLanguages.Add(Entry.Language);
      size := size + 1;

    end;

  end;


  // Remove
  //    Protoze nemame tuto kolekci implementovanou jako linked list - coz by
  //    bylo rozhodne efektivnejsi, implementujeme zde odebrani zaznamu velice
  //    hloupe a neefektivne.

  procedure TVocabulary.Remove(Entry: PVocabularyEntry);
  var
    TempEntry: PVocabularyEntry;
    i, j: integer;
  begin

    i := 0;
    TempEntry := GetEntryByIndex(i);
    while ((TempEntry <> nil) and (TempEntry <> Entry) and (i < GetSize())) do
    begin

      i := i + 1;
      TempEntry := GetEntryByIndex(i);

    end;

    if (i < GetSize()) then
    begin

      j := i;
      while (j < (GetSize() - 1)) do
      begin

        VocabularyEntries[j] := VocabularyEntries[j+1];
        j := j + 1;

      end;

      size := size - 1;

    end;

  end;

  function TVocabulary.LoadFromFile(FileName: String) : boolean;
  var
    VocabularyXML: TXMLDocument;
    Success: boolean;
  begin

    Success := false;

    try

      if (FileExists(FileName)) then
      begin

        VocabularyXML := TXMLDocument.Create(nil);
        VocabularyXML.LoadFromFile(FileName);

        VocabularyXML.Active := true;

        self.Clear();
        ParseFromXML(VocabularyXML);

        Success := true;

      end;

    finally

      VocabularyXML := nil;

    end;

    LoadFromFile := Success;

  end;

  procedure TVocabulary.SaveToFile(FileName: String);
  var
    VocabularyXML: TXMLDocument;
  begin

    try

      VocabularyXML := TXMLDocument.Create(nil);
      VocabularyXML.Active := true;
      VocabularyXML.Options:=[doAutoSave, doNodeAutoCreate, doNodeAutoIndent];
      ParseToXML(VocabularyXML, FileName);
      // VocabularyXML.SaveToFile(FileName); !! See ParseToXML() !!

    finally

      VocabularyXML := nil;

    end;

  end;

  procedure TVocabulary.ParseToXML(VocabularyXML: IXMLDocument; FileName: String);
  var
    RootElement: IXMLNode;
    i: integer;
    XMLLines: TStringList;
  begin

    RootElement := VocabularyXML.AddChild('Vocabulary');

    for i := 0 to GetSize() - 1 do
    begin

      SaveSingleEntry(GetEntryByIndex(i), RootElement);

    end;

    // Protoze TXMLDocument neumi pridavat <?xml> element
    // musime si je do dokumentu pridat samy!
    XMLLines := TStringList.Create();
    XMLLines.Assign(VocabularyXML.XML);
    XMLLines.Insert(0, '<?xml version="1.0" encoding="utf-8"?>');
    XMLLines.SaveToFile(FileName);

  end;

  procedure TVocabulary.ParseFromXML(VocabularyXML: IXMLDocument);
  var
    RootElement, VocabularyEntryRaw: IXMLNode;
  begin

    RootElement := VocabularyXML.ChildNodes.FindNode('Vocabulary');

    if (RootElement <> nil) then
    begin

      VocabularyEntryRaw := RootElement.ChildNodes.First;

      while (VocabularyEntryRaw <> nil) do
      begin

        ParseSingleEntry(VocabularyEntryRaw);
        VocabularyEntryRaw := VocabularyEntryRaw.NextSibling;

      end;

    end;
      
  end;

  procedure TVocabulary.ParseSingleEntry(EntryNode: IXMLNode);
  var
    Name, Lang: string;
    Entry: TVocabularyEntry;
  begin

    Name := OleVariantToStr(EntryNode.Attributes['Name']);
    Lang := OleVariantToStr(EntryNode.Attributes['Language']);
    if ((Name <> '') and (Lang <> '')) then
    begin

      Entry.Name := Name;
      Entry.Language := Lang;
      ParseSynonyms(EntryNode, @Entry);

      self.Add(Entry);
      VocabularyLanguages.Add(Lang);

    end;

  end;

  procedure TVocabulary.ParseSynonyms(SynonymsNode: IXMLNode; Entry: PVocabularyEntry);
  var
    SingleSynonymNode: IXMLNode;
    Synonym: String;
  begin

    if (SynonymsNode <> nil) then
    begin

      Entry^.Synonyms := TStringList.Create();

      SingleSynonymNode := SynonymsNode.ChildNodes.First;

      while (SingleSynonymNode <> nil) do
      begin

        Synonym := SingleSynonymNode.Text;
        if (Synonym <> '') then
          Entry^.Synonyms.Add(Synonym);

        SingleSynonymNode := SingleSynonymNode.NextSibling;

      end;

    end;

  end;

  procedure TVocabulary.SaveSingleEntry(SingleEntry: PVocabularyEntry; RootElement: IXMLNode);
  var
    XMLEntry, SynonymNode: IXMLNode;
    i: integer;
  begin

    XMLEntry := RootElement.AddChild('Label');

    if (XMLEntry <> nil) then
    begin

      XMLEntry.Attributes['Name'] := SingleEntry^.Name;
      XMLEntry.Attributes['Language'] := SingleEntry^.Language;

      for i := 0 to SingleEntry^.Synonyms.Count - 1 do
      begin

        SynonymNode := XMLEntry.AddChild('Synonym');

        if (SynonymNode <> nil) then
        begin

          SynonymNode.Text := SingleEntry^.Synonyms.Strings[i];

        end;

      end;

    end;

  end;

  function TVocabulary.GetLangCount() : integer;
  begin

    GetLangCount := VocabularyLanguages.Count;

  end;

  function TVocabulary.GetLangByIndex(Index: integer) : string;
  begin

    if (Index < GetLangCount()) then
      GetLangByIndex := VocabularyLanguages.Strings[Index]
    else
      GetLangByIndex := '';

  end;

  procedure TVocabulary.RemoveByName(Name: string; Lang: string = '');
  var
    TempEntry: PVocabularyEntry;
    i: integer;
  begin

    if (Lang <> '') then
    begin

      TempEntry := GetEntryByName(Name, Lang);

      if (TempEntry <> nil) then
        Remove(TempEntry);

    end
    else begin

      for i := 0 to (GetLangCount() - 1) do
      begin

        TempEntry := GetEntryByName(Name, GetLangByIndex(i));

        if (TempEntry <> nil) then
          Remove(TempEntry);

      end;

    end;

  end;

end.

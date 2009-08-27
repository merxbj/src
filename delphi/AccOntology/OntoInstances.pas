unit OntoInstances;

interface

uses StrUtils, Classes;

type

  TTranslatedProperty = record
    Name: String;
    Value: String;
  end;

  TInstance = record
    Name: string;
    CreatedByThisApp: boolean;
    IsFakeInstance: boolean;
    Shortcut: TTranslatedProperty;
    Color: TTranslatedProperty;
    Material: TTranslatedProperty;
    AccurateLocality: TTranslatedProperty;
    Language: string;
  end;

  PInstance = ^TInstance;

  TInstances = class
    private
      Instances: array of TInstance;
      Size: integer;
      Language: string;
    public
      constructor Create; overload;

      function GetSize() : integer;
      function GetInstance(Index: integer) : PInstance;
      function ToStringList() : TStringList;
      function GetLanguage() : string;
      function Add(Instance: TInstance) : integer;

      procedure Clear();
      procedure SetLanguage(Lang: string);

  end;

  PInstances = ^TInstances;

implementation

  constructor TInstances.Create;
  begin

    inherited;

    Clear();

  end;

  function TInstances.GetSize() : integer;
  begin

    GetSize := Size;

  end;

  function TInstances.GetInstance(Index: integer) : PInstance;
  begin

    if (Index < GetSize()) then
      GetInstance := @(Instances[Index])
    else
      GetInstance := nil;
  end;

  function TInstances.ToStringList() : TStringList;
  var
    i: integer;
    list: TStringList;
  begin

    list := TStringList.Create();

    for i := 0 to GetSize() - 1 do
    begin

      list.Add(GetInstance(i).Name);

    end;

    ToStringList := list;

  end;

  function TInstances.Add(Instance: TInstance) : integer;
  begin

    if (GetSize() = ((High(Instances) - Low(Instances)) + 1 )) then
      SetLength(Instances, GetSize() + 10); // udelejme si vic mista pro instance

    Instances[GetSize()] := Instance;
    size := size + 1;

    Add := (size - 1); // navrat pozici ulozeni instance

  end;

  procedure TInstances.Clear();
  begin

    Instances := nil;
    SetLength(Instances, 10); // startovni velikost pole instanci

    size := 0;

  end;

  procedure TInstances.SetLanguage(Lang: string);
  begin

    self.Language := Lang;

  end;

  function TInstances.GetLanguage() : string;
  begin

    GetLanguage := self.Language;

  end;

end.

unit OntoInstances;

interface

uses StrUtils, Classes;

type

  TInstance = record
    Name: string;
    CreatedByThisApp: boolean;
    IsFakeInstance: boolean;
    Shortcut: string;
    Color: string;
    Material: string;
    AccurateLocality: string;
  end;

  PInstance = ^TInstance;

  TInstances = class
    private
      Instances: array of TInstance;
      Size: integer;
    public
      constructor Create; overload;

      function GetSize() : integer;
      function GetInstance(Index: integer) : PInstance;
      function ToStringList() : TStringList;

      procedure Add(Instance: TInstance);
      procedure Clear();

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

  procedure TInstances.Add(Instance: TInstance);
  begin

    if (GetSize() = ((High(Instances) - Low(Instances)) + 1 )) then
      SetLength(Instances, GetSize() + 10); // udelejme si vic mista pro instance

    Instances[GetSize()] := Instance;
    size := size + 1;

  end;

  procedure TInstances.Clear();
  var
    i: integer;
  begin

    Instances := nil;
    SetLength(Instances, 10); // startovni velikost pole instanci

    size := 0;

  end;

end.

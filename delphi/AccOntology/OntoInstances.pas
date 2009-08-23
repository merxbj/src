unit OntoInstances;

interface

uses StrUtils, Classes;

type

  TInstance = record
    Name: string;
    CreatedByThisApp: boolean;
    Shortcut: string;
    Color: string;
    Material: string;
    AccurateLocality: string;
  end;

  PInstance = ^TInstance;

  TInstances = class
    private
      Instances: array [1..100] of TInstance;
      Size: integer;
    public
      function GetSize() : integer;
      function GetInstance(Index: integer) : PInstance;
      function ToStringList() : TStringList;

      procedure Add(Instance: TInstance);
      procedure Clear();

  end;

  PInstances = ^TInstances;

implementation

  function TInstances.GetSize() : integer;
  begin

    GetSize := Size;

  end;

  function TInstances.GetInstance(Index: integer) : PInstance;
  begin

    if (Index <= GetSize()) then
      GetInstance := @Instances[Index]
    else
      GetInstance := nil;
  end;

  function TInstances.ToStringList() : TStringList;
  var
    i: integer;
    list: TStringList;
  begin

    list := TStringList.Create();

    for i := 1 to GetSize() do
    begin

      list.Add(GetInstance(i).Name);

    end;

    ToStringList := list;

  end;

  procedure TInstances.Add(Instance: TInstance);
  begin

    if (GetSize() < 100) then
    begin

      Instances[GetSize() + 1] := Instance;
      size := size + 1;

    end;

  end;

  procedure TInstances.Clear();
  var
    i: integer;
  begin

    for i := 1 to GetSize() do
    begin

      Instances[i].Name := '';
      Instances[i].CreatedByThisApp := false;
      Instances[i].Shortcut := '';
      Instances[i].Color := '';
      Instances[i].Material := '';
      Instances[i].AccurateLocality := '';

    end;

    size := 0;

  end;

end.

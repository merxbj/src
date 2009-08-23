unit OntoInstances;

interface

uses StrUtils, Classes;

type

  TInstance = record
    name: string;
    CreatedByThisApp: boolean;
    Shortcut: string;
    Color: string;
    Material: string;
    Vocabulary: string;
    AccurateLocality: string;
  end;

  TInstances = class
    private
      Instances: array [1..100] of TInstance;
      Size: integer;
    public
      function GetSize() : integer;
      function GetInstance(Index: integer) : TInstance;
      function ToStringList() : TStringList;
  end;

implementation

  function TInstances.GetSize() : integer;
  begin

    GetSize := self.Size;

  end;

  function TInstances.GetInstance(Index: integer) : TInstance;
  begin

    if 
    GetInstance := Instances[Index];

  end;

end.

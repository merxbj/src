unit CsvParser;

interface

uses
    Classes, StrUtils, SysUtils;

type

    PObject = ^TObject;

    // class representing a single row from CSV
    TDataRow = class
        public
            // ctor/dtor
            constructor Create;
            destructor Destroy; override;

            // interface methods
            procedure AddColumn(ColumnValue: String);
            function GetColumnCount() : Integer;
            function GetColumnValue(Index: Integer) : String;
        protected
            DataColumns: TStringList;
    end;
    PDataRow = ^TDataRow;

    // class representing a whole CSV
    TDataTable = class
        public
            // ctor/dtor
            constructor Create; overload;
            destructor Destroy; override;

            // interface methods
            function GetRowCount() : Integer;
            function GetRow(Index: Integer) : PDataRow;
            procedure AddRow(DataRow: PDataRow);
        protected
            DataTable: TList;
    end;
    PDataTable = ^TDataTable;

    // class providing a convenient interface for CSV parsing
    TCsvParser = class
        public
            // ctor/dtor
            constructor Create(NewRowDelimiter: String; NewColumnDelimiter: String);

            // interface methods
            function Parse(FilePath: String; DataTableToFill: PDataTable) : boolean;

        protected
            // private members
            RowDelimiter: String;
            ColumnDelimiter: String;
            
            // private methods
            function ReadRawRow(var CsvFile: TextFile) : String;
            function ParseLine(CsvLine: String) : PDataRow;
            function HasEndOfRow(RawRow: String) : boolean;
            function HasEndOfColumn(CsvLine: String) : boolean;
    end;

implementation

    constructor TCsvParser.Create(NewRowDelimiter: String; NewColumnDelimiter: String);
    begin
        RowDelimiter := NewRowDelimiter;
        ColumnDelimiter := NewColumnDelimiter;
    end;

    function TCsvParser.Parse(FilePath: String; DataTableToFill: PDataTable) : boolean;
    var
        CsvFile: TextFile;
        Line: String;
    begin
        AssignFile(CsvFile, FilePath);
        Reset(CsvFile);

        while not EOF(CsvFile) do
        begin
            Line := ReadRawRow(CsvFile);
            DataTableToFill.AddRow(ParseLine(Line));
        end;
        Parse := true;
    end;

    function TCsvParser.ReadRawRow(var CsvFile: TextFile) : String;
    var
        SingleChar: Char;
        RawRow: String;
    begin
        while not EOF(CsvFile) and not HasEndOfRow(RawRow) do
        begin
            Read(CsvFile, SingleChar);
            RawRow := RawRow + SingleChar;
        end;
        ReadRawRow := Copy(RawRow, 1, Length(RawRow) - Length(RowDelimiter));
    end;

    function TCsvParser.HasEndOfRow(RawRow: String) : boolean;
    begin
        HasEndOfRow := (Pos(RowDelimiter, RawRow) > 0);
    end;

    function TCsvParser.HasEndOfColumn(CsvLine: String) : boolean;
    begin
        HasEndOfColumn := (Pos(ColumnDelimiter, CsvLine) > 0);
    end;

    function TCsvParser.ParseLine(CsvLine: String) : PDataRow;
    var
        DataRow: TDataRow;
        i: Integer;
        TmpColumn: String;
    begin
        DataRow := TDataRow.Create;

        for i := 1 to Length(CsvLine) do
        begin
            TmpColumn := TmpColumn + CsvLine[i];
            if (HasEndOfColumn(TmpColumn)) then
            begin
                DataRow.AddColumn(Copy(TmpColumn, 1, Length(TmpColumn) - Length(ColumnDelimiter)));
                TmpColumn := '';
            end;
        end;

        if (Length(TmpColumn) > 0) then
            DataRow.AddColumn(TmpColumn);

        ParseLine := @DataRow;
    end;

    constructor TDataTable.Create;
    begin
        inherited;

        DataTable := TList.Create;
    end;

    destructor TDataTable.Destroy;
    var
        Row: TDataRow;
        i: Integer;
    begin
        for i := 0 to DataTable.Count - 1 do
        begin
            Row := TDataRow(DataTable.Items[i]);
            Row.Free;
        end;
        DataTable.Free;

        inherited;
    end;

    function TDataTable.GetRowCount() : Integer;
    begin
        GetRowCount := DataTable.Count;
    end;

    function TDataTable.GetRow(Index: Integer) : PDataRow;
    var
        Row: TDataRow;
    begin
        Row := TDataRow(DataTable.Items[Index]);
        GetRow := @Row;
    end;

    procedure TDataTable.AddRow(DataRow: PDataRow);
    begin
        DataTable.Add(DataRow^);
    end;

    constructor TDataRow.Create;
    begin
        inherited;

        DataColumns := TStringList.Create;
    end;

    destructor TDataRow.Destroy;
    begin
        DataColumns.Free;
    end;

    function TDataRow.GetColumnCount() : Integer;
    begin
        GetColumnCount := DataColumns.Count;
    end;

    function TDataRow.GetColumnValue(Index: Integer) : String;
    begin
        if (Index >= GetColumnCount) then
            GetColumnValue := '<<< INDEX OF OUT BOUNDS >>>'
        else
            GetColumnValue := DataColumns.Strings[Index];
    end;

    procedure TDataRow.AddColumn(ColumnValue: String);
    begin
        DataColumns.Add(ColumnValue);
    end;

end.


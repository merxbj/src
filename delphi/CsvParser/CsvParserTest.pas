unit CsvParserTest;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, CsvParser;

type
    TForm1 = class(TForm)
        Edit1: TEdit;
        Button1: TButton;
        ListBox1: TListBox;
        ListBox2: TListBox;
        ListBox3: TListBox;
    Button2: TButton;
    Button3: TButton;
    procedure Button1Click(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    private
        procedure ShowData;
    public
        { Public declarations }
    end;

var
    Form1: TForm1;
    DataTable: TDataTable;
    ViewPosition: Integer;

const
    RowDelimiter = Chr(13) + Chr(10);
    ColumnDelimiter = ';';

implementation

{$R *.dfm}

procedure TForm1.Button1Click(Sender: TObject);
var
    Parser: TCsvParser;
begin

    DataTable.Free;
    DataTable := TDataTable.Create;

    Parser := TCsvParser.Create(RowDelimiter, ColumnDelimiter);
    Parser.Parse(Edit1.Text, @DataTable);

    ShowData;
end;

procedure TForm1.ShowData;
var
    i: Integer;
begin

    ListBox1.Items.Clear;
    ListBox2.Items.Clear;
    ListBox3.Items.Clear;

    for i := 0 to DataTable.GetRowCount - 1 do
    begin
        ListBox1.Items.Add(DataTable.GetRow(i).GetColumnValue(ViewPosition));
        ListBox2.Items.Add(DataTable.GetRow(i).GetColumnValue(ViewPosition + 1));
        ListBox3.Items.Add(DataTable.GetRow(i).GetColumnValue(ViewPosition + 2));
    end;

end;

procedure TForm1.FormCreate(Sender: TObject);
begin
    ViewPosition := 0;
end;

procedure TForm1.Button2Click(Sender: TObject);
begin
    ViewPosition := ViewPosition - 1;
    ShowData;
end;

procedure TForm1.Button3Click(Sender: TObject);
begin
    ViewPosition := ViewPosition + 1;
    ShowData;
end;

end.

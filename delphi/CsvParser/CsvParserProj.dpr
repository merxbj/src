program CsvParserProj;

uses
  Forms,
  CsvParserTest in 'CsvParserTest.pas' {Form1},
  CsvParser in 'CsvParser.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.

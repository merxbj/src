program Sklad;

uses
  Forms,
  prevod_Gronsky in 'prevod_Gronsky.pas' {Form1};

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.

program Ontology;

uses
  Forms,
  AccOntology in 'AccOntology.pas' {Form1},
  OntoCore in 'OntoCore.pas',
  OntoUtils in 'OntoUtils.pas',
  OntoInstances in 'OntoInstances.pas',
  OntoVocabulary in 'OntoVocabulary.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.

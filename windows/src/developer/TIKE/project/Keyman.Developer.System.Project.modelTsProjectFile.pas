(*
  Name:             Keyman.Developer.System.Project.tsProjectFile
  Copyright:        Copyright (C) SIL International.
  Documentation:
  Description:
  Create Date:      1 Aug 2006

  Modified Date:    24 Aug 2015
  Authors:          mcdurdin
  Related Files:
  Dependencies:

  Bugs:
  Todo:
  Notes:
  History:
*)
unit Keyman.Developer.System.Project.modelTsProjectFile;  // I3306   // I4687   // I4688   // I4692

interface

uses
  System.SysUtils,
  Xml.XMLIntf,

  Keyman.Developer.System.Project.ProjectFile,
  Keyman.Developer.System.Project.ProjectFiles,
  Keyman.Developer.System.Project.ProjectFileType,
  UKeymanTargets;

type
  TmodelTsProjectFile = class;

  TmodelTsProjectFile = class(TOpenableProjectFile)
  private
    FDebug: Boolean;
    FWarnAsError: Boolean;   // I4706

    function GetTargetFilename: string;
  protected
    function GetRelativeOrder: Integer; override;
    procedure GetFileParameters; override;

    property IsDebug: Boolean read FDebug;
  public
    procedure Load(node: IXMLNode; LoadState: Boolean); override;   // I4698
    procedure Save(node: IXMLNode; SaveState: Boolean); override;   // I4698
    procedure LoadState(node: IXMLNode); override;   // I4698
    procedure SaveState(node: IXMLNode); override;   // I4698

    property Debug: Boolean read FDebug write FDebug;

    property WarnAsError: Boolean read FWarnAsError write FWarnAsError;   // I4706

//    property OutputFilename: string read GetOutputFilename;
    property TargetFilename: string read GetTargetFilename;
  end;

implementation

uses
  System.Classes,
  System.Variants,
  Winapi.Windows,

  utilsystem;

{-------------------------------------------------------------------------------
 - TmodelTsProjectFile                                                             -
 -------------------------------------------------------------------------------}

procedure TmodelTsProjectFile.Save(node: IXMLNode; SaveState: Boolean);   // I4698
begin
  inherited Save(node, SaveState);   // I4698
  if SaveState then
    Self.SaveState(node);   // I4698
end;

procedure TmodelTsProjectFile.SaveState(node: IXMLNode);   // I4698
begin
  inherited SaveState(node);
  node.AddChild('Debug').NodeValue := FDebug;
end;

procedure TmodelTsProjectFile.Load(node: IXMLNode; LoadState: Boolean);   // I4698
begin
  inherited Load(node, LoadState);

  if LoadState then
    Self.LoadState(node);
end;

procedure TmodelTsProjectFile.LoadState(node: IXMLNode);   // I4698
begin
  inherited LoadState(node);
  try
    if node.ChildNodes.IndexOf('Debug') >= 0 then FDebug := node.ChildValues['Debug'];
  except
    FDebug := False;
  end;
end;

function TmodelTsProjectFile.GetRelativeOrder: Integer;
begin
  Result := 20;
end;

function TmodelTsProjectFile.GetTargetFilename: string;
var
  OutputFileName, FTempFileVersion: string;
begin
  OutputFileName := ChangeFileExt(FileName, '.js');
  // https://github.com/keymanapp/keyman/issues/631
  // This appears to be a Delphi compiler bug (RSP-20457)
  // Workaround is to make a copy of the parameter locally
  // which fixes the reference counting.
  FTempFileVersion := FileVersion;
  Result := OwnerProject.GetTargetFilename(OutputFileName, FileName, FTempFileVersion);
end;

procedure TmodelTsProjectFile.GetFileParameters;
begin
  SetFileVersion('1.0');   // I4701
  // TODO: Consider adding external references, e.g. model.tsv etc
end;

initialization
  RegisterProjectFileType('.model.ts', TmodelTsProjectFile);
end.

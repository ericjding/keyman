unit Keyman.System.BuildISO6393Registry;

interface

type
  TBuildISO6393Registry = class
    class procedure Build(const ISO6393File, DestinationFile: string);
  end;

implementation

uses
  System.SysUtils,
  System.Classes;

class procedure TBuildISO6393Registry.Build(const ISO6393File, DestinationFile: string);
var
  FISO6393, FResult: TStringList;
  i: Integer;
  s: string;
  s6393: string;
  s6391: string;

  function GetField(var s: string): string;
  var
    i: Integer;
  begin
    i := 1;
    while i <= Length(s) do
    begin
      if s[i] = #9 then
      begin
        Result := Copy(s, 1, i-1);
        Delete(s, 1, i);
        Exit;
      end;
      Inc(i);
    end;
    Result := s;
    s := '';
  end;
begin
  FISO6393 := TStringList.Create;
  try
    FISO6393.LoadfromFile(ISO6393File);
    FResult := TStringList.Create;
    try

      FResult.Add('unit Keyman.System.Standards.ISO6393ToBCP47Registry;');
      FResult.Add('');
      FResult.Add('interface');
      FResult.Add('');
      FResult.Add('// File-Date: '+FormatDateTime('yyyy-mm-dd hh:nn:ss', Now));
      FResult.Add('// Extracted from http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry');
      FResult.Add('// Generated by build_standards_data');
      FResult.Add('');
      FResult.Add('uses');
      FResult.Add('  System.Generics.Collections;');
      FResult.Add('');
      FResult.Add('type');
      FResult.Add('  TISO6393ToBCP47Map = class');
      FResult.Add('  public');
      FResult.Add('    class procedure Fill(dict: TDictionary<string,string>);');
      FResult.Add('  end;');
      FResult.Add('');
      FResult.Add('implementation');
      FResult.Add('');
      FResult.Add('{ TISO6393ToBCP47Map }');
      FResult.Add('');
      FResult.Add('class procedure TISO6393ToBCP47Map.Fill(dict: TDictionary<string, string>);');
      FResult.Add('begin');

      // Ignore first line which is a header
      for i := 1 to FISO6393.Count - 1 do
      begin
        s := FISO6393[i];
        s6393 := GetField(s); // ID
        GetField(s); // Part2B
        GetField(s); // Part2T
        s6391 := GetField(s); // Part1
        if s6391 <> '' then
          FResult.Add('  dict.Add('''+s6393+''','''+s6391+''');');
      end;

      FResult.Add('end;');
      FResult.Add('');
      FResult.Add('end.');

      FResult.SaveToFile(DestinationFile);
    finally
      FResult.Free;
    end;
  finally
    FISO6393.Free;
  end;
end;

end.
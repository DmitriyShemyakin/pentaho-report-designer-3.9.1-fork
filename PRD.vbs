' Launches Report Designer without a console window (javaw + hidden Run).
Option Explicit

Dim fso, sh, base, forkPath, flatPath, libPath, javaw, cmd

Set fso = CreateObject("Scripting.FileSystemObject")
Set sh = CreateObject("WScript.Shell")

base = fso.GetParentFolderName(WScript.ScriptFullName)
forkPath = fso.BuildPath(base, "target\prd-ui-fork-3.9.1-fork-1.jar")
flatPath = fso.BuildPath(base, "target\patch-lib\flatlaf.jar")
libPath = fso.BuildPath(base, "runtime\prd-ce-3.9.1-GA\lib")

If Not fso.FileExists(forkPath) Then
  MsgBox "Missing build JAR:" & vbCrLf & forkPath & vbCrLf & vbCrLf & "Run: mvn package", vbCritical, "PRD fork"
  WScript.Quit 1
End If
If Not fso.FileExists(flatPath) Then
  MsgBox "Missing FlatLaf:" & vbCrLf & flatPath & vbCrLf & vbCrLf & "Run: mvn package", vbCritical, "PRD fork"
  WScript.Quit 1
End If
If Not fso.FolderExists(libPath) Then
  MsgBox "Missing PRD lib folder:" & vbCrLf & libPath & vbCrLf & vbCrLf & "Run once: tools\setup-prd-runtime.ps1", vbCritical, "PRD fork"
  WScript.Quit 1
End If

javaw = ""
If Len(sh.ExpandEnvironmentStrings("%JAVA_HOME%")) > 0 Then
  Dim jh
  jh = sh.ExpandEnvironmentStrings("%JAVA_HOME%")
  If Right(jh, 1) = "\" Then jh = Left(jh, Len(jh) - 1)
  If fso.FileExists(fso.BuildPath(jh, "bin\javaw.exe")) Then
    javaw = fso.BuildPath(jh, "bin\javaw.exe")
  End If
End If
If Len(javaw) = 0 Then
  javaw = "javaw"
End If

Dim cp
cp = forkPath & ";" & flatPath & ";" & libPath & "\*"

cmd = Q(javaw) & " --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED" & _
      " --add-opens java.desktop/sun.awt=ALL-UNNAMED -cp " & Q(cp) & _
      " org.pentaho.reporting.designer.core.ReportDesigner"

Dim i
For i = 0 To WScript.Arguments.Count - 1
  cmd = cmd & " " & Q(WScript.Arguments(i))
Next

' 0 = hidden window; False = do not wait for PRD to exit
sh.Run cmd, 0, False
WScript.Quit 0

Function Q(s)
  Q = Chr(34) & Replace(s, Chr(34), Chr(34) & Chr(34)) & Chr(34)
End Function

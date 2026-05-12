@echo off
rem No console for the app: delegates to PRD.vbs (javaw). A cmd window may flash once; use PRD.vbs for zero console.
wscript //nologo "%~dp0PRD.vbs" %*
exit /b 0

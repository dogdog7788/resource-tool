@setlocal enableextensions enabledelayedexpansion
@set classpath=.
@for %%c in (./lib/*.jar) do @set classpath=!classpath!;./lib/%%c
@echo %classpath%
@set classpath=%classpath%;
@echo %classpath%
java -ms256m -mx4000m -classpath %classpath% com.sr178.game.tool.ExcelToTransferTemp
@pause
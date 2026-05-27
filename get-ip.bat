@echo off
echo 当前网络配置信息:
echo ====================
ipconfig | findstr IPv4
echo.
echo 请记录上述IP地址，然后在手机浏览器中访问:
echo http://[上面的IP地址]:5173
echo.
echo 按任意键关闭此窗口...
pause >nul
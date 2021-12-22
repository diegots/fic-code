#!/usr/bin/env /usr/bin/python3
# -*- coding: UTF-8 -*-# enable debugging
import cgitb
import cgi
import sys
cgitb.enable()
print("Content-Type: text/html;charset=utf-8")
print()
data = sys.stdin.read()

#
# Feed path has to include IMEI of device
#
startIndex = data.find("<title>") + 7
endIndex = data.find("</title>") 
imei = data[startIndex : endIndex]
destPath = "/var/www/feed/" + imei + ".xml"

#
# Writing received data to destPath
#
fh = open(destPath, "w")
fh.write(data)
fh.close()

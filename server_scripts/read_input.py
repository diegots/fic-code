#!/usr/bin/env /usr/bin/python3
# -*- coding: UTF-8 -*-# enable debugging
import cgitb
import cgi
import sys
cgitb.enable()
print("Content-Type: text/html;charset=utf-8")
print()
data = sys.stdin.read()
fh = open("/var/www/write/request.txt", "w")
fh.write(data)
fh.close()

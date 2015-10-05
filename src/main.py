#!/usr/bin/python3

import controller.controller

tag = "main.py : "

#class Main:
#    def __init__(self):
#        print(tag + "Testing from main executable")

if __name__ == "__main__":
    print(tag + "Launching Controller")
    controller.controller.Controller()

#!/usr/bin/python3

import sys
import os

from gi.repository import Gtk

src_path = os.path.dirname(os.path.dirname(os.path.realpath("controller.py")))
sys.path.append(src_path)
import view.mainWindow
import view.aboutWindow


tag = "controller.py : "


class Controller:
    def __init__(self):
        print(tag + "Controller contructor")
        self.lauch_widgets()

    #
    # Launch widgets
    #
    def lauch_widgets(self):
        print(tag + "Launching widgets") 
        view.mainWindow.MainWindow()
        Gtk.main()

# 
# import sys
# import os
# import view
# 
# 
# tag = "main.py : "
# 
# 
# #
# # Pring some help in the std input
# #
# def show_help():
#     s = '''Wrong number of parameters or misspelled parameter name.
# Accepted parameters:
#   --config path_to_config_file'''
#     print (s)
# 
# 
# #
# # Launch views
# #
# def lauch_program():
#     print(tag + "Should launch main program now...") # XXX
#     View.main()
# 
# 
# #
# # Check if given config file path is a file
# #
# def valid_config_file(path):
#     p = os.path.realpath(path)
#     if os.path.isfile(p):
#         return True
#     else:
#         return False
# 
# 
# #
# # Default config file path, used if not tell otherwise
# #
# def build_default_file_config_path():
#     p = os.path.split(main_executable)[0]
#     p = os.path.split(p)[0]
#     p = os.path.join(p, "json/db_books.json")
#     return (p)
# 
# #
# # Check number of arguments and select required config file
# #
# def check_arguments():
#     if l == 1: # No arguments, use default config file
#         p = build_default_file_config_path()
#         print (tag + "Using default config file at: " + p)
#         lauch_program()
#     
#     elif l == 3: # 2 arguments, config flag and path
#         if sys.argv[1] == "--config":
#             print (tag + "Checking if " + sys.argv[2] + " is a valid config file")
#             if valid_config_file(sys.argv[2]): # Using given config file
#                 print(tag + "Config file accepted")
#                 print("Lauch program!!!!!") # XXX
#             else: # Wrong config file
#                  s = ("Config file is not valid, please, check it's syntax and try"
#                      " again")
#                  print(s)
#             
#         else: # Wrong syntax: other flag than --config
#             show_help()
#     
#     else: # Wrong syntax: other number of arguments than 0 or 2
#         show_help()
# # Get main executable path
# main_executable = os.path.abspath(sys.argv[0])
# 
# 
# # Get number of arguments passed
# l = len(sys.argv)
# 
# check_arguments()
# 

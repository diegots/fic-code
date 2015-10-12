# -*- coding: utf-8 -*-

import os.path, sys

src_path = os.path.dirname(os.path.dirname(os.path.realpath("controller.py")))
sys.path.append(src_path)
import view.mainWindow
import model.model

tag = "controller.py  : "


class Controller:
    
    def __init__(self):
        print(tag + "Controller contructor")
        self.model = model.model.Model()
        self.data = self.model.loadJsonFile()
        self.launchWidgets()


    # Launch widgets sending controller as a parameter
    def launchWidgets(self):
        print(tag + "Launching widgets") 
        view.mainWindow.MainWindow(self)
        print(tag + "Graphic library main loop ended. Nothing left to do, finishing up!")

    # action: what pagination button was pressed 
    def pageAction(self, action):
        print (tag + "pageAction " + action)

    def uploadAction(self):
        print (tag + "uploadAction")

    #
    # Services available to the view module
    #

    # requestData send current available data to the view module
    def requestData(self, view):
        print(tag + "requestData: sending data to view widget")
        view.populateDataWiget(self.data)

    def doSearch(self, view, keywords, exactMatch, caseSensitive, field):
        print(tag + "doSearch")
        new_data = self.model.doSearch(keywords, exactMatch, caseSensitive, field)
        view.populateDataWiget(new_data)
        
    def doUpload(self, book_data):
        self.model.upload(book_data)
        

class ConfigFile:

    #
    # Default config file path, used if not tell otherwise
    #
    def build_default_file_config_path():
        p = os.path.split(main_executable)[0]
        p = os.path.split(p)[0]
        p = os.path.join(p, "json/db_books.json")
        return (p)

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

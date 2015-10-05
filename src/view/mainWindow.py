#!/usr/bin/python3

from gi.repository import Gtk, Gdk, GLib, GObject

import view.aboutWindow

tag = "mainWindow.py : "

#
# View is the main class that hosts all the main window widgets
#
class MainWindow:
    def __init__(self):
        print(tag + "Constructor init")
        self.builder = Gtk.Builder()
        # Añadir la vista
        self.builder.add_from_file("view/interface.glade")  
        # Añadir ños elementos que nos interesan 
        self.principal_window = self.builder.get_object("window1")
        # Conectar a eventos
        self.builder.connect_signals(self)  # conectar eventos        
        self.principal_window.connect("delete-event", Gtk.main_quit)

        self.cargar_elementos()
        self.asignar_eventos()

        # mostrar la ventana principal
        self.principal_window.show()

        
    # Widget's association
    def cargar_elementos(self):
        print(tag + "cargar_elementos")
        self.taskList      = self.builder.get_object("liststore1")
        self.treeView      = self.builder.get_object("treeview2")
        self.firstBtn      = self.builder.get_object("button1")
        self.previousBtn   = self.builder.get_object("button2")
        self.nextBtn       = self.builder.get_object("button3")
        self.lastBtn       = self.builder.get_object("button4")
        self.uploadBtn     = self.builder.get_object("button6")
        self.aboutBtn      = self.builder.get_object("button5")
        self.comboBox      = self.builder.get_object("comboboxtext1")
        self.exactCheckBox = self.builder.get_object("checkbutton1")
        self.caseCheckBox  = self.builder.get_object("checkbutton2")
        self.searchEntry   = self.builder.get_object("searchentry1")
        

    # Link signals with handlers
    def asignar_eventos(self):
        print(tag + "asignar_eventos")

        self.firstBtn.connect      ("clicked",        self.on_first)
        self.previousBtn.connect   ("clicked",        self.on_previous)
        self.nextBtn.connect       ("clicked",        self.on_next)
        self.lastBtn.connect       ("clicked",        self.on_last)
        self.uploadBtn.connect     ("clicked",        self.on_upload)
        self.aboutBtn.connect      ("clicked",        self.on_acercaDe)
        self.comboBox.connect      ("changed",        self.on_search)
        self.exactCheckBox.connect ("toggled",        self.on_search)
        self.caseCheckBox.connect  ("toggled",        self.on_search)
        self.searchEntry.connect   ("search-changed", self.on_search)


    # Handlers
    def on_first(self,w):
        print(tag + "on_first")

    def on_previous(self,w):
        print(tag + "on_previous")

    def on_next(self,w):
        print(tag + "on_next")

    def on_last(self,w):
        print(tag + "on_last")

    def on_upload(self, w):
        print(tag + "on_upload")

    def on_acercaDe(self, w):
        print(tag + "on_acercaDe")
        view.aboutWindow.AboutWindow()
            
    def on_search(self, w):
        print(tag + "on_search")

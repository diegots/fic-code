#!/usr/bin/python3


from gi.repository import Gtk, Gdk, GLib, GObject


#
# View is the main class that hosts all the app widgets
#
class MainWindow:
    def __init__(self):
        self.builder = Gtk.Builder()
        # Añadir la vista
        self.builder.add_from_file("interface.glade")  
        # Añadir ños elementos que nos interesan 
        self.principal_w = builder.get_object("window1")
        # Conectar a eventos
        self.builder.connect_signals(self)  # conectar eventos        
        self.principal_w.connect("delete-event", Gtk.main_quit)
        # mostrar la ventana principal
        self.principal_w.show()
        
    def cargar_elementos(self):
        self.taskList      = self.builder.get_object("liststore1")
        self.treeView      = self.builder.get_object("treeview2")
        self.firstBtn      = self.builder.get_object("button1")
        self.previousBtn   = self.builder.get_object("button2")
        self.nextBtn       = self.builder.get_object("button3")
        self.lastBtn       = self.builder.get_object("button4")
        self.uploadBtn     = self.builder.get_object("button6")
        self.aboutBtn      = self.builder.get_object("button5")
        self.combobox      = self.builder.get_object("comboboxtext1")
        self.exactCheckBox = self.builder.get_object("checkbutton1")
        self.caseCheckBox  = self.builder.get_object("checkbutton2")
        self.searchEntry   = self.builder.get_object("searchentry1")
        
    def asignar_eventos(self):
        select = self.treeview.get_selection()
        select.connect("changed", self.on_tree_selection_changed)

    
    def on_tree_selection_changed(self,w): 
        selection = self.treeview.get_selection()
        selection.set_mode(Gtk.SelectionMode.SINGLE) 
        self.model, self.tree_iter =  selection.get_selected()
        self.rowSelected = True
            
            
    # Eventos
    #def on_load(self):
    
    #def on_rowSelected(self, w):        
        

    #def on_upload(self, w):
        #MVCModel.MVCModel.uploadBook(self, book_data, file_path, security_token)
        
    #def on_Advancedsearch(self,w):
        
        
    # def on_first(self,w): # XXX
    
    # def on_previous(self,w): # XXX
        
    # def on_next(self,w): # XXX
    
    # def on_last(self,w): # XXX
        
    
    #Update a view
    
    # def update_list(self): # XXX
        
    # def setVisible_uploadBtn(self): # XXX
        
    
        
    # Message Dialog    
    def error_dialog(self, message):
        # gtk.MessageDialog(parent=None, flags=0, type=gtk.MESSAGE_INFO, buttons=gtk.BUTTONS_NONE, message_format=None)
        dialog = Gtk.MessageDialog(None, 0, Gtk.MessageType.ERROR, Gtk.ButtonsType.OK, None)
        dialog.format_secondary_text(message)
        dialog.run()
        dialog.hide()
        
    def warning_dialog(self, message):
        dialog = Gtk.MessageDialog(None, 0, Gtk.MessageType.WARNING, Gtk.ButtonsType.OK_CANCEL, None)
        dialog.format_secondary_text(message) 
        response = dialog.run()
        r = True
        if response == Gtk.ResponseType.OK:
            r = True
        elif response == Gtk.ResponseType.CANCEL:
            r = False
        dialog.hide()
        return r
        
    def question_dialog(self, message):
        dialog = Gtk.MessageDialog(None, 0, Gtk.MessageType.QUESTION, Gtk.ButtonsType.YES_NO, None)
        dialog.format_secondary_text(message)
        response = dialog.run()
        r = True
        if response == Gtk.ResponseType.YES:
            r = True
        elif response == Gtk.ResponseType.NO:
            r = False
        dialog.hide()
        return r

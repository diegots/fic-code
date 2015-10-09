from gi.repository import Gtk, Gdk, GLib, GObject

import view.aboutWindow
import view.dialog

tag = "mainWindow.py : "

#
# View is the main class that hosts all the main window widgets
#
class MainWindow:
    def __init__(self, controller):
        print(tag + "Constructor init")

        self.controller = controller

        self.builder = Gtk.Builder()
        # Read widgets from XML file
        self.builder.add_from_file("view/interface.glade")  # XXX fix path build

        # Connect to events
        self.builder.connect_signals(self)  # conectar eventos        

        self.cargar_elementos()
        self.asignar_eventos()

        self.prepareTreeView()
        self.loadListStore(self)

        # Show main windows and enter Gtk events loop
        self.principal_window.show()
        Gtk.main()

    #
    # Init stuff
    #
        
    # Widget's association
    def cargar_elementos(self):
        print(tag + "cargar_elementos")
        self.principal_window = self.builder.get_object("window1")
        self.listStore = self.builder.get_object("liststore1")
        self.treeView = self.builder.get_object("treeview2")
        self.treeTitleCol = self.builder.get_object("treeviewcolumn1")
        self.treeTitleAuth = self.builder.get_object("treeviewcolumn2")
        self.uploadBtn = self.builder.get_object("button6")
        self.aboutBtn = self.builder.get_object("button5")
        self.comboBox = self.builder.get_object("comboboxtext1")
        self.exactCheckBox = self.builder.get_object("checkbutton1")
        self.caseCheckBox = self.builder.get_object("checkbutton2")
        self.searchEntry = self.builder.get_object("searchentry1")
        self.progressBar = self.builder.get_object("progressbar1")
        self.treeSelection = self.builder.get_object("treeview-selection10")

    # Link signals with handlers
    def asignar_eventos(self):
        print(tag + "asignar_eventos")

        self.principal_window.connect("delete-event", self.on_main_quit)
        self.uploadBtn.connect       ("clicked", self.on_upload)
        self.aboutBtn.connect        ("clicked", self.on_acercaDe)
        self.comboBox.connect        ("changed", self.on_search)
        self.exactCheckBox.connect   ("toggled", self.on_search)
        self.caseCheckBox.connect    ("toggled", self.on_search)
        self.searchEntry.connect     ("search-changed", self.on_search)


    #
    # Event handlers
    #
    def on_first(self, w):
        print(tag + "on_first")
        self.controller.pageAction("first")

    def on_previous(self, w):
        print(tag + "on_previous")
        self.controller.pageAction("previous")

    def on_next(self, w):
        print(tag + "on_next")
        self.controller.pageAction("next")

    def on_last(self, w):
        print(tag + "on_last")
        self.controller.pageAction("last")

    def on_upload(self, w):
        print(tag + "on_upload")

        # Get selected items
        model, pathList = self.treeSelection.get_selected_rows()

        data = []

        columns = self.treeView.get_columns()
        colNames = []
        for col in columns:
            colNames.append(col.get_title())

        for path in pathList:
            treeIter = model.get_iter(path) 

            colValue0 = model.get_value(treeIter, 0) 
            colName0 = colNames[0]          
            colValue1 = model.get_value(treeIter, 1) 
            colName1 = colNames[1]          

            selectedDict = {}
            selectedDict.update({colNames[0]: colValue0})
            selectedDict.update({colNames[1]: colValue1})

            data.append(selectedDict)
            
        self.controller.doUpload(data)

    def on_acercaDe(self, w):
        print(tag + "on_acercaDe")
        view.aboutWindow.AboutWindow()
            
    def on_search(self, w):
        print(tag + "on_search")

        keywords = self.searchEntry.get_text()
        field = self.comboBox.get_active_text()
        case = self.caseCheckBox.get_active()
        exact = self.exactCheckBox.get_active()
  
        self.controller.doSearch(self, keywords, exact, case, field)        

    def on_main_quit(self, widget, event, donnees=None):
        print("destroy signal occurred")
        dialog = view.dialog.MessageDialog()
        message = "Are you sure to close the Library Managenment?"
        respuesta = dialog.question_dialog(self.principal_window, message)
        if not respuesta:
            return True
        else:
            Gtk.main_quit()
            return False
        
    # 
    # Private functions
    #
    def loadListStore(self, w):
        print(tag + "loadListStore: requesting data from the controller")
        self.controller.requestData(self)

    # Prepare the treeView to host info
    def prepareTreeView(self):
        print(tag + "prepareTreeView")

        # Renderer for the title column
        rendererTitle = Gtk.CellRendererText()
        # This is the title column
        treeTitleCol = Gtk.TreeViewColumn('Title', rendererTitle, text=0)
        # Make the column sortable
        treeTitleCol.set_sort_column_id(0)
        # Make the column user resizable
        treeTitleCol.set_resizable(True)
        # Adding the column to the treeView
        self.treeView.append_column(treeTitleCol)

        # Renderer for the author column
        rendererAuthor = Gtk.CellRendererText()
        # This is the author column
        treeAuthorCol = Gtk.TreeViewColumn('Author', rendererAuthor, text=1)
        # Make the column sortable
        treeAuthorCol.set_sort_column_id(1)
        # Adding the column to the treeView
        self.treeView.append_column(treeAuthorCol)


    #
    # Services avaibable to the controller module
    #
    def populateDataWiget(self, data):
        print(tag + "populateDataWiget")

        # Make sure listStore is empty before adding new data
        self.listStore.clear()

        for v in data:
            self.listStore.append([v["title"], v["author"]])

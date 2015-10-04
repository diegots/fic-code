#!/usr/bin/python3


from gi.repository import Gtk, Gdk, GLib, GObject


#
# About Dialog that show's app info
#
class AboutWindow:
    def __init__(self, glade):
        builder = Gtk.Builder()
        glade.add_objects_from_file("interface.glade", ("dialog_about",))
        self.about= builder.get_object("aboutdialog-vbox1")
        self.about.run() 
        self.about.hide()

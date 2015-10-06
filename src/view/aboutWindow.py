from gi.repository import Gtk, Gdk, GLib, GObject

tag = "aboutWindow.py : "


#
# About Dialog that show's app info
#
class AboutWindow:
    def __init__(self):
        print(tag + "Constructor init")
        builder = Gtk.Builder()
        builder.add_from_file("view/interface.glade")

        self.about = builder.get_object("aboutdialog1")
        self.about.run()
        self.about.hide()

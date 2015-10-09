from gi.repository import Gtk

class MessageDialog:
           
        
    def question_dialog(self, widget, message):
        
        dialog = Gtk.MessageDialog(widget, 0, Gtk.MessageType.QUESTION,
            Gtk.ButtonsType.OK_CANCEL, "CLOSE")
        
        dialog.format_secondary_text(message)        
        response = dialog.run()                                          
        r = False
        if response == Gtk.ResponseType.OK:
            r = True
        dialog.destroy()   
        return r

    def info_dialog(self, widget, message):
        
        dialog = Gtk.MessageDialog(widget, 0, Gtk.MessageType.INFO,
            Gtk.ButtonsType.OK, "UPDATE")
        dialog.format_secondary_text(message)
        r = dialog.run()
        if r == Gtk.ResponseType.OK:
            dialog.destroy()
        
    
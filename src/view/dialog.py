from gi.repository import Gtk


class MessageDialog:
    
    def question_dialog(self, widget, message):
        
        dialog = Gtk.MessageDialog(widget, 0, Gtk.MessageType.QUESTION,
            Gtk.ButtonsType.OK_CANCEL, "QUESTION DIALOG")
        
        dialog.format_secondary_text(message)        
        response = dialog.run()
        r = True
        if response == Gtk.ResponseType.CANCEL:
            r = False
                
        dialog.hide()    
        return r

    def info_dialog(self, widget, message):
        
        dialog = Gtk.MessageDialog(self, 0, Gtk.MessageType.INFO,
            Gtk.ButtonsType.OK, "INFO DIALOG")
        dialog.format_secondary_text(message)
        r = dialog.run()
        if r == Gtk.ResponseType.OK:
            dialog.destroy()
        
    
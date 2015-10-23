# Learning GUI coding

from tkinter import *

#Create centered main GUI window
class MFlyGUI(Frame):
  
    def __init__(self, parent):
        Frame.__init__(self, parent, background="blue")   
         
        self.parent = parent
        self.parent.title("M-Fly Ground Station")
        self.pack(fill=BOTH, expand=1)
        self.centerWindow()

    def centerWindow(self):
      
        w = 600
        h = 400

        sw = self.parent.winfo_screenwidth()
        sh = self.parent.winfo_screenheight()
        
        x = (sw - w)/2
        y = (sh - h)/2
        self.parent.geometry('%dx%d+%d+%d' % (w, h, x, y))


    # Trying to figure out how to add buttons...not working yet
    def initUI(self):
      
        self.parent.title("Quit button")
        self.style = Style()
        self.style.theme_use("default")

        self.pack(fill=BOTH, expand=1)

        quitButton = Button(self, text="Quit",
            command=self.quit)
        quitButton.place(x=50, y=50)

def main():
  
    root = Tk()
    ex = MFlyGUI(root)
    app = MFlyGUI(root)
    root.mainloop()  

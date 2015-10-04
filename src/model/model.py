#!/usr/bin/python3

import json
import os.path

class model():
    def loadFile(self):

        # Path to the json db file
#        json_db_path = os.path.abspath("json/db_books.json")
        abs_path = os.path.basename("model.py")
        abs_path = os.path.abspath(abs_path)
        print (abs_path)
        json_db_path = ""
#        print (json_db_path)
	

        if os.path.exists(json_db_path):
            # open() arguments other that filename are not mandatory
            f = open(json_db_path, "r", encoding="utf8") 

            d = f.read() # returns an io.TextIOWrapper object
            self.json_data = json.loads(d)
            return self.json_data

        else:
            # print("File", os.path.basename(json_db_path), "not found on:", json_db_path)
            return []


m = model()
print (m.loadFile())
# print (type (m.loadFile))


# keywords: user inserted keywords
# fieldType: 0 performs an AND search, 1 author, 2 for title
# exactMatch: true or false
# isCapital: true or false
# def search(self, keywords, fieldType, exactMatch, isCapital):

import json
import os.path
import time
import random

tag = "model.py : "


class Model:

#    def __init__(self):
#        return self

    def loadJsonFile(self):

        # Path to the json db file
        path = os.path.realpath("model.py")
        path = os.path.dirname(path)
        path = os.path.dirname(path)
        path = os.path.join(path, "json")
        path = os.path.join(path, "db_books.json")
        print (tag + "loading JSON from: " + path)
        
        if os.path.exists(path):
            # open() arguments other that filename are not mandatory
            f = open(path, "r", encoding="utf8") 

            d = f.read()  # returns an io.TextIOWrapper object
            self.json_data = json.loads(d)

            return self.json_data

        else:
            raise FileNotFoundError(path)
    
    def upload(self, book_data):
        time.sleep(random.uniform(3,6))
    
    def doSearch(self, keywords, exactMatch, caseSensitive, field):
        print(tag + "doSearch")
        new_dict = []

        # Well, if no keywords were passed, just return all data
        if not len(keywords):
            return self.json_data
        
        if field == "All fields":
            print(tag + "doSearch: field == all_fields")
            new_dict = self.searchByAllFields(self.json_data, keywords, exactMatch, caseSensitive)

        elif field == "Author":
            print(tag + "doSearch: field == author")  
            new_dict = self.seachByField("author", self.json_data, keywords, exactMatch, caseSensitive) 

        elif field == "Title":
            print(tag + "doSearch: field == title")
            new_dict = self.seachByField("title", self.json_data, keywords, exactMatch, caseSensitive)

        return new_dict
    
    
    def searchByAllFields(self, data, keywords, exactMatch, caseSensitive):     
        print (tag + "searchByAllFields")
        search = []
        if exactMatch:
            if caseSensitive:
                for dicti in data:
                    if keywords == dicti.get("author") or keywords == dicti.get("title"):
                        search.append(dicti)
                        
            elif (not caseSensitive):
                for dicti in data:
                    if keywords.upper() == dicti.get("author").upper() or keywords.upper() == dicti.get("title").upper():
                            search.append(dicti)
                            
        elif (not exactMatch):
            if caseSensitive:
                for dicti in data:
                    if keywords in dicti.get("author") or keywords in dicti.get("title"):
                        search.append(dicti)
                        
            elif (not caseSensitive):
                for dicti in data:
                    if keywords.upper() in dicti.get("author").upper() or keywords.upper() in dicti.get("title").upper():
                            search.append(dicti)
                            
        return search 


    def seachByField(self, field, data, keywords, exactMatch, caseSensitive):
        print (tag + "seachByField")
        search = []
        
        if exactMatch:
            if caseSensitive:
                for dicti in data:
                    if keywords == dicti.get(field):
                        search.append(dicti)
                        
            elif (not caseSensitive):
                for dicti in data:
                    if keywords.upper() == dicti.get(field).upper():
                            search.append(dicti)
                            
        elif (not exactMatch):
            if caseSensitive:
                for dicti in data:
                    if keywords in dicti.get(field):
                        search.append(dicti)
                        
            elif (not caseSensitive):
                for dicti in data:
                    if keywords.upper() in dicti.get(field).upper():
                            search.append(dicti)

        return search                 

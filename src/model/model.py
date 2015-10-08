import json
import os.path


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

    def doSearch(self, keywords, exactMatch, caseSensitive, field):
        print(tag + "do_search")
        new_dict = []
        # Well, if no keywords were passed, just return all data
        if not len(keywords):
            return self.json_data
        
        if field == "All fields":
            print("field == all_fields")
            new_dict = self.searchAll(self.json_data, keywords, exactMatch, caseSensitive)

        elif field == "Author":
            print("field == author")  
            new_dict = self.seachAuthor(self.json_data, keywords, exactMatch, caseSensitive)

        elif field == "Title":
            print("field == title")
            new_dict = self.seachTitle(self.json_data, keywords, exactMatch, caseSensitive)

        return new_dict

    def searchAll(self, data, keywords, exactMatch, caseSensitive):     
        search = []
        for dicti in data:
            if keywords in dicti.get("author") or keywords in dicti.get("title"):
                search.append(dicti)
        print ("PRINT QUERY RESULTS")
        print (search)
        return search 


    def seachAuthor(self, data, keywords, exactMatch, caseSensitive):
        search = []
        for dicti in data: 
            if keywords in dicti.get("author"):
                search.append(dicti)
                print("Result: " + dicti.get("author"))
        print ("PRINT QUERY RESULTS")
        print (search)
        return search                 

    def seachTitle(self, data, keywords, exactMatch, caseSensitive): 
        search = []
        for dicti in data: 
            if keywords in dicti.get("title"):
                search.append(dicti)
                print("Result: " + dicti.get("title"))
        print ("PRINT QUERY RESULTS")
        print (search)
        return search                 
                

  
# keywords: user inserted keywords
# fieldType: 0 performs an AND search, 1 author, 2 for title
# exactMatch: true or false
# isCapital: true or false
# def search(self, keywords, fieldType, exactMatch, isCapital):

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

            d = f.read() # returns an io.TextIOWrapper object
            self.json_data = json.loads(d)

            return self.json_data

        else:
            raise FileNotFoundError(path)

    def doSearch(self, keywords, exactMatch, caseSensitive, field):
        print(tag + "do_search")

        new_dict = self.json_data

        # Well, if no keywords were passed, just return all data
        if not len(keywords):
            return new_dict

        for dicti in new_dict:
            if field == 0:
                print("field == all_fields")
                if keywords in dicti.get("author") or search_str in dicti.get("title"):
                    print(dicti.get(key1) + " : "+ dicti.get(key2))

            elif field == "author":
                print("field == author")  
                if keywords in dicti.get("author"):
                    print(dicti.get("author"))

            elif field == "title":
                print("field == title")
                #print(search_str + " : " + dicti.get(key1))
                if keywords in dicti.get("title"):
                    print("Result: " + dicti.get("title"))

        return new_dict

    def searchAll(keywords, exactMatch, caseSensitive):
        
        return l

  
# keywords: user inserted keywords
# fieldType: 0 performs an AND search, 1 author, 2 for title
# exactMatch: true or false
# isCapital: true or false
# def search(self, keywords, fieldType, exactMatch, isCapital):

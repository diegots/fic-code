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
            # new_dict = self.searchByAllFields(self.json_data, keywords, exactMatch, caseSensitive)
            new_dict = self.seachByField("author", self.json_data, keywords, exactMatch, caseSensitive) 
            new_dict = self.seachByField("title", new_dict, keywords, exactMatch, caseSensitive) 

        elif field == "Author":
            print("field == author")  
            new_dict = self.seachByField("author", self.json_data, keywords, exactMatch, caseSensitive) 

            print("field == title")
            new_dict = self.seachByField("title", self.json_data, keywords, exactMatch, caseSensitive)

        return new_dict
    
    
    def searchByAllFields(self, data, keywords, exactMatch, caseSensitive):     
        search = []
        for dicti in data:
            if keywords in dicti.get("author") or keywords in dicti.get("title"):
                search.append(dicti)
        print ("PRINT QUERY RESULTS")
        print (search)
        return search 


    def seachByField(self, field, data, keywords, exactMatch, caseSensitive):
        print ("Search by field")
        search = []
        if exactMatch:
            print ("EXACT MATCH")
            if caseSensitive:
                for dicti in data:
                    if keywords == dicti.get(field):
                        search.append(dicti) 
                        
            elif (not caseSensitive):
                keywords1 = keywords.upper()
                print("PALABRA 1 :", keywords)                
                for dicti in data:
                    keywords2 = dicti.get(field).upper()
                    print("PALABRA 2 :", keywords2)                    
                    if keywords1 == keywords2:
                        print("ES VERDAD PALABRA 1 Y PALABRA 2")
                        search.append(dicti)    

        elif (not exactMatch):
            print ("NOT EXACT MATCH")
            if caseSensitive:
                for dicti in data:
                    if keywords in dicti.get(field):
                        search.append(dicti)
                        
            elif (not caseSensitive):
                keywords1 = keywords.upper()
                print("PALABRA 1 :", keywords)
                for dicti in data:
                    keywords2 = dicti.get(field).upper()
                    print("PALABRA 2 :", keywords2)                    
                    if keywords1 in keywords2:
                        print("ES VERDAD PALABRA 1 Y PALABRA 2")                        
                        search.append(dicti)                
                    
                                                                        
        print ("PRINT QUERY RESULTS")
        print (search)
        return search                 

    
# 
#     def searchAll(self, data, keywords, exactMatch, caseSensitive):     
#         search = []
#         for dicti in data:
#             if keywords in dicti.get("author") or keywords in dicti.get("title"):
#                 search.append(dicti)
#         print ("PRINT QUERY RESULTS")
#         print (search)
#         return search 
# 
# 
#     def seachAuthor(self, data, keywords, exactMatch, caseSensitive):
#         search = []
#         for dicti in data: 
#             if keywords in dicti.get("author"):
#                 search.append(dicti)
#                 print("Result: " + dicti.get("author"))
#         print ("PRINT QUERY RESULTS")
#         print (search)
#         return search                 
# 
#     def seachTitle(self, data, keywords, exactMatch, caseSensitive): 
#         search = []
#         for dicti in data: 
#             if keywords in dicti.get("title"):
#                 search.append(dicti)
#                 print("Result: " + dicti.get("title"))
#         print ("PRINT QUERY RESULTS")
#         print (search)
#         return search                 
#                 

  
# keywords: user inserted keywords
# fieldType: 0 performs an AND search, 1 author, 2 for title
# exactMatch: true or false
# isCapital: true or false
# def search(self, keywords, fieldType, exactMatch, isCapital):

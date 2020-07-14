#in-memory grep implementation, filter lines starting wth filterKeyword
#return text string - memory hungry but fast
#http://stackoverflow.com/questions/10717504/is-it-possible-to-use-read-csv-to-read-only-specific-lines
def SimpleLineGrep(ASCIIfileName,filterKeyword):
    try:
        from StringIO import StringIO
    except ImportError:
        from io import StringIO
        
    s = StringIO()
    
    with open(ASCIIfileName) as f:
        for line in f:
            if line.startswith(filterKeyword):
                s.write(line)
    
    s.seek(0) # "rewind" to the beginning of the StringIO object
    return s

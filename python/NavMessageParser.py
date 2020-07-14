import pandas as pd
import numpy as np
import UserUtils as uutils

def readNavLogger (data_file):
    print("filtering PR from %s" % data_file)
    RawMeas = uutils.SimpleLineGrep(data_file,'Nav')
    
    colNames = ["Nav","Svid","Type","Status","MessageId","Sub-messageId",
        "Data1","Data2", "Data3", "Data4", "Data5", "Data6", "Data7", "Data8", "Data9", "Data10", "Data11"]
    dataFrame = pd.read_csv(RawMeas, delimiter = ",",error_bad_lines=False,header=None,
                            usecols=range(1,len(colNames)),names= colNames,
                            encoding = 'utf-8-sig',na_values = ["NULL",""], engine ='c')
    return dataFrame

def main():
    data_file = './data/s8_01.txt'
    df_Nav = readNavLogger(data_file)
    
    print(df_Nav)
    #print('Nav Svid list : ' , df_Nav.Svid.unique())

if __name__ == "__main__":
    main()

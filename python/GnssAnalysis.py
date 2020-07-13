import pandas as pd
import matplotlib.pyplot as plt
import matplotlib
matplotlib.style.use('ggplot')
import numpy as np
import datetime

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

def readGNSSLogger (data_file):
    print("filtering PR from %s" % data_file)
    RawMeas = SimpleLineGrep(data_file,'Raw')
    
    colNames = ["Raw","ElapsedRealtimeMillis","TimeNanos","LeapSecond","TimeUncertaintyNanos","FullBiasNanos","BiasNanos","BiasUncertaintyNanos","DriftNanosPerSecond","DriftUncertaintyNanosPerSecond","HardwareClockDiscontinuityCount","Svid","TimeOffsetNanos","State","ReceivedSvTimeNanos","ReceivedSvTimeUncertaintyNanos","Cn0DbHz","PseudorangeRateMetersPerSecond","PseudorangeRateUncertaintyMetersPerSecond","AccumulatedDeltaRangeState","AccumulatedDeltaRangeMeters","AccumulatedDeltaRangeUncertaintyMeters","CarrierFrequencyHz","CarrierCycles","CarrierPhase","CarrierPhaseUncertainty","MultipathIndicator","SnrInDb","ConstellationType"]  
    dataFrame = pd.read_csv(RawMeas, delimiter = ",",error_bad_lines=False,header=None,
                            usecols=range(1,len(colNames)),names= colNames,
                            encoding = 'utf-8-sig',na_values = ["NULL",""],engine ='c')
    return dataFrame

def BasicInfo(AndroidData):
    listOfSV = AndroidData.Svid.unique()
    listOfConstelations = AndroidData.ConstellationType.unique()
    GNSS_Constelations = {1:'GPS',2:'SBAS',3:'GLONASS',4:'QZSS',5:'BeiDou',6:'Galileo'}

    print('Observing the following SVs:{}\nusing following constelations: {}\n'.format(
          ','.join(map(str,listOfSV)),','.join([GNSS_Constelations[s] for s in listOfConstelations])))

def covertGPSTimetoUTC(GPSseconds,GPSWeek,UTC_offset=18):
    GPS0 = datetime.datetime(1980, 1, 6)
    weekID = datetime.timedelta(weeks =int(GPSWeek))
    weekSeconds = datetime.timedelta(seconds = round(GPSseconds)) 
    
    #round to nearest due to clock drift
    UTC_offset= datetime.timedelta(seconds = UTC_offset) 
    
    #so time is
    epoch = GPS0 + weekID +  weekSeconds - UTC_offset
    return epoch


def main():
    data_file = './data/s8_01.txt'
    df_GNSS = readGNSSLogger(data_file)

    # Cut noisy start of data...
    df_GNSS = df_GNSS[1500:]
    # print(df_GNSS)
    # BasicInfo(df_GNSS)

    # get only GPS
    # 일단은 어려워서 노답이니 GPS만
    df_GPS = df_GNSS[df_GNSS.ConstellationType==1]
    BasicInfo(df_GPS)


    # calculate pseudorange

    #we operate in [ns=1e-9] so as [100milisec=decsec=1e-1] [decsec]=1e8[ns]
    GNSS_const = {'totalWeekSecs':7*24*3600,
                'lightSpeed':299792458,
                'ds':1e8,
                'GPSday':24*60*60,
                'GPSweek':7*24*60*60 
    } #constants
    
    # get TOW
    GPSWeek = (-df_GPS.FullBiasNanos * 1e-9 / GNSS_const['totalWeekSecs']).astype('int')
    print('GPS week {} '.format(GPSWeek.unique()[0]))

    # calculate tRx and tTx
    tRx_ns = df_GPS.TimeNanos + df_GPS.TimeOffsetNanos - df_GPS.FullBiasNanos.iloc[0] - (GPSWeek * GNSS_const['totalWeekSecs'] * 1e9)
    tTx_ns = df_GPS.ReceivedSvTimeNanos

    # Option 1
    PR_m = (tRx_ns - tTx_ns) * GNSS_const['lightSpeed'] * 1e-9
    print(PR_m.head(10))

    print('====================================================')
    # Option 2 (Paolo Crosta 방식)
    PR_m = (tRx_ns % GNSS_const['ds'] - tTx_ns % GNSS_const['ds']) * GNSS_const['lightSpeed']*1e-9
    print(PR_m.head(10))

    # estimating epoch of observation
    listOfSV = df_GPS.Svid.unique() #all SV that I see during the period
    allRxSec = tRx_ns.values * 1e-9
    GPSepochs = allRxSec[0::len(listOfSV)]
    
    print("Our time is corrupted by clock drift so we can't use it directly, for ex:") 
    for epoch in GPSepochs[:5]:
        print("{TOW:f}".format(TOW=epoch))
    '''
    Instead we want to round our observations to the nearest epoch. This will only work for 
    2.PR generation as we disregard running clock. For the 1. we need to calculate offset and 
    add it to our clocks by hand.

    To demonstrate 2. approach lets first demonstrate how the observation should be tagged internally,
    that is in week and seconds of week, that is:
    '''
    for epoch in GPSepochs[:5]:
        print("{GPSweek}\t{TOW:.0f}".format(GPSweek=GPSWeek.values[1] ,TOW=epoch))
    
    print("\n\nFor display purpose (user) we present time in UT, that is:")
    
    for epoch in GPSepochs[:5]:
        print(covertGPSTimetoUTC(epoch,GPSWeek.values[1]))

    df_PR = pd.DataFrame({'epoch' : allRxSec, 'SV_ID' : df_GPS.Svid,'PR': PR_m})
    print(df_PR)
    df_PR = df_PR[df_PR.SV_ID !=2]  # outlier 날림
    print(df_PR)

    SV_ranges = df_PR.pivot(index='epoch',columns='SV_ID', values='PR')
    SV_ranges.plot(figsize=[15,5])

    plt.show()

if __name__ == "__main__":
    main()

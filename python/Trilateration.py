import math
import numpy as np

def getTrilateration(dist_line):
    
    earthR = 6371

    LatA = float(dist_line[0]) 
    LonA = float(dist_line[1]) 
    DistA = float(dist_line[2]) / 1000

    LatB = float(dist_line[3]) 
    LonB = float(dist_line[4]) 
    DistB = float(dist_line[5]) / 1000

    LatC = float(dist_line[6]) 
    LonC = float(dist_line[7]) 
    DistC = float(dist_line[8]) / 1000

    xA = earthR * (math.cos(math.radians(LatA)) * math.cos(math.radians(LonA)))
    yA = earthR * (math.cos(math.radians(LatA)) * math.sin(math.radians(LonA)))
    zA = earthR * (math.sin(math.radians(LatA)))

    xB = earthR * (math.cos(math.radians(LatB)) * math.cos(math.radians(LonB)))
    yB = earthR * (math.cos(math.radians(LatB)) * math.sin(math.radians(LonB)))
    zB = earthR * (math.sin(math.radians(LatB)))

    xC = earthR * (math.cos(math.radians(LatC)) * math.cos(math.radians(LonC)))
    yC = earthR * (math.cos(math.radians(LatC)) * math.sin(math.radians(LonC)))
    zC = earthR * (math.sin(math.radians(LatC)))

    P1 = np.array([xA, yA, zA])
    P2 = np.array([xB, yB, zB])
    P3 = np.array([xC, yC, zC])

    ex = (P2 - P1)/(np.linalg.norm(P2 - P1))
    i = np.dot(ex, P3 - P1)
    ey = (P3 - P1 - i * ex) / (np.linalg.norm(P3 - P1 - i*ex))
    ez = np.cross(ex, ey)
    d = np.linalg.norm(P2 - P1)
    j = np.dot(ey, P3 - P1)

    x = (math.pow(DistA, 2) - math.pow(DistB, 2) + math.pow(d, 2))/(2*d)
    y = ((math.pow(DistA, 2) - math.pow(DistC, 2) + math.pow(i, 2) + math.pow(j, 2))/(2*j)) - ((i/j)*x)

    print(DistA)
    print(x,y)

    print(math.pow(DistC, 2))
    print(math.pow(x, 2))
    print(math.pow(y, 2))

    print(math.pow(DistA, 2) - math.pow(x, 2) - math.pow(y, 2))

    z = math.sqrt(math.pow(DistA, 2) - math.pow(x, 2) - math.pow(y, 2))
    # try:
    #     z = math.sqrt(math.pow(DistA, 2) - math.pow(x, 2) - math.pow(y, 2))
    # except:
    #     z = float('nan')

    triPt = P1 + x*ex + y*ey + z*ez

    lat = math.degrees(math.asin(triPt[2] / earthR))
    lon = math.degrees(math.atan2(triPt[1], triPt[0]))

    return (lat, lon)


if __name__ == "__main__":
    data = '''20688659.31056547, 13228194.67186006, 2.852263e+07,
            16149940.2941784,  20858246.08163256, 2.332815e+07,
            12346096.19203435, 20694124.44252037, 2.069359e+07'''

    print(getTrilateration(data.split(',')))


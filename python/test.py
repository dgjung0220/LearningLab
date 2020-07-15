from laika.lib.coordinates import ecef2geodetic, geodetic2ecef
import numpy as np
import seaborn as sns

from laika import AstroDog
constellations = ['GPS']
dog = AstroDog(valid_const=constellations)

from datetime import datetime
from laika.gps_time import GPSTime

time = GPSTime(1946, 222803)
sat_prn = 'G02'
sat_pos, sat_vel, sat_clock_err, sat_clock_drift = dog.get_sat_info(sat_prn, time)
print("Sattelite's position in ecef(m) : \n", sat_pos, '\n')
print("Sattelite's velocity in ecef(m/s) : \n", sat_vel, '\n')
print("Sattelite's clock error(s) : \n", sat_clock_err, '\n\n')

time = GPSTime(1946, 222804)
sat_prn = 'G06'
sat_pos, sat_vel, sat_clock_err, sat_clock_drift = dog.get_sat_info(sat_prn, time)
print("Sattelite's position in ecef(m) : \n", sat_pos, '\n')
print("Sattelite's velocity in ecef(m/s) : \n", sat_vel, '\n')
print("Sattelite's clock error(s) : \n", sat_clock_err, '\n\n')

time = GPSTime(1946, 222805)
sat_prn = 'G12'
sat_pos, sat_vel, sat_clock_err, sat_clock_drift = dog.get_sat_info(sat_prn, time)
print("Sattelite's position in ecef(m) : \n", sat_pos, '\n')
print("Sattelite's velocity in ecef(m/s) : \n", sat_vel, '\n')
print("Sattelite's clock error(s) : \n", sat_clock_err, '\n\n')
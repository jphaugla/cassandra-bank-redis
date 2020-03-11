#!/usr/local/bin/python3

import redis
import pprint

try:
  r = redis.StrictRedis(
    decode_responses=True,
    host='redis',
    port=6379
    )

  for key in r.scan_iter():
       print(key)

except Exception as err:
  print("Error connecting to Redis: {}".format(err))

#!/usr/local/bin/python3

import redis

from redisearch import Client


def list_indexes():
    try:
        r = redis.StrictRedis(
            decode_responses=True,
            host='redis',
            port=6379
        )

        cursor = '0'
        returnSet = set()
        while cursor != 0:
            cursor, keys = r.scan(cursor, match='idx:*', count=10000)
            for i in keys:
                docList = i.split("{")
                keyName=docList[0]
                indexName=keyName.split(":")
                returnSet.add(indexName[1])
        # print(returnSet)
        return returnSet

    except Exception as err:
        print("Error connecting to Redis: {}".format(err))


def index_size(index_name):
    # Creating a client with a given index name
    # print("in index_size index_name=" + index_name)
    client = Client(index_name,'redis',6379)
    all_info = client.info()
    # print(all_info)
    return all_info


def main():
    print("starting main")
    total_index_size_mb = 0.0
    for index_name in list_indexes():
        index_info = index_size(index_name)
        index_mb = index_info.get('inverted_sz_mb')
        print("index=" + index_name + ", size in mb=" + index_mb)
        total_index_size_mb = float(index_mb) + total_index_size_mb
    print("total index size=" + str(total_index_size_mb))



if '__main__' == __name__:
    main()

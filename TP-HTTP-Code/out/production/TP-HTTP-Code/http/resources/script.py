#!/usr/bin/python

import sys
if len(sys.argv) != 2:
    print("400 Bad Request\n")
    print("Wrong number of argument for the script\n")
else:
	# first param is the name of the file

    nbIteration = int(sys.argv[1])
    if nbIteration < 0:
        print("400 Bad Request")
        print("Argument shoud not be negative")
    else:
        print("200 OK\n")
        for i in range(nbIteration):
            print("Hello World")

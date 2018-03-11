#!python3
"""
Opens the pmap file and generates a table with
powers of two ranges (ex. 4096, 8192...) and counts
the number of segments in each range. Saves a CSV file
with the results
"""

import sys

def make_histogram(input_file, results_file):
    '''
    reads the file and make the histogram.
    '''
    histogram = {}
    entries = []
    with open(input_file, 'r') as lines:
        print('Reading pmap file...')
        for line in lines:
            entries = line.split(',')
            try:
                segsize = int(entries[3])
                power = 2048
                while power < segsize:
                    power = power * 2
                try:
                    histogram[power] = histogram[power] + 1
                except KeyError:
                    histogram[power] = 1
                    print('Entry for {}k created.'.format(int(power / 1024)))
            except ValueError:
                print('Ignoring line "%s"', line)
    savecsv(histogram, results_file)

def savecsv(histogram, results_file):
    '''
    Saves the histogram of segment sizes
    '''
    print('Saving results...')
    file = open(results_file, 'w')
    for key in sorted(histogram):
        kbyte = int(key / 1024)
        line = '{}k, {}\n'.format(kbyte, histogram[key])
        print(line)
        file.write(line)
    file.close()
    print('Done!')

if __name__ == "__main__":
    print('Histogram maker (pmap) v0.1')
    if len(sys.argv) < 3:
        print('Usage: python3 histogram-pmap.py <pmap file> <output file>\n')
    else:
        try:
            make_histogram(sys.argv[1], sys.argv[2])
        except FileNotFoundError as error:
            print('File not found! Error: {}'.format(error))
        except PermissionError as error:
            print('Cannot create file {}. Error: {}'.format(sys.argv[2], error))
        except OSError as error:
            print('Unexpected OS error: {}'.format(error))

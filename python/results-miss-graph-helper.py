"""
Simple program to transform the results files into a more graph friendly csv file
in order to compare TLB and SB misses over time.
"""
import csv
import sys


def parse_results(results_file, graph_file):
    with open(results_file, 'r') as working_file:
        with open(graph_file, 'w') as output_file:
            fieldnames = ['TLB 128 total assoc miss', 'SB 128 total assoc miss']
            writer = csv.writer(output_file, delimiter=';', lineterminator='\n')
            writer.writerow(fieldnames)
            reader = csv.DictReader(working_file, delimiter=';', lineterminator='\n')
            buffer_type_pointer = 0
            tlb_miss_value = 0
            sb_miss_value = 0
            for row in reader:
                buffer_type_pointer += 1
                if buffer_type_pointer == 3:
                    tlb_miss_value = int(row['miss'])
                elif buffer_type_pointer == 7:
                    sb_miss_value = int(row['miss'])
                elif buffer_type_pointer == 8:
                    buffer_type_pointer = 0
                    writer.writerow([tlb_miss_value, sb_miss_value])
    print('Done!')


if __name__ == "__main__":
    print('Results Miss Graph Maker (to plot a graph!!!) v0.1')
    if len(sys.argv) < 3:
        print('Usage: python3 results-miss-graph-helper.py <results CSV file> <graph CSV file>\n')
    else:
        try:
            parse_results(sys.argv[1], sys.argv[2])
        except FileNotFoundError as error:
            print('File not found! Error: {}'.format(error))
        except PermissionError as error:
            print('Cannot create file {}. Error: {}'.format(sys.argv[2], error))
        except OSError as error:
            print('Unexpected OS error: {}'.format(error))
